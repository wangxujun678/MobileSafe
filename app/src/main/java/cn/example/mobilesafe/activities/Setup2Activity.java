package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Setup2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
    }


    public void next(View v) {
        startActivity(new Intent(this, Setup3Activity.class));
        finish();
        //设置activity切换动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    public void previous(View v) {
        startActivity(new Intent(this, Setup1Activity.class));
        finish();

        overridePendingTransition(R.anim.previous_tran_in, R.anim.previous_tran_out);
    }
}
