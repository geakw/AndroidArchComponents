package com.geakw.arch.components;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.geakw.arch.components.listener.MyLocationListener1;
import com.geakw.arch.components.listener.SimpleLocationListener;

/**
 * Created by wlq on 10/6/17.
 */

public class LocateActivity1 extends AppCompatActivity {
    private MyLocationListener1 myLocationListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLocationListener = new MyLocationListener1(getApplicationContext(), new SimpleLocationListener() {
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
                    myLocationListener.start();
                }
            }
        }).start();
    }

}
