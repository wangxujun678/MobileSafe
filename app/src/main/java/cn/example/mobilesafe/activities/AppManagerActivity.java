package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.example.mobilesafe.bean.AppInfo;
import cn.example.mobilesafe.engine.AppInfos;

public class AppManagerActivity extends Activity implements View.OnClickListener {

    private ListView lvAppInfo;
    private TextView tvRom;
    private TextView tvSd;
    private TextView tvAppNumber;

    private AppInfo clickAppInfo;
    private List<AppInfo> appInfos;
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;

    private PopupWindow popupWindow;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            AppManagerAdapter adapter = new AppManagerAdapter();
            lvAppInfo.setAdapter(adapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        init();
    }


    private void init() {

        lvAppInfo = (ListView) findViewById(R.id.lv_appInfo);
        tvRom = (TextView) findViewById(R.id.tv_rom);
        tvSd = (TextView) findViewById(R.id.tv_sd);
        tvAppNumber = (TextView) findViewById(R.id.tv_app_number);

        long romFreeSpace = Environment.getDataDirectory().getFreeSpace();
        long sdFreeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
        //格式化并显示剩余可用空间
        tvRom.setText("内存可用：" + Formatter.formatFileSize(this, romFreeSpace));
        tvSd.setText("sd可用：" + Formatter.formatFileSize(this, sdFreeSpace));

        //获取所有应用信息是耗时操作，开辟一个子线程
        new Thread() {
            @Override
            public void run() {

                //获取所有应用信息
                appInfos = AppInfos.getAppInfos(AppManagerActivity.this);

                //Log.v("ss", "获取到数据");

                //appInfos拆成 用户程序的集合 + 系统程序的集合
                userAppInfos = new ArrayList<AppInfo>();
                systemAppInfos = new ArrayList<AppInfo>();
                for (AppInfo appInfo : appInfos) {
                    if (appInfo.isUserApp()) {
                        userAppInfos.add(appInfo);
                    } else {
                        systemAppInfos.add(appInfo);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

        lvAppInfo.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             * @param view
             * @param firstVisibleItem 第一个可见的条的位置
             * @param visibleItemCount 一页可以展示多少个条目
             * @param totalItemCount   总共的item的个数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                popupWindowDismiss();

                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > (userAppInfos.size() + 1)) {
                        tvAppNumber.setText("系统程序" + systemAppInfos.size() + "个");
                    } else {
                        tvAppNumber.setText("用户程序" + userAppInfos.size() + "个");
                    }
                }
            }
        });

        lvAppInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Object obj = lvAppInfo.getItemAtPosition(position);
                    if (obj != null && obj instanceof AppInfo) {
                        clickAppInfo = (AppInfo) obj;

                        View contentView = View.inflate(AppManagerActivity.this, R.layout.item_popup, null);
                        LinearLayout ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
                        LinearLayout ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);
                        LinearLayout ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);
                        LinearLayout ll_detail = (LinearLayout) contentView.findViewById(R.id.ll_detail);

                        ll_uninstall.setOnClickListener(AppManagerActivity.this);
                        ll_share.setOnClickListener(AppManagerActivity.this);
                        ll_start.setOnClickListener(AppManagerActivity.this);
                        ll_detail.setOnClickListener(AppManagerActivity.this);

                        popupWindowDismiss();

                        popupWindow = new PopupWindow(contentView, -2, -2);//-2表示包裹内容
                        //需要注意：使用PopupWindow 必须设置背景,不然没有动画,这里设置成透明的
                        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        //获取点击的item在窗体上的位置，主要是为了获取y坐标
                        int[] location = new int[2];
                        view.getLocationInWindow(location);
                        //设置popupWindow显示的位置
                        popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 80, location[1]);

                        //设置popupWindow的显示动画
                        ScaleAnimation anim = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        anim.setDuration(500);
                        contentView.startAnimation(anim);
                    }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_uninstall:
                Intent uninstallIntent = new Intent("android.intent.action.DELETE",
                        Uri.parse("package:" + clickAppInfo.getApkPackageName()));
                startActivity(uninstallIntent);
                popupWindowDismiss();
                break;
            case R.id.ll_start:
                Intent startIntent = getPackageManager().getLaunchIntentForPackage(clickAppInfo.getApkPackageName());
                if (startIntent != null) {
                    startActivity(startIntent);
                } else {
                    Toast.makeText(this, "无法运行该应用程序", Toast.LENGTH_SHORT).show();
                }
                popupWindowDismiss();
                break;
            case R.id.ll_share:
                Intent shareIntent = new Intent("android.intent.action.SEND");
                shareIntent.setType("text/plain");
                shareIntent.putExtra("android.intent.extra.SUBJECT", "分享");
                shareIntent.putExtra("android.intent.extra.TEXT", "Hi！推荐您使用软件：" + clickAppInfo.getApkName() +
                        "下载地址:" + "https://play.google.com/store/apps/details?id=" + clickAppInfo.getApkPackageName());
                startActivity(Intent.createChooser(shareIntent, "分享"));
                popupWindowDismiss();
                break;
            case R.id.ll_detail:
                Intent detailIntent = new Intent();
                detailIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                detailIntent.addCategory(Intent.CATEGORY_DEFAULT);
                detailIntent.setData(Uri.parse("package:" + clickAppInfo.getApkPackageName()));
                startActivity(detailIntent);
                break;
        }

    }

    private class AppManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
        }

        @Override
        public Object getItem(int position) {//处理提示的用户程序和系统程序有多少个
            //如果position等于提示内容所处的位置，则返回null
            if (position == 0 || position == userAppInfos.size() + 1) {
                return null;
            }
            AppInfo appInfo;
            if (position < userAppInfos.size() + 1) {
                appInfo = userAppInfos.get(position - 1);
            } else {
                appInfo = systemAppInfos.get(position - userAppInfos.size() - 2);
            }
            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setPadding(5, 5, 5, 5);
                textView.setText("用户程序" + userAppInfos.size() + "个");
                return textView;
            } else if (position == userAppInfos.size() + 1) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setPadding(5, 5, 5, 5);
                textView.setText("系统程序" + systemAppInfos.size() + "个");
                return textView;
            }
            AppInfo appInfo;
            if (position < userAppInfos.size() + 1) {
                appInfo = userAppInfos.get(position - 1);
            } else {
                appInfo = systemAppInfos.get(position - userAppInfos.size() - 2);
            }

            ViewHolder holder;
            if (convertView != null && convertView instanceof LinearLayout) {

                holder = (ViewHolder) convertView.getTag();

            } else {

                convertView = View.inflate(AppManagerActivity.this, R.layout.item_app_manager, null);
                holder = new ViewHolder();
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tvLocation = (TextView) convertView.findViewById(R.id.tv_location);
                holder.tvApkSize = (TextView) convertView.findViewById(R.id.tv_apk_size);
                convertView.setTag(holder);
            }
            //System.out.println(appInfo.toString());
            holder.ivIcon.setImageDrawable(appInfo.getIcon());
            holder.tvName.setText(appInfo.getApkName());
            if (appInfo.isRom()) {
                holder.tvLocation.setText("手机内存");
            } else {
                holder.tvLocation.setText("外部存储");
            }
            holder.tvApkSize.setText(Formatter.formatFileSize(AppManagerActivity.this, appInfo.getApkSize()));
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvLocation;
        TextView tvApkSize;
    }

    /**
     * 隐藏popupWindow并置为空
     */
    private void popupWindowDismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
}
