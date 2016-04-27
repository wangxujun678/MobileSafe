package cn.example.mobilesafe.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.example.mobilesafe.bean.TaskInfo;
import cn.example.mobilesafe.engine.TaskInfoParser;
import cn.example.mobilesafe.utils.SystemInfoUtils;
import cn.example.mobilesafe.utils.UIUtils;

public class TaskManagerActivity extends Activity {

    private TextView tvTaskCount;
    private TextView tvMemory;
    private ListView lvTask;

    private int processCount;
    private long availMem;
    private long totalMem;
    private List<TaskInfo> taskInfos;
    private ArrayList<TaskInfo> userTaskInfos;
    private ArrayList<TaskInfo> systemTaskInfos;
    private TaskManagerAdapter taskManagerAdapter;

    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);

        initUI();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (taskManagerAdapter!=null){
            taskManagerAdapter.notifyDataSetChanged();
        }
    }

    private void initUI() {

        mPref = getSharedPreferences("config",MODE_PRIVATE);

        tvTaskCount = (TextView) findViewById(R.id.tv_task_count);
        tvMemory = (TextView) findViewById(R.id.tv_memory);
        lvTask = (ListView) findViewById(R.id.lv_task);

        processCount = SystemInfoUtils.getProcessCount(TaskManagerActivity.this);
        tvTaskCount.setText("进程：" + processCount + "个");
        availMem = SystemInfoUtils.getAvailMem(TaskManagerActivity.this);
        totalMem = SystemInfoUtils.getTotalMem(TaskManagerActivity.this);
        tvMemory.setText("剩余/总内存：" + Formatter.formatFileSize(TaskManagerActivity.this, availMem)
                + "/" + Formatter.formatFileSize(TaskManagerActivity.this, totalMem));

        lvTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object obj = lvTask.getItemAtPosition(position);
                if (obj != null && obj instanceof TaskInfo) {
                    TaskInfo taskInfo = (TaskInfo) obj;
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (taskInfo.getPackageName().equals(getPackageName())) {
                        return;
                    }
                    /**
                     * 判断当前的item是否被勾选上
                     * 如果被勾选上了。那么就改成没有勾选。 如果没有勾选。就改成已经勾选
                     */
                    if (taskInfo.isChecked()) {
                        taskInfo.setChecked(false);
                        holder.cbAppStatus.setChecked(false);
                    } else {
                        taskInfo.setChecked(true);
                        holder.cbAppStatus.setChecked(true);
                    }
                }
            }
        });

    }

    private void initData() {

        new Thread() {
            @Override
            public void run() {

                taskInfos = TaskInfoParser.getTaskInfos(TaskManagerActivity.this);
                userTaskInfos = new ArrayList<TaskInfo>();
                systemTaskInfos = new ArrayList<TaskInfo>();
                for (TaskInfo taskInfo : taskInfos) {
                    if (taskInfo.isUserApp()) {
                        userTaskInfos.add(taskInfo);
                    } else {
                        systemTaskInfos.add(taskInfo);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        taskManagerAdapter = new TaskManagerAdapter();
                        lvTask.setAdapter(taskManagerAdapter);
                    }
                });
            }
        }.start();
    }


    private class TaskManagerAdapter extends BaseAdapter {
        @Override
        public int getCount() {

            boolean result = mPref.getBoolean("show_system",false);
            if (result){
                return userTaskInfos.size() + 1 + systemTaskInfos.size() + 1;
            }else{
                return userTaskInfos.size() + 1;
            }
        }

        @Override
        public Object getItem(int position) {
            if (position == 0 || position == userTaskInfos.size() + 1) {
                return null;
            }
            TaskInfo taskInfo = null;
            if (position < userTaskInfos.size() + 1) {
                taskInfo = userTaskInfos.get(position - 1);
            }
            if (position > userTaskInfos.size() + 1) {
                taskInfo = systemTaskInfos.get(position - userTaskInfos.size() - 2);
            }
            return taskInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                // 第0个位置显示的应该是 用户程序的个数的标签。
                TextView tv = new TextView(getApplicationContext());
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("用户进程：" + userTaskInfos.size() + "个");
                return tv;
            } else if (position == (userTaskInfos.size() + 1)) {
                TextView tv = new TextView(getApplicationContext());
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("系统进程：" + systemTaskInfos.size() + "个");
                return tv;
            }

            ViewHolder holder;
            if (convertView != null && convertView instanceof LinearLayout) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(TaskManagerActivity.this, R.layout.item_task_manager, null);
                holder = new ViewHolder();

                holder.ivAppIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tvAppName = (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.tvAppMemorySize = (TextView) convertView.findViewById(R.id.tv_app_memory_size);
                holder.cbAppStatus = (CheckBox) convertView.findViewById(R.id.cb_app_status);

                convertView.setTag(holder);
            }

            TaskInfo taskInfo = null;
            if (position < userTaskInfos.size() + 1) {
                taskInfo = userTaskInfos.get(position - 1);
            }
            if (position > userTaskInfos.size() + 1) {
                taskInfo = systemTaskInfos.get(position - userTaskInfos.size() - 2);
            }

            //给item设置数据
            holder.ivAppIcon.setImageDrawable(taskInfo.getIcon());
            holder.tvAppName.setText(taskInfo.getAppName());
            holder.tvAppMemorySize.setText("内存占用：" +
                    Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemorySize() * 1000));
            if (taskInfo.isChecked()) {
                holder.cbAppStatus.setChecked(true);
            } else {
                holder.cbAppStatus.setChecked(false);
            }

            //判断当前展示的item是否是自己的程序。如果是,就把checkbox隐藏
            if (taskInfo.getPackageName().equals(getPackageName())) {
                holder.cbAppStatus.setVisibility(View.INVISIBLE);
            } else {
                holder.cbAppStatus.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView ivAppIcon;
        TextView tvAppName;
        TextView tvAppMemorySize;
        CheckBox cbAppStatus;
    }

    /**
     * 全选
     *
     * @param v
     */
    public void selectAll(View v) {
        for (TaskInfo taskInfo : userTaskInfos) {
            if (taskInfo.getPackageName().equals(getPackageName())) {
                continue;
            }
            taskInfo.setChecked(true);
        }
        for (TaskInfo taskInfo : systemTaskInfos) {
            taskInfo.setChecked(true);
        }
        taskManagerAdapter.notifyDataSetChanged();
    }

    /**
     * 反选
     *
     * @param v
     */
    public void selectOpposite(View v) {
        for (TaskInfo taskInfo : userTaskInfos) {
            if (taskInfo.getPackageName().equals(getPackageName())) {
                continue;
            }
            taskInfo.setChecked(!taskInfo.isChecked());
        }
        for (TaskInfo taskInfo : systemTaskInfos) {
            taskInfo.setChecked(!taskInfo.isChecked());
        }
        taskManagerAdapter.notifyDataSetChanged();
    }

    /**
     * 杀死进程
     *
     * @param v
     */
    public void killProcess(View v) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        /**
         * 注意： 当集合在迭代的时候。不能修改集合的大小,所以在创建一个集合存放将要杀死的进程
         */
        List<TaskInfo> killTaskInfos = new ArrayList<TaskInfo>();

        // 清理的总共的进程个数
        int totalCount = 0;
        // 清理的进程的大小
        int killMem = 0;

        for (TaskInfo taskInfo : userTaskInfos) {
            if (taskInfo.isChecked()) {
                killTaskInfos.add(taskInfo);
                totalCount++;
                killMem += taskInfo.getMemorySize();
            }
        }

        for (TaskInfo taskInfo : systemTaskInfos) {
            if (taskInfo.isChecked()) {
                killTaskInfos.add(taskInfo);
                totalCount++;
                killMem += taskInfo.getMemorySize();
            }
        }

        for (TaskInfo taskInfo : killTaskInfos) {
            if (taskInfo.isUserApp()) {
                userTaskInfos.remove(taskInfo);
                am.killBackgroundProcesses(taskInfo.getPackageName());// 杀死进程 参数表示包名
            } else {
                systemTaskInfos.remove(taskInfo);
                am.killBackgroundProcesses(taskInfo.getPackageName());
            }
        }

        UIUtils.showToast(TaskManagerActivity.this, "共清理" + totalCount + "个进程,释放"
                + Formatter.formatFileSize(TaskManagerActivity.this, killMem * 1000) + "内存");

        processCount -= totalCount;
        tvTaskCount.setText("进程:" + processCount + "个");
        availMem += killMem;
        tvMemory.setText("剩余/总内存:" + Formatter.formatFileSize(TaskManagerActivity.this, availMem)
                + "/" + Formatter.formatFileSize(TaskManagerActivity.this, totalMem));

        taskManagerAdapter.notifyDataSetChanged();
    }

    /**
     * 打开设置页面
     *
     * @param v
     */
    public void openSetting(View v) {
        Intent intent = new Intent(this,TaskManagerSettingActivity.class);
        startActivity(intent);
    }
}
