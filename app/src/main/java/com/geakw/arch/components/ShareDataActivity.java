package com.geakw.arch.components;

import android.arch.lifecycle.LifecycleActivity;
import android.os.Bundle;


/**
 * Created by wlq on 11/6/17.
 */

public class ShareDataActivity extends LifecycleActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share_data);
    }
}
