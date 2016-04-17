package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {

    public SharedPreferences mPref;
    private GestureDetector mDetector;//手势识别器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPref = getSharedPreferences("config", MODE_PRIVATE);
        mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            /**
             * 监听手势滑动事件
             * e1:滑动的起点 e2:滑动终点 velocityX:水平速度 velocityY:垂直速度
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                //判断纵向滑动幅度是否过大，过大的话不允许切换画面
                if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
                    return true;
                }

                //判断滑动速度是否过慢
                if (Math.abs(velocityX) < 100) {
                    return true;
                }

                //向右划，上一页
                if (e2.getRawX() - e1.getRawX() > 200) {
                    showPreviousPage();
                    return true;
                }
                //向左滑，下一页
                if (e1.getRawX() - e2.getRawX() > 200) {
                    showNextPage();
                    return true;
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    /**
     * 展示下一页, 子类必须实现
     */
    public abstract void showPreviousPage();

    /**
     * 展示上一页, 子类必须实现
     */
    public abstract void showNextPage();

    // 点击下一页按钮
    public void next(View v){
        showNextPage();
    }

    // 点击上一页按钮
    public void previous(View v){
        showPreviousPage();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);// 委托手势识别器处理触摸事件
        return super.onTouchEvent(event);
    }
}
