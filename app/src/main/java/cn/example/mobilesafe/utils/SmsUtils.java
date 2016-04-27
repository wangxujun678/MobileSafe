package cn.example.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2016/4/27.
 */
public class SmsUtils {

    /**
     * 备份短信的接口
     */
    public interface BackupCallBackSms {

        public void beforeBackupSms(int count);

        public void onBackupSms(int process);
    }

    public static boolean backup(Context context, BackupCallBackSms backupCallBackSms) {
        /**
         * 目的 ： 备份短信：
         *
         * 1 判断当前用户的手机上面是否有sd卡
         * 2 权限 ---
         *   使用内容观察者
         * 3 写短信(写到sd卡)
         */
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //使用contentResolver去查询短信数据库
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms/");
            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
            // type = 1 接收短信
            // type = 2 发送短信

            int count = cursor.getCount();
            backupCallBackSms.beforeBackupSms(count);//回调接口，把短信总数传进去

            int process = 0;
            try {
                //把短信备份到sd卡
                File file = new File(Environment.getExternalStorageDirectory(), "smsbackup.xml");
                FileOutputStream fos = new FileOutputStream(file);
                // 得到序列化器
                // 在android系统里面所有有关xml的解析都是pull解析
                XmlSerializer serializer = Xml.newSerializer();
                serializer.setOutput(fos,"utf-8");// 把短信序列化到sd卡然后设置编码格式
                serializer.startDocument("utf-8",true);// standalone表示当前的xml是否是独立文件 ture表示文件独立。yes

                serializer.startTag(null,"smss");
                serializer.attribute(null,"count",String.valueOf(count));

                while (cursor.moveToNext()){
                    serializer.startTag(null,"sms");

                    serializer.startTag(null,"address");
                    serializer.text(cursor.getString(0));
                    serializer.endTag(null,"address");

                    serializer.startTag(null,"date");
                    serializer.text(cursor.getString(1));
                    serializer.endTag(null,"date");

                    serializer.startTag(null,"type");
                    serializer.text(cursor.getString(2));
                    serializer.endTag(null,"type");

                    serializer.startTag(null,"body");
                    serializer.text(cursor.getString(3));
                    serializer.endTag(null,"body");

                    serializer.endTag(null,"sms");

                    process++;
                    backupCallBackSms.onBackupSms(process);

                    SystemClock.sleep(200);
                }
                cursor.close();
                serializer.endTag(null,"smss");
                serializer.endDocument();

                fos.flush();
                fos.close();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, "没有找到sd卡", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
