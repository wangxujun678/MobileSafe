package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity {

    private EditText etPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

        etPhone = (EditText) findViewById(R.id.et_phone);
        String phone = mPref.getString("safe_phone","");
        etPhone.setText(phone);
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this, Setup2Activity.class));
        finish();

        overridePendingTransition(R.anim.previous_tran_in, R.anim.previous_tran_out);
    }

    @Override
    public void showNextPage() {
        String phone = etPhone.getText().toString().trim();//过滤空格
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this,"安全号码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        mPref.edit().putString("safe_phone",phone).commit();

        startActivity(new Intent(this, Setup4Activity.class));
        finish();
        //设置activity切换动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    public void selectContact(View v){
        Intent intent = new Intent(Setup3Activity.this,ContactsActivity.class);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            String phone = data.getStringExtra("phone");
            phone = phone.replace("-","").replace(" ","");//替换 "-"和空格为""
            etPhone.setText(phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
