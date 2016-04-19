package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AToolsActivity extends Activity {

    /**
     * 高级工具
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    /**
     * 打开归属地查询页面
     *
     */
    public void numberAddressQuery(View v){
        startActivity(new Intent(this,AddressActivity.class));
    }
}
