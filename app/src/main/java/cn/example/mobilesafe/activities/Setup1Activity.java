package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * 第一个设置向导页
 */
public class Setup1Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    public void next(View v) {
        startActivity(new Intent(this, Setup2Activity.class));
        finish();
        //设置activity切换动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }
}
