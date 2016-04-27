package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import cn.example.mobilesafe.utils.SmsUtils;

public class AToolsActivity extends Activity {

    private ProgressDialog pd;

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

    /**
     * 备份短信
     * @param v
     */
    public void backupSms(View v){

        pd = new ProgressDialog(AToolsActivity.this);
        pd.setTitle("提示");
        pd.setMessage("稍安勿躁。正在备份。你等着吧。。");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();

        new Thread(){
            @Override
            public void run() {
                boolean result = SmsUtils.backup(AToolsActivity.this,new SmsUtils.BackupCallBackSms() {
                    @Override
                    public void beforeBackupSms(int count) {
                        pd.setMax(count);
                    }

                    @Override
                    public void onBackupSms(int process) {
                        pd.setProgress(process);
                    }
                });
                if (result){
                    AToolsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AToolsActivity.this,"备份成功",Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    AToolsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AToolsActivity.this,"备份失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                pd.dismiss();
            }
        }.start();
    }
}
