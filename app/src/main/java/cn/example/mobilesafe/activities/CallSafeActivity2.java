package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.example.mobilesafe.bean.BlackNumberInfo;
import cn.example.mobilesafe.db.dao.BlackNumberDao;

public class CallSafeActivity2 extends Activity {

    private LinearLayout llProgress;
    private ListView lvBlackNumber;
    private EditText etPageJumpTo;
    private TextView tvPageNumber;

    private BlackNumberDao dao;
    private List<BlackNumberInfo> blackNumberInfos;
    private CallSafeAdapter adapter;

    private int mCurrentPage = 1;
    private int mPagesize = 20;
    private int totalPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe2);

        initUI();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            llProgress.setVisibility(View.INVISIBLE);

            adapter = new CallSafeAdapter();
            lvBlackNumber.setAdapter(adapter);

            tvPageNumber.setText(mCurrentPage + "/" + totalPage);
        }
    };

    /**
     * 初始化界面
     */
    private void initUI() {
        llProgress = (LinearLayout) findViewById(R.id.ll_progress);
        llProgress.setVisibility(View.VISIBLE);
        lvBlackNumber = (ListView) findViewById(R.id.lv_black_number);
        etPageJumpTo = (EditText) findViewById(R.id.et_page_jump_to);
        tvPageNumber = (TextView) findViewById(R.id.tv_page_number);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                dao = new BlackNumberDao(CallSafeActivity2.this);
                blackNumberInfos = dao.findPar(mCurrentPage, mPagesize);

                if (dao.getTotalNumber() % mPagesize == 0) {
                    totalPage = dao.getTotalNumber() / mPagesize;
                } else {
                    totalPage = dao.getTotalNumber() / mPagesize + 1;
                }

                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * 添加黑名单，弹出对话框
     */
    public void addBlackNumber(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.dialog_add_black_number, null);

        final EditText etBlackNumber = (EditText) dialogView.findViewById(R.id.et_black_number);
        final CheckBox cbPhone = (CheckBox) dialogView.findViewById(R.id.cb_phone);
        final CheckBox cbSms = (CheckBox) dialogView.findViewById(R.id.cb_sms);
        Button btnOk = (Button) dialogView.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = etBlackNumber.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(CallSafeActivity2.this, "号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String mode = "";
                if (cbPhone.isChecked() && cbSms.isChecked()) {
                    mode = "1";
                } else if (cbPhone.isChecked()) {
                    mode = "2";
                } else if (cbSms.isChecked()) {
                    mode = "3";
                } else {
                    Toast.makeText(CallSafeActivity2.this, "请勾选拦截模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                blackNumberInfo.setNumber(number);
                blackNumberInfo.setMode(mode);
                blackNumberInfos.add(0, blackNumberInfo);
                //把电话号码和拦截模式添加到数据库
                dao.add(number, mode);
                //把添加的黑名单显示出来
                if (adapter == null) {
                    adapter = new CallSafeAdapter();
                    lvBlackNumber.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }

        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setView(dialogView);
        dialog.show();
    }

    /**
     * 自定义adapter
     */
    private class CallSafeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return blackNumberInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return blackNumberInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(CallSafeActivity2.this, R.layout.item_black_number_list, null);
                holder = new ViewHolder();
                holder.tvBlackNumber = (TextView) convertView.findViewById(R.id.tv_black_number);
                holder.tvMode = (TextView) convertView.findViewById(R.id.tv_mode);
                holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvBlackNumber.setText(blackNumberInfos.get(position).getNumber());
            System.out.println(blackNumberInfos.get(position).getNumber());
            String mode = blackNumberInfos.get(position).getMode();
            if (mode.equals("1")) {
                holder.tvMode.setText("电话+短信拦截");
            } else if (mode.equals("2")) {
                holder.tvMode.setText("电话拦截");
            } else if (mode.equals("3")) {
                holder.tvMode.setText("短信拦截");
            }
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = blackNumberInfos.get(position).getNumber();
                    boolean result = dao.delete(number);
                    if (result) {
                        Toast.makeText(CallSafeActivity2.this, "删除成功", Toast.LENGTH_SHORT).show();
                        blackNumberInfos.remove(position);
                        adapter.notifyDataSetChanged();//刷新界面
                    } else {
                        Toast.makeText(CallSafeActivity2.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tvBlackNumber;
        TextView tvMode;
        ImageView ivDelete;
    }

    /**
     * 上一页
     * @param v
     */
    public void prePage(View v) {
        if (mCurrentPage <= 1) {
            Toast.makeText(this, "已经是第一页了", Toast.LENGTH_SHORT).show();
            return;
        }
        mCurrentPage--;
        initData();
    }

    /**
     * 下一页
     * @param v
     */
    public void nextPage(View v) {
        if (mCurrentPage >= totalPage) {
            Toast.makeText(this, "已经是最后一页了", Toast.LENGTH_SHORT).show();
            return;
        }
        mCurrentPage++;
        initData();
    }

    /**
     * 跳转
     * @param v
     */
    public void jumpTo(View v) {
        String pageJumpTo = etPageJumpTo.getText().toString().trim();
        if (TextUtils.isEmpty(pageJumpTo)) {
            Toast.makeText(this, "请输入正确的页码", Toast.LENGTH_SHORT).show();
            return;
        }
        int page = Integer.parseInt(pageJumpTo);
        if (page > 0 && page <= totalPage) {
            mCurrentPage = page;
            initData();
            etPageJumpTo.setText("");
        } else {
            Toast.makeText(this, "请输入正确的页码", Toast.LENGTH_SHORT).show();
        }
    }

}
