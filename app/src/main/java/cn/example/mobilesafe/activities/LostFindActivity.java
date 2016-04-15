package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class LostFindActivity extends Activity {

    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("config", MODE_PRIVATE);

        boolean configed = mPrefs.getBoolean("configed", false);
        if (configed) {
            setContentView(R.layout.activity_lost_find);
        } else {
            startActivity(new Intent(LostFindActivity.this, Setup1Activity.class));
            finish();
        }
    }

    /**
     * 重新进入设置向导
     */
    public void reEnterSetup1(View v){
        startActivity(new Intent(this, Setup1Activity.class));
        finish();
    }
}
