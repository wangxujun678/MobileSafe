package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import cn.example.mobilesafe.view.SettingItemView;

public class SettingActivity extends Activity {

    private SettingItemView sivUpdate;
    private SharedPreferences mPref;//用来保存设置页面选项的勾选状态
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mPref = getSharedPreferences("config", MODE_PRIVATE);
        sivUpdate = (SettingItemView) findViewById(R.id.siv_update);

        //初始化勾选状态
        boolean autoUpdate = mPref.getBoolean("auto_update", true);
        if(autoUpdate){
            sivUpdate.setChecked(true);//同时还会更新文本描述
        }else{
            sivUpdate.setChecked(false);
        }

        sivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sivUpdate.isChecked()){
                    sivUpdate.setChecked(false);
                    mPref.edit().putBoolean("auto_update", false).commit();
                }else{
                    sivUpdate.setChecked(true);
                    mPref.edit().putBoolean("auto_update", true).commit();
                }
            }
        });
    }

}
