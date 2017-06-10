package com.geakw.arch.components;

import android.arch.lifecycle.LifecycleActivity;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.geakw.arch.components.listener.MyLocationListener2;
import com.geakw.arch.components.listener.SimpleLocationListener;

/**
 * Created by wlq on 10/6/17.
 * 在打开定位时，判断当前的lifeCycleOwner所处的状态
 */

public class LocateActivity2 extends LifecycleActivity {
    private MyLocationListener2 myLocationListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLocationListener = new MyLocationListener2(getApplicationContext(), getLifecycle(),new SimpleLocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                super.onLocationChanged(location);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkStatus();

    }


    @Override
    protected void onStop() {
        super.onStop();
        myLocationListener.stop();
    }

    //异步的网络通信
    private void checkStatus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myLocationListener.enable();
                        }
                    });
                }
            }
        }).start();
    }

}
