package cn.example.mobilesafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class Setup4Activity extends BaseSetupActivity {

    private CheckBox cbProtect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        cbProtect = (CheckBox) findViewById(R.id.cb_protect);

        boolean protect = mPref.getBoolean("protect",false);
        if(protect){
            cbProtect.setChecked(true);
            cbProtect.setText("防盗保护已经开启");
        }else{
            cbProtect.setChecked(false);
            cbProtect.setText("防盗保护没有开启");
        }
        cbProtect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cbProtect.setText("防盗保护已经开启");
                    mPref.edit().putBoolean("protect",true).commit();
                }else{
                    cbProtect.setText("防盗保护没有开启");
                    mPref.edit().putBoolean("protect",false).commit();
                }
            }
        });
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this, Setup3Activity.class));
        finish();

        overridePendingTransition(R.anim.previous_tran_in, R.anim.previous_tran_out);
    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(this, LostFindActivity.class));
        finish();

        //设置activity切换动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);

        mPref.edit().putBoolean("configed", true).commit();// 更新sp,表示已经展示过设置向导了,下次进来就不展示啦
    }

}
