package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import cn.example.mobilesafe.service.KillProcessService;
import cn.example.mobilesafe.utils.SystemInfoUtils;

public class TaskManagerSettingActivity extends Activity {

    private CheckBox cbShowSystemProcess;
    private CheckBox cbLockKillProcess;
    private SharedPreferences mPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager_setting);

        initUI();
    }

    private void initUI() {

        cbShowSystemProcess = (CheckBox) findViewById(R.id.cb_show_system_process);
        cbLockKillProcess = (CheckBox) findViewById(R.id.cb_lock_kill_process);
        mPref = getSharedPreferences("config",MODE_PRIVATE);

        cbShowSystemProcess.setChecked(mPref.getBoolean("show_system",false));

        if (SystemInfoUtils.isServiceRunning(TaskManagerSettingActivity.this,"cn.example.mobilesafe.service.KillProcessService")){
            cbLockKillProcess.setChecked(true);
        }else{
            cbLockKillProcess.setChecked(false);
        }

        cbShowSystemProcess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPref.edit().putBoolean("show_system",isChecked).commit();
            }
        });

        //定时清理进程
        final Intent intent = new Intent(TaskManagerSettingActivity.this, KillProcessService.class);
        cbLockKillProcess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    startService(intent);
                }else{
                    stopService(intent);
                }
            }
        });
    }


}
