package cn.example.mobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by Administrator on 2016/4/17.
 */
public class LocationService extends Service {

    private LocationManager lm;
    private SharedPreferences mPref;
    private MyLocationListener listener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPref = getSharedPreferences("config", Context.MODE_PRIVATE);

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);// 是否允许付费,比如使用3g网络定位
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String bestProvider = lm.getBestProvider(criteria, true);// 获取最佳位置提供者 if true then only a provider that is currently enabled is returned

        listener = new MyLocationListener();
        lm.requestLocationUpdates(bestProvider, 0, 0, listener);
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            mPref.edit().putString("location", "longitude:" + location.getLongitude() + "latitude:" + location.getLatitude()).commit();
            stopSelf();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(listener);// 当service销毁时,停止更新位置, 节省电量
    }
}
