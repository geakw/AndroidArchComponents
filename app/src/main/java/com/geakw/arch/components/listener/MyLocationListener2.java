package com.geakw.arch.components.listener;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;

/**
 * Created by wlq on 10/6/17.
 */

public class MyLocationListener2 implements LifecycleObserver {
    boolean enabled = false;
    private Context mContext;
    private SimpleLocationListener mCallback;
    private Lifecycle lifecycle;

    public MyLocationListener2(Context context, Lifecycle lifecycle, SimpleLocationListener callback) {
        mContext = context;
        mCallback = callback;
        this.lifecycle = lifecycle;
        lifecycle.removeObserver(this);
    }

    public void enable() {
        enabled = true;
        if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            start();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void start() {
        if (enabled) {
            //connect
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void stop() {
        //disconnect if connect
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        lifecycle.removeObserver(this);
    }

}
