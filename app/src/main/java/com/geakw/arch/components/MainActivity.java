package com.geakw.arch.components;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onLifecycleClick(View view) {
        Intent intent = new Intent(this, LifeCycleActivity.class);
        startActivity(intent);
    }


    public void onViewModelClick(View view) {
        Intent intent = new Intent(this, ShareDataActivity.class);
        startActivity(intent);
    }

}
