package cn.example.mobilesafe.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/4/27.
 */
public class UIUtils {
    /**
     * 可以在UI线程和子线程中弹出toast
     * @param context
     * @param msg
     */
    public static void showToast(final Activity context,final String msg){
        if("main".equals(Thread.currentThread().getName())){
            Toast.makeText(context, msg, 1).show();
        }else{
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, msg, 1).show();
                }
            });
        }
    }
}
