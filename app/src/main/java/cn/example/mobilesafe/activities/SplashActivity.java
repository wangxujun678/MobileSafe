package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.example.mobilesafe.utils.StreamUtils;

/**
 * 用户打开应用时的闪屏页面，包含检查客户端版本功能，如果不是最新版本，则会从服务端下载最新版本并安装
 */
public class SplashActivity extends Activity {

    private static final int CODE_UPDATE_DIALOG = 0;
    private static final int CODE_URL_ERROR = 1;
    private static final int CODE_NET_ERROR = 2;
    private static final int CODE_JSON_ERROR = 3;
    private static final int CODE_ENTER_HOME = 4;

    private TextView tvVersion;
    private TextView tvProgress;
    private ProgressBar pb;
    private RelativeLayout rlRoot;

    //服务器返回的信息
    String mVersionName;//版本名
    int mVersionCode;//版本号
    String mDescription;//版本描述
    String mDownloadUrl;//新版本下载地址

    private SharedPreferences mPref;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(SplashActivity.this, "URL异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_NET_ERROR:
                    Toast.makeText(SplashActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(SplashActivity.this, "数据解析异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_ENTER_HOME:
                    enterHome();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tvVersion = (TextView) findViewById(R.id.tv_Version);
        tvVersion.setText("版本名：" + getLocalVersionName());
        tvProgress = (TextView) findViewById(R.id.tv_Progress);
        pb = (ProgressBar) findViewById(R.id.pb);
        rlRoot = (RelativeLayout) findViewById(R.id.rl_root);

        copyDB("address.db");// 拷贝归属地查询数据库
        copyDB("antivirus.db");

        //判断是否需要自动更新
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        boolean autoUpdate = mPref.getBoolean("auto_update", true);
        if (autoUpdate) {
            checkVersion();
        } else {
            mHandler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);
        }

        //闪屏页设置渐变动画
        AlphaAnimation anmi = new AlphaAnimation(0.3f, 1f);
        anmi.setDuration(2000);
        rlRoot.startAnimation(anmi);
    }

    /**
     * 检查客户端版本
     */
    public void checkVersion() {
        final long startTime = System.currentTimeMillis();
        new Thread() {
            public void run() {
                Message msg = Message.obtain();
                HttpURLConnection conn = null;
                try {
                    //本机地址用localhost，模拟器加载用（10.0.2.2）
                    URL url = new URL("http://10.0.2.2:8080/update.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");//设置连接方法
                    conn.setConnectTimeout(5000);//设置连接超时
                    conn.setReadTimeout(5000);//设置响应超时
                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream is = conn.getInputStream();
                        String result = StreamUtils.readFromStream(is);
                        is.close();
//                        System.out.println("网络返回:" + result);

                        //解析json
                        JSONObject jo = new JSONObject(result);
                        mVersionName = jo.getString("versionName");
                        mVersionCode = jo.getInt("versionCode");
                        mDescription = jo.getString("description");
                        mDownloadUrl = jo.getString("downloadUrl");
//                        System.out.println("版本描述：" + mDescription);

                        System.out.println("下载地址：" + mDownloadUrl);
                        //如果服务器端的版本号大于客户端的版本号，则显示更新对话框
                        if (mVersionCode > getLocalVersionCode()) {
                            msg.what = CODE_UPDATE_DIALOG;
                        } else {
                            msg.what = CODE_ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    //URL异常
                    msg.what = CODE_URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    //网络异常
                    msg.what = CODE_NET_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    //json解析失败
                    msg.what = CODE_JSON_ERROR;
                    e.printStackTrace();
                } finally {
                    //如果闪屏页显示了不到2秒，就让线程睡眠直至显示够2秒
                    long endTime = System.currentTimeMillis();
                    long timeUsed = endTime - startTime;
                    if (timeUsed < 2000) {
                        try {
                            Thread.sleep(2000 - timeUsed);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }.start();

    }

    /**
     * 显示更新对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本：" + mVersionName);
        builder.setMessage(mDescription);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                download();
            }
        });
        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        // 设置取消的监听,用户点击返回键时会触发
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.show();
    }

    /**
     * 获取客户端的版本名
     */
    public String getLocalVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            //没有找到包名异常
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取客户端的版本号
     */
    public int getLocalVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            //没有找到包名异常
            e.printStackTrace();
        }
        return -1;
    }

    private void download() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            tvProgress.setVisibility(View.VISIBLE);

            HttpUtils utils = new HttpUtils();
            String target = Environment.getExternalStorageDirectory() + "/update.apk";
            utils.download(mDownloadUrl, target, new RequestCallBack<File>() {

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    System.out.println("下载进度:" + current + "/" + total);
                    tvProgress.setText("下载进度：" + current * 100 / total + "%");
                }

                @Override
                public void onSuccess(ResponseInfo<File> fileResponseInfo) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(fileResponseInfo.result), "application/vnd.android.package-archive");
                    startActivityForResult(intent, 0);//如果用户取消安装的话，会返回结果，回调onActivityResult
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(SplashActivity.this, "下载失败!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(SplashActivity.this, "没找到sd卡哦亲！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 如果用户取消安装的话，回调此方法
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        enterHome();
    }

    /**
     * 进入主界面
     */
    private void enterHome() {
        Intent intent = new Intent();
        intent.setClass(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 拷贝数据库
     */
    private void copyDB(String dbName) {

//        File filesDir = getFilesDir();
//        System.out.println("路径:" + filesDir.getAbsolutePath());

        File destFile = new File(getFilesDir(), dbName);// 要拷贝的目标地址,该地址在data/data/下应用包内
        if (destFile.exists()) {
            System.out.println("数据库已存在");
            return;
        }

        FileOutputStream fos = null;
        InputStream is = null;

        try {
            is = getAssets().open(dbName);
            fos = new FileOutputStream(destFile);
            int len = 0;
            byte[] b = new byte[1024];
            while ((len = is.read(b)) != -1) {
                fos.write(b, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
