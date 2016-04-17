package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {

    private SharedPreferences mPrefs;

    private TextView tvSafePhone;
    private ImageView ivLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("config", MODE_PRIVATE);

        boolean configed = mPrefs.getBoolean("configed", false);//判断是否进入过设置向导
        if (configed) {
            setContentView(R.layout.activity_lost_find);

            // 根据sp更新安全号码
            tvSafePhone = (TextView) findViewById(R.id.tv_safe_phone);
            String phone = mPrefs.getString("safe_phone","");
            tvSafePhone.setText(phone);

            // 根据sp更新保护锁
            ivLock = (ImageView) findViewById(R.id.iv_lock);
            boolean protect = mPrefs.getBoolean("protect",false);
            if(protect){
                ivLock.setImageResource(R.drawable.lock);
            }else{
                ivLock.setImageResource(R.drawable.unlock);
            }

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
