package cn.example.mobilesafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Administrator on 2016/4/28.
 */
public class KillAllProcessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //得到手机上面正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcesses  = activityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
            //杀死所有的进程
            activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
        }
        Toast.makeText(context, "清理完毕", Toast.LENGTH_SHORT).show();
    }
}
