package cn.example.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.example.mobilesafe.bean.BlackNumberInfo;

/**
 * Created by Administrator on 2016/4/20.
 */
public class BlackNumberDao {

    public BlackNumberOpenHelper helper;

    public BlackNumberDao(Context context) {
        helper = new BlackNumberOpenHelper(context);
    }

    /**
     * 添加号码
     *
     * @param number
     * @param mode
     * @return
     */
    public boolean add(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        long rowId = db.insert("blacknumber", null, values);
        db.close();
        return rowId == -1 ? false : true;
    }

    /**
     * 删除号码
     *
     * @param number
     * @return
     */
    public boolean delete(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowsAffected = db.delete("blacknumber", "number=?", new String[]{number});
        db.close();
        return rowsAffected == 0 ? false : true;
    }

    /**
     * 修改一个黑名单号码的拦截模式
     *
     * @param number
     * @param mode
     */
    public boolean changeNumberMode(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        int rowsAffected = db.update("blacknumber", values, "number=?", new String[]{number});
        db.close();
        return rowsAffected == 0 ? false : true;
    }

    /**
     * 返回一个黑名单号码拦截模式
     *
     * @param number
     * @return
     */
    public String findNumberMode(String number) {
        String mode = "";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "number=?", new String[]{number}, null, null, null);
        if (cursor.moveToNext()) {
            mode = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return mode;
    }

    /**
     * 查询所有的黑名单
     *
     * @return
     */
    public List<BlackNumberInfo> findAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
        Cursor cursor = db.query("blacknumber", new String[]{"number", "mode"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setNumber(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }

    /**
     * 返回分页数据
     *
     * @param pageNumber 表示当前是哪一页
     * @param pageSize   表示每一页有多少条数据
     * @return limit 表示限制当前有多少数据
     * offset 表示跳过 从第几条开始
     */
    public List<BlackNumberInfo> findPar(int pageNumber, int pageSize) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?",
                new String[]{String.valueOf(pageSize), String.valueOf((pageNumber - 1) * pageSize)});
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setNumber(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }

    /**
     * 分批加载数据
     *
     * @param startIndex
     * @param maxCount
     * @return
     */
    public List<BlackNumberInfo> findPar2(int startIndex, int maxCount) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?",
                new String[]{String.valueOf(maxCount), String.valueOf(startIndex)});
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setNumber(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberInfos.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }

    /**
     * 获取总的记录数
     *
     * @return
     */
    public int getTotalNumber() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
}
