package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DragViewActivity extends Activity {

    private TextView tvTop;
    private TextView tvBottom;
    private ImageView ivDrag;

    private int startX;
    private int startY;

    private SharedPreferences mPref;

    long[] mHits = new long[2];//数组长度表示要点击的次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);

        mPref = getSharedPreferences("config", MODE_PRIVATE);

        tvTop = (TextView) findViewById(R.id.tv_top);
        tvBottom = (TextView) findViewById(R.id.tv_bottom);
        ivDrag = (ImageView) findViewById(R.id.iv_drag);

        //获取屏幕宽高
        final int winWidth = getWindowManager().getDefaultDisplay().getWidth();
        final int winHeight = getWindowManager().getDefaultDisplay().getHeight();

        //系统绘制组件的过程 onMeasure(测量view), onLayout(安放位置), onDraw(绘制)
        int lastX = mPref.getInt("lastX", 0);
        int lastY = mPref.getInt("lastY", 0);

        //根据图片位置，决定提示框显示和隐藏
        if (lastY > winHeight / 2) {
            tvTop.setVisibility(View.VISIBLE);
            tvBottom.setVisibility(View.INVISIBLE);
        } else {
            tvTop.setVisibility(View.INVISIBLE);
            tvBottom.setVisibility(View.VISIBLE);
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivDrag.getLayoutParams();// 获取布局对象
        params.leftMargin = lastX;// 设置左边距
        params.topMargin = lastY;//设置上边距
        ivDrag.setLayoutParams(params);// 重新设置位置

        ivDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();//将开机后开始计算的时间赋给数组最后一位
                if (mHits[0] >= SystemClock.uptimeMillis() - 500) {//连击完成的时间在500毫秒内
                    // 把图片居中
                    ivDrag.layout(winWidth / 2 - ivDrag.getWidth() / 2, winHeight / 2 - ivDrag.getHeight() / 2,
                            winWidth / 2 + ivDrag.getWidth() / 2, winHeight / 2 + ivDrag.getHeight() / 2);
                }
            }
        });

        ivDrag.setOnTouchListener(new View.OnTouchListener() {
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

                        // 更新左上右下距离
                        int l = ivDrag.getLeft() + dX;
                        int t = ivDrag.getTop() + dY;
                        int r = ivDrag.getRight() + dX;
                        int b = ivDrag.getBottom() + dY;

                        // 判断是否超出屏幕边界, 注意状态栏的高度,这里直接给20个dp
                        if (l < 0 || t < 0 || r > winWidth || b > winHeight - 20) {
                            break;
                        }

                        //根据图片位置，决定提示框显示和隐藏
                        if (t > winHeight / 2) {
                            tvTop.setVisibility(View.VISIBLE);
                            tvBottom.setVisibility(View.INVISIBLE);
                        } else {
                            tvTop.setVisibility(View.INVISIBLE);
                            tvBottom.setVisibility(View.VISIBLE);
                        }
                        // 更新界面
                        ivDrag.layout(l, t, r, b);
                        // 重新初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        mPref.edit().putInt("lastX", ivDrag.getLeft()).commit();
                        mPref.edit().putInt("lastY", ivDrag.getTop()).commit();
                        break;
                }
                return false;//事件要向下传递,让onclick(双击事件)可以响应
            }
        });
    }


}
