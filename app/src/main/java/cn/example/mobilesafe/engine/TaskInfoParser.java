package cn.example.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import java.util.ArrayList;
import java.util.List;

import cn.example.mobilesafe.activities.R;
import cn.example.mobilesafe.bean.TaskInfo;

/**
 * 获取手机进程信息
 * Created by Administrator on 2016/4/27.
 */
public class TaskInfoParser {

    public static List<TaskInfo> getTaskInfos(Context context) {

        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
        PackageManager packageManager = context.getPackageManager();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取到手机上面所有运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            TaskInfo taskInfo = new TaskInfo();

            //获取进程名字(包名)
            String processName = info.processName;
            taskInfo.setPackageName(processName);

            //根据进程的端口号获取进程占用的内存信息
            Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
            int totalPrivateDirty = memoryInfo[0].getTotalPrivateDirty();
            taskInfo.setMemorySize(totalPrivateDirty);

            try {
                //获取应用图标
                PackageInfo packageInfo = packageManager.getPackageInfo(processName, 0);
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                taskInfo.setIcon(icon);

                //获取应用名字
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                taskInfo.setAppName(appName);

                //获取当前应用程序的标记
                int flags = packageInfo.applicationInfo.flags;
                if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    taskInfo.setUserApp(false);
                } else {
                    taskInfo.setUserApp(true);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                // 系统核心库里面有些系统没有图标。必须给一个默认的图标
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
                taskInfo.setAppName(processName);
            }
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
