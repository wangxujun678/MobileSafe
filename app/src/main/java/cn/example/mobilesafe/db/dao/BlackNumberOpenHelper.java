package cn.example.mobilesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/4/20.
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper {

    public BlackNumberOpenHelper(Context context) {
        super(context, "safe.db", null, 1);
    }

    /**
     * blanknumber 表名
     * _id 主键 自动增长
     * number 拦截号码
     * mode 拦截模式 电话拦截 短信拦截 电话+短信拦截
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacknumber (_id integer primary key autoincrement,number varchar(20),mode varchar(2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
