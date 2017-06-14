package com.geakw.arch.components;

import android.util.Log;

import com.geakw.arch.components.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wlq on 14/6/17.
 */

public class Repository {
    private static final String TAG = Repository.class.getSimpleName();

    public static List<User> getUsers() {
        Log.e(TAG,"getUsers");
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            users.add(new User("name" + i,i));
        }
        return users;
    }
}
