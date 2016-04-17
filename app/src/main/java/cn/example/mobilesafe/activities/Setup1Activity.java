package cn.example.mobilesafe.activities;

import android.content.Intent;
import android.os.Bundle;

/**
 * 第一个设置向导页
 */
public class Setup1Activity extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void showPreviousPage() {

    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(this, Setup2Activity.class));
        finish();
        //设置activity切换动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

}
