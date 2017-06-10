package com.geakw.arch.components;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.Observer;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.geakw.arch.components.listener.LocationLiveData;
import com.geakw.arch.components.listener.SimpleLocationListener;


/**
 * Created by wlq on 10/6/17.
 */

public class LocationActivity3 extends LifecycleActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkStatus();
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
                            LocationLiveData.get(LocationActivity3.this).observe(LocationActivity3.this, new Observer<Location>() {
                                @Override
                                public void onChanged(@Nullable Location location) {
                                    //update ui
                                }
                            });
                        }
                    });
                }
            }
        }).start();
    }
}
