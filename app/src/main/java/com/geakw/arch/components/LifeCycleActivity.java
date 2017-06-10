package com.geakw.arch.components;

import android.arch.lifecycle.LifecycleActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.geakw.arch.components.observer.LifecycleActivityObserver;

/**
 * Created by wlq on 10/6/17.
 * 生命周期调用
 */

public class LifeCycleActivity extends LifecycleActivity {
    LifecycleActivityObserver lifecycleActivityObserver =
            new LifecycleActivityObserver(getLifecycle());
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifecycle);
    }

}
