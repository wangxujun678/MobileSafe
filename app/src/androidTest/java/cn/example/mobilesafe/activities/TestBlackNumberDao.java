package cn.example.mobilesafe.activities;

import android.content.Context;
import android.test.AndroidTestCase;

import java.util.List;
import java.util.Random;

import cn.example.mobilesafe.bean.BlackNumberInfo;
import cn.example.mobilesafe.db.dao.BlackNumberDao;

/**
 * Created by Administrator on 2016/4/20.
 */
public class TestBlackNumberDao extends AndroidTestCase {

    public Context mContext;

    @Override
    protected void setUp() throws Exception {
        this.mContext = getContext();
        super.setUp();
    }



    public void testAdd() {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        Random random = new Random();
        for (int i = 1; i <= 200; i++) {
            long number = 13500000000l + i;
            dao.add(number + "", String.valueOf(random.nextInt(3) + 1));
        }
    }

    public void testDelete(){
        BlackNumberDao dao = new BlackNumberDao(mContext);
        boolean delete = dao.delete("13500000001");
        assertEquals(true,delete);
    }

    public void testFind(){
        BlackNumberDao dao = new BlackNumberDao(mContext);
        String number = dao.findNumberMode("13500000004");
        System.out.println(number);
    }

    public void testFindAll(){
        BlackNumberDao dao = new BlackNumberDao(mContext);
        List<BlackNumberInfo> blackNumberInfos = dao.findAll();
        for (BlackNumberInfo blackNumberInfo:blackNumberInfos){
            System.out.println(blackNumberInfo.getMode() + "" + blackNumberInfo.getNumber());
        }
    }
}
