package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);

        mPref = getSharedPreferences("config", MODE_PRIVATE);

        tvTop = (TextView) findViewById(R.id.tv_top);
        tvBottom = (TextView) findViewById(R.id.tv_bottom);
        ivDrag = (ImageView) findViewById(R.id.iv_drag);

        //系统绘制组件的过程 onMeasure(测量view), onLayout(安放位置), onDraw(绘制)
        int lastX = mPref.getInt("lastX", 0);
        int lastY = mPref.getInt("lastY", 0);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivDrag.getLayoutParams();// 获取布局对象
        params.leftMargin =lastX;// 设置左边距
        params.topMargin = lastY;//设置上边距
        ivDrag.setLayoutParams(params);

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
                        // 更新界面
                        ivDrag.layout(l, t, r, b);
                        // 重新初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        mPref.edit().putInt("lastX",startX).commit();
                        mPref.edit().putInt("lastY",startY).commit();
                        break;
                }
                return true;
            }
        });
    }


}
