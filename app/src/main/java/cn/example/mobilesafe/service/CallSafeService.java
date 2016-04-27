package cn.example.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import cn.example.mobilesafe.db.dao.BlackNumberDao;

/**
 * 拦截短信和电话的服务
 * 只拦截了短信，拦截电话还没完成
 */
public class CallSafeService extends Service{

    private BlackNumberDao dao;
    private InnerReceiver innerReceiver;
    private TelephonyManager tm;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dao = new BlackNumberDao(this);

        //获取到系统的电话服务
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        //监听电话状态
        tm.listen(new MyPhoneStateListener(),PhoneStateListener.LISTEN_CALL_STATE);

        innerReceiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(innerReceiver,filter);
    }

    private class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
//            * @see TelephonyManager#CALL_STATE_IDLE  电话闲置
//            * @see TelephonyManager#CALL_STATE_RINGING 电话铃响的状态
//            * @see TelephonyManager#CALL_STATE_OFFHOOK 电话接通
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    String mode = dao.findNumberMode(incomingNumber);
                    if (mode.equals("1")||mode.equals("2")){
                        Uri uri = Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri,true,new MyContentObserver(new Handler(),incomingNumber));
                        //挂断电话
                        endCall();
                    }
                    break;
            }
        }
    }

    private void endCall() {
        try {
            //通过类加载器加载ServiceManager
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
            //通过反射得到当前的方法
            Method method = clazz.getDeclaredMethod("getService", String.class);

            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);

            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 内容观察者，如果是黑名单号码来电，自动挂断后会删除通话记录
     */
    private class MyContentObserver extends ContentObserver{

        String incomingNumber;
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler,String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            getContentResolver().unregisterContentObserver(this);
            deleteCallLog(incomingNumber);
            super.onChange(selfChange);
        }
    }

    private void deleteCallLog(String incomingNumber) {

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
