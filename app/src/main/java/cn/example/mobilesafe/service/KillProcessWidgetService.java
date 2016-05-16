package cn.example.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import java.util.Timer;
import java.util.TimerTask;

import cn.example.mobilesafe.activities.R;
import cn.example.mobilesafe.receiver.MyAppWidget;
import cn.example.mobilesafe.utils.SystemInfoUtils;

/**
 * Created by Administrator on 2016/4/28.
 */
public class KillProcessWidgetService extends Service {

    private AppWidgetManager appWidgetManager;
    private Timer timer;
    private TimerTask timerTask;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //桌面小控件的管理者
        appWidgetManager = AppWidgetManager.getInstance(this);
        //每隔5秒钟更新一次桌面
        //初始化定时器
        timer = new Timer();
        //初始化一个定时任务
        timerTask = new TimerTask() {
            @Override
            public void run() {
                //初始化一个远程的view,把当前的布局文件添加进行
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);

                //给桌面控件设置文字
                int processCount = SystemInfoUtils.getProcessCount(KillProcessWidgetService.this);
                views.setTextViewText(R.id.process_count,"正在运行的软件:" +String.valueOf(processCount));

                long availMem = SystemInfoUtils.getAvailMem(KillProcessWidgetService.this);
                views.setTextViewText(R.id.process_memory,"可用内存:" +
                        Formatter.formatFileSize(KillProcessWidgetService.this,availMem));

                //给桌面控件的按钮设置点击监听
                Intent intent = new Intent();
                intent.setAction("cn.example.mobilesafe.receiver.KillAllProcessReceiver");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(KillProcessWidgetService.this,0,intent,0);
                views.setOnClickPendingIntent(R.id.btn_clear,pendingIntent);


                ComponentName provider = new ComponentName(getApplicationContext(), MyAppWidget.class);
                //更新桌面
                appWidgetManager.updateAppWidget(provider,views);
            }
        };

        //从0开始。每隔5秒钟更新一次
        timer.schedule(timerTask,0,5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //优化代码
        if(timer != null || timerTask != null){
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
        }
    }
}
