package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class Setup4Activity extends Activity {

    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        mPref = getSharedPreferences("config", MODE_PRIVATE);
    }


    public void next(View v) {
        startActivity(new Intent(this, LostFindActivity.class));
        finish();

        //设置activity切换动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);

        mPref.edit().putBoolean("configed", true).commit();
    }

    public void previous(View v) {
        startActivity(new Intent(this, Setup3Activity.class));
        finish();

        overridePendingTransition(R.anim.previous_tran_in, R.anim.previous_tran_out);
    }
}
