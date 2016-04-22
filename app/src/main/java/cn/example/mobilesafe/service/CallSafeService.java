package cn.example.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;

import cn.example.mobilesafe.db.dao.BlackNumberDao;

/**
 * 拦截短信和电话的服务
 * 只拦截了短信，拦截电话还没完成
 */
public class CallSafeService extends Service{

    private BlackNumberDao dao;
    private InnerReceiver innerReceiver;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dao = new BlackNumberDao(this);
        innerReceiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(innerReceiver,filter);
    }

    private class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            for (Object object:objects){
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = message.getOriginatingAddress();
                String messageBody = message.getMessageBody();
                //通过短信的电话号码查询拦截的模式
                String numberMode = dao.findNumberMode(originatingAddress);
                if (numberMode.equals("1")||numberMode.equals("3")){
                    abortBroadcast();
                }

                //智能拦截模式 发票  你的头发漂亮 分词
//                if(messageBody.contains("fapiao")){
//                    abortBroadcast();
//                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(innerReceiver);
    }
}
