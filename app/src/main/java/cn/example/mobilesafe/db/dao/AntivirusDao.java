package cn.example.mobilesafe.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2016/4/28.
 */
public class AntivirusDao {

    /**
     * 传入应用的md5特征值，去病毒数据库查询是否有匹配，如果有，则返回详细信息，否则返回null
     *
     * @param md5
     * @return
     */
    public static String checkFileVirus(String md5) {

        String desc = null;

        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/cn.example.application.mobilesafe/files/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select desc from datable where md5 = ?", new String[]{md5});
        if (cursor.moveToNext()) {
            desc = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return desc;
    }

    public static void addVirus(String md5, String desc) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/cn/example/mobilesafe/files/antivirus.db", null, SQLiteDatabase.OPEN_READWRITE);

        ContentValues contentValues = new ContentValues();
        contentValues.put("md5", md5);
        contentValues.put("type", 6);
        contentValues.put("name", "Android.Troj.AirAD.a");
        contentValues.put("desc", desc);

        db.insert("datable", null, contentValues);

        db.close();
    }
}
