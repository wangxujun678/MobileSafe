package cn.example.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import cn.example.mobilesafe.activities.R;
import cn.example.mobilesafe.service.LocationService;

/**
 * Created by Administrator on 2016/4/17.
 */
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        /**
         * 短信最多140字节,超出的话,会分为多条短信发送,所以是一个数组
         * 因为我们的短信指令很短,所以for循环只执行一次
         * pdus是固定的SMSpdus数组，sdk里看不到，在源代码里
         */
        Object[] objects = (Object[]) intent.getExtras().get("pdus");
        for (Object object : objects) {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
            String originatingAddress = message.getOriginatingAddress();//获取短信来源的手机号
            String messageBody = message.getMessageBody();

//            System.out.println(originatingAddress + ":" + messageBody);

            if ("#*alarm*#".equals(messageBody)) {
                // 播放报警音乐, 即使手机调为静音,也能播放音乐, 因为使用的是媒体声音的通道,和铃声无关
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setVolume(1f, 1f);
                player.setLooping(true);
                player.start();

                abortBroadcast();// 中断短信的传递, 从而系统短信app就收不到内容了
            } else if ("#*location*#".equals(messageBody)) {
                System.out.println("获取定位中...再次发送即可获得最新定位！");

                context.startService(new Intent(context, LocationService.class));// 开启定位服务

                /*
                //此处可能需要能一下才能读到SharedPreferences里的location数据
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                */

                SharedPreferences sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
                String location = sp.getString("location", "");

                System.out.println(location);

                // 发送短信给安全号码
                String phone = sp.getString("safe_phone", "");// 读取安全号码
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, "last location:" + location, null, null);

                abortBroadcast();
            } else if ("#*wipedata*#".equals(messageBody)) {
                //获取DevicePolicyManager
                //创建ComponentName设备管理组件，这一步需要写一个AdminReceiver继承DeviceAdminReceiver
                //激活设备管理器, 也可以在设置->安全->设备管理器中手动激活
                //调用DevicePolicyManager的清除数据和锁屏的方法
                System.out.println("远程清除数据");
                abortBroadcast();
            } else if ("#*lockscreen*#".equals(messageBody)) {
                System.out.println("远程锁屏");
                abortBroadcast();
            }

        }
    }
}
