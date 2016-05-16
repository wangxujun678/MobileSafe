package cn.example.mobilesafe.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import cn.example.mobilesafe.service.KillProcessWidgetService;

/**
 * Created by Administrator on 2016/4/28.
 */
public class MyAppWidget extends AppWidgetProvider {



    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        Intent intent = new Intent(context, KillProcessWidgetService.class);
        context.startService(intent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        Intent intent = new Intent(context, KillProcessWidgetService.class);
        context.stopService(intent);
    }
}
