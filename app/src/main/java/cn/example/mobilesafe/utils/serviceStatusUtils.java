package cn.example.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * service状态工具
 */
public class serviceStatusUtils {

    /**
     * 检测服务是否在运行
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context context,String serviceName){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100);// 获取系统所有正在运行的服务,最多返回100个
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices){
            String className = runningServiceInfo.service.getClassName();//获取服务的名称
            if (className.equals(serviceName)){//服务存在
                return true;
            }
        }
        return false;
    }
}
