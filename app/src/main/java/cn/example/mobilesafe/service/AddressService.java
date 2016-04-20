package cn.example.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import cn.example.mobilesafe.activities.R;
import cn.example.mobilesafe.db.dao.AddressDao;

public class AddressService extends Service {

    SharedPreferences mPref;
    TelephonyManager tm;
    MyListener listener;
    BroadcastReceiver receiver;

    WindowManager mWM;
    private WindowManager.LayoutParams params;
    View view;
    private int startX;
    private int startY;
    private int winWidth;
    private int winHeight;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        receiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver, filter);//注册广播
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);//停止来电监听
        unregisterReceiver(receiver);//注销广播
    }

    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    System.out.println("电话铃响");
                    String address = AddressDao.getAddress(incomingNumber);
                    //Toast.makeText(AddressService.this,address,Toast.LENGTH_LONG).show();
                    showToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:// 电话闲置状态
                    if (mWM != null && view != null) {
                        mWM.removeView(view);// 从window中移除view
                        view = null;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 监听去电的广播接收者，需要权限 android.permission.PROCESS_OUTGOING_CALLS
     */
    class OutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();// 获取去电电话号码
            String address = AddressDao.getAddress(number);
            //Toast.makeText(context,address,Toast.LENGTH_LONG).show();
            showToast(address);
        }
    }

    /**
     * 自定义归属地浮窗
     *
     * @param text
     */
    private void showToast(String text) {
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);//获取窗口管理器

        // 获取屏幕宽高
        winWidth = mWM.getDefaultDisplay().getWidth();
        winHeight = mWM.getDefaultDisplay().getHeight();

        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;// 电话窗口。它用于电话交互（特别是呼入）。它置于所有应用程序之上，状态栏之下。
        params.gravity = Gravity.LEFT + Gravity.TOP;// 将重心位置设置为左上方,
        // 也就是(0,0)从左上方开始,而不是默认的中心位置
        // 设置浮窗的位置, 基于左上方的偏移量
        params.x = mPref.getInt("lastX", 0);
        params.y = mPref.getInt("lastY", 0);

        view = View.inflate(this, R.layout.toast_address, null);

        int[] bgs = new int[]{R.drawable.call_locate_white,
                R.drawable.call_locate_orange, R.drawable.call_locate_blue,
                R.drawable.call_locate_gray, R.drawable.call_locate_green};
        int style = mPref.getInt("address_style", 0);
        view.setBackgroundResource(bgs[style]);// 根据存储的样式更新背景

        TextView tvText = (TextView) view.findViewById(R.id.tv_number);
        tvText.setText(text);

        mWM.addView(view, params);//在窗口中显示自定义控件需要两个元素：View和WindowManager.LayoutParams
        //可以在任意应用中显示，类似于Toast

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();
                        //计算偏移量
                        int dX = endX - startX;
                        int dY = endY - startY;

                        // 更新浮窗位置
                        params.x += dX;
                        params.y += dY;

                        // 防止坐标偏离屏幕
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > winWidth-view.getWidth()) {
                            params.x = winWidth-view.getWidth();
                        }
                        if (params.y > winHeight-view.getHeight()) {
                            params.y = winHeight-view.getHeight();
                        }

                        mWM.updateViewLayout(view, params);

                        // 重新初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        mPref.edit().putInt("lastX", startX).commit();
                        mPref.edit().putInt("lastY", startY).commit();
                        break;
                }
                return true;
            }
        });
    }
}
