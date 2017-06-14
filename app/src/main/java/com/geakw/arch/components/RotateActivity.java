package com.geakw.arch.components;

import android.app.Activity;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.geakw.arch.components.bean.User;

import java.util.List;

/**
 * Created by wlq on 14/6/17.
 */

public class RotateActivity extends LifecycleActivity {
    private static final String TAG = RotateActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RotateActivityViewModel viewModel =
                ViewModelProviders.of(this).get(RotateActivityViewModel.class);
        viewModel.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                Log.e(TAG,users.toString());
            }
        });
    }
}
