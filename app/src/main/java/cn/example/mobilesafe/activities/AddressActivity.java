package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import cn.example.mobilesafe.db.dao.AddressDao;

public class AddressActivity extends Activity {

    private EditText etNumber;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        etNumber = (EditText) findViewById(R.id.et_number);
        tvResult = (TextView) findViewById(R.id.tv_result);

        //监听EditText的变化
        etNumber.addTextChangedListener(new TextWatcher() {
            // 文字发生变化前的回调
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            // 文字变化时的回调
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String address = AddressDao.getAddress(s.toString());
                tvResult.setText(address);
            }

            // 文字变化结束之后的回调
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void query(View v) {
        String number = etNumber.getText().toString().trim();
        if (!TextUtils.isEmpty(number)) {
            String address = AddressDao.getAddress(number);
            tvResult.setText(address);
        } else {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            etNumber.startAnimation(shake);
            vibrate();
        }
    }

    /**
     * 手机震动, 需要权限 android.permission.VIBRATE
     */
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(500);
    }
}
