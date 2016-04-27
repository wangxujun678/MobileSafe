package cn.example.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.example.mobilesafe.bean.AppInfo;

/**
 * Created by Administrator on 2016/4/23.
 */
public class AppInfos {

    public static List<AppInfo> getAppInfos(Context context){

        List<AppInfo> packageAppInfos = new ArrayList<AppInfo>();

        PackageManager pm = context.getPackageManager();//获取到包的管理者
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);//获取到已安装的包的信息

        for (PackageInfo installedPackage:installedPackages){

            AppInfo appInfo = new AppInfo();

            Drawable drawable = installedPackage.applicationInfo.loadIcon(pm);//获取应用图标
            appInfo.setIcon(drawable);

            String apkName  = installedPackage.applicationInfo.loadLabel(pm).toString();//获取应用名字
            appInfo.setApkName(apkName);

            String sourceDir = installedPackage.applicationInfo.sourceDir;//获取apk资源的路径
            File file = new File(sourceDir);
            long apkSize = file.length();//获取应用大小，此时还没格式化
            appInfo.setApkSize(apkSize);

            int flags = installedPackage.applicationInfo.flags;//获取到安装应用程序的标记
            //System.out.println(flags);
            if ((flags & ApplicationInfo.FLAG_SYSTEM)!=0){//表明是系统应用
                appInfo.setUserApp(false);
            }else{
                appInfo.setUserApp(true);
            }
            if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0){//表明在sd卡中
                appInfo.setRom(false);
            }else{
                appInfo.setRom(true);
            }

            String packageName = installedPackage.packageName;//获取应用包名
            appInfo.setApkPackageName(packageName);

            packageAppInfos.add(appInfo);
        }
        return packageAppInfos;
    }

}
