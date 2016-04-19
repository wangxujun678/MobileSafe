package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import cn.example.mobilesafe.service.AddressService;
import cn.example.mobilesafe.utils.serviceStatusUtils;
import cn.example.mobilesafe.view.SettingClickView;
import cn.example.mobilesafe.view.SettingItemView;

/**
 * 设置中心
 */
public class SettingActivity extends Activity {

    private SharedPreferences mPref;//用来保存设置页面选项的勾选状态
    private SettingItemView sivUpdate;
    private SettingItemView sivAddress;
    private SettingClickView scvAddressStyle;
    private SettingClickView scvAddressLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mPref = getSharedPreferences("config", MODE_PRIVATE);

        initUpdateView();
        initAddressView();
        initAddressStyle();
        initAddressLocation();
    }

    //初始化自动更新开关
    private void initUpdateView(){
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

    /**
     * 初始化归属地开关
     */
    private void initAddressView(){
        sivAddress = (SettingItemView) findViewById(R.id.siv_address);
        boolean serviceRunning = serviceStatusUtils.isServiceRunning(SettingActivity.this,
                "cn.example.mobilesafe.service.AddressService");
        if (serviceRunning){
            sivAddress.setChecked(true);
        }else{
            sivAddress.setChecked(false);
        }
        sivAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sivAddress.isChecked()){
                    sivAddress.setChecked(false);
                    stopService(new Intent(SettingActivity.this, AddressService.class));// 停止归属地服务
                }else{
                    sivAddress.setChecked(true);
                    startService(new Intent(SettingActivity.this, AddressService.class));// 开启归属地服务
                }
            }
        });
    }

    /**
     * 初始化归属地提示框风格
     */
    private void initAddressStyle(){
        scvAddressStyle = (SettingClickView) findViewById(R.id.scv_address_style);
        scvAddressStyle.setTitle("归属地提示框风格");
        scvAddressStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSingleChooseDialog();
            }
        });
    }

    /**
     * 弹出选择归属地提示框风格的对话框
     */
    private String[] items = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };
    private void showSingleChooseDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("归属地提示框风格");

        int style = mPref.getInt("address_style",0);
        builder.setSingleChoiceItems(items,style,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPref.edit().putInt("address_style",which).commit();
                dialog.dismiss();
                scvAddressStyle.setDesc(items[which]);// 更新组合控件的描述信息
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    /**
     * 修改归属地显示位置
     */
    private void initAddressLocation(){
        scvAddressLocation = (SettingClickView) findViewById(R.id.scv_address_location);
        scvAddressLocation.setTitle("归属地提示框显示位置");
        scvAddressLocation.setDesc("设置归属地提示框的显示位置");

        scvAddressLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,DragViewActivity.class));
            }
        });
    }
}
