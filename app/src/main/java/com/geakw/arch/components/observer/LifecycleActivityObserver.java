package com.geakw.arch.components.observer;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

/**
 * Created by wlq on 10/6/17.
 */

public class LifecycleActivityObserver implements LifecycleObserver {
    private static String TAG = LifecycleActivityObserver.class.getSimpleName();
    private Lifecycle mLifecycle;

    public LifecycleActivityObserver(Lifecycle lifecycle) {
        mLifecycle = lifecycle;
        mLifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreated() {
        Log.e(TAG, "onCreated state : " + mLifecycle.getCurrentState());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart() {
        Log.e(TAG, "onStart state : " + mLifecycle.getCurrentState());

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume() {
        Log.e(TAG, "onResume state : " + mLifecycle.getCurrentState());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause() {
        Log.e(TAG, "onPause state : " + mLifecycle.getCurrentState());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        Log.e(TAG, "onStop state : " + mLifecycle.getCurrentState());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        Log.e(TAG, "onDestroy state : " + mLifecycle.getCurrentState());
        mLifecycle.removeObserver(this);
    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
//    public void onAny() {
//        Log.e(TAG, "onAny state : " + mLifecycle.getCurrentState());
//    }
}

