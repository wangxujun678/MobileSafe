package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import cn.example.mobilesafe.db.dao.AntivirusDao;
import cn.example.mobilesafe.utils.MD5Utils;

public class AntivirusActivity extends Activity {

    private static final int BEGIN = 1;
    private static final int SCANNING = 2;
    private static final int FINISH = 3;
    private ImageView ivScanning;
    private TextView tvInitVirus;
    private ProgressBar pb;
    private LinearLayout llContent;
    private ScrollView scrollView;

    private Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);

        initUI();
        initData();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int unsafeApp = 0;
            switch (msg.what) {
                case BEGIN:

                    break;
                case SCANNING:
                    TextView child = new TextView(AntivirusActivity.this);
                    ScanInfo scanInfo = (ScanInfo) message.obj;

                    if (scanInfo != null) {
                        if (scanInfo.desc) {
                            child.setText(scanInfo.appName + "有病毒");
                            child.setTextColor(Color.RED);
                            unsafeApp++;
                        } else {
                            child.setText(scanInfo.appName + "扫描安全");
                        }
                    }

                    llContent.addView(child, 0);
                    //自动滚动
                    scrollView.post(new Runnable() {

                        @Override
                        public void run() {
                            //一直往下面进行滚动
                            scrollView.fullScroll(scrollView.FOCUS_DOWN);

                        }
                    });
                    break;
                case FINISH:
                    // 当扫描结束的时候。停止动画
                    ivScanning.clearAnimation();
                    if (unsafeApp == 0) {
                        tvInitVirus.setText("没有发现病毒软件");
                    } else if (unsafeApp > 0) {
                        tvInitVirus.setText("共发现" + unsafeApp + "个病毒软件");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                message = Message.obtain();
                message.what = BEGIN;
                handler.sendMessage(message);

                PackageManager packageManager = getPackageManager();
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
                int size = installedPackages.size();
                pb.setMax(size);

                int progress = 0;
                for (PackageInfo packageInfo : installedPackages) {
                    ScanInfo scanInfo = new ScanInfo();

                    String appName = (String) packageInfo.applicationInfo.loadLabel(packageManager);
                    scanInfo.appName = appName;

                    String packageName = packageInfo.applicationInfo.packageName;
                    scanInfo.packageName = packageName;

                    // 首先需要获取到每个应用程序的目录
                    String sourceDir = packageInfo.applicationInfo.sourceDir;
                    // 获取到文件的md5
                    String md5 = MD5Utils.getFileMd5(sourceDir);

                    String desc = AntivirusDao.checkFileVirus(md5);
                    if (desc == null) {
                        scanInfo.desc = false;
                    } else {
                        scanInfo.desc = true;
                    }
                    progress++;
                    SystemClock.sleep(100);
                    pb.setProgress(progress);

                    message = Message.obtain();
                    message.what = SCANNING;
                    message.obj = scanInfo;
                    handler.sendMessage(message);
                }
                message = Message.obtain();
                message.what = FINISH;
                handler.sendMessage(message);
            }
        }.start();
    }

    static class ScanInfo {
        boolean desc;
        String appName;
        String packageName;
    }

    private void initUI() {

        ivScanning = (ImageView) findViewById(R.id.iv_scanning);
        tvInitVirus = (TextView) findViewById(R.id.tv_init_virus);
        pb = (ProgressBar) findViewById(R.id.progressBar1);
        llContent = (LinearLayout) findViewById(R.id.ll_content);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(4000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);

        ivScanning.startAnimation(rotateAnimation);

    }


}
