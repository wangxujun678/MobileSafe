package cn.example.mobilesafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import cn.example.mobilesafe.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

    private SettingItemView sivSim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        sivSim = (SettingItemView) findViewById(R.id.siv_sim);

        String sim = mPref.getString("sim",null);
        if(TextUtils.isEmpty(sim)){
            sivSim.setChecked(false);
        }else{
            sivSim.setChecked(true);
        }
        sivSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sivSim.isChecked()){
                    sivSim.setChecked(false);
                    //删除已绑定的sim卡
                    mPref.edit().remove("sim").commit();
                }else{
                    sivSim.setChecked(true);
                    //获取sim卡序列号
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = tm.getSimSerialNumber();
                    //保存sim卡序列号
                    mPref.edit().putString("sim", simSerialNumber).commit();
                }
            }
        });
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this, Setup1Activity.class));
        finish();

        overridePendingTransition(R.anim.previous_tran_in, R.anim.previous_tran_out);
    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(this, Setup3Activity.class));
        finish();
        //设置activity切换动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }


}
