package cn.example.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;

/**
 * Created by Administrator on 2016/4/27.
 */
public class KillProcessService extends Service{

    private LockScreenReceiver receiver;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class LockScreenReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取进程管理器
            ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            //获取到手机上面所有正在运行的进程
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo info:runningAppProcesses){
                am.killBackgroundProcesses(info.processName);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new LockScreenReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        //注册一个锁屏的广播
        registerReceiver(receiver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
        receiver = null;//手动回收
    }
}
