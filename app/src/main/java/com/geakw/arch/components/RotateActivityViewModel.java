package com.geakw.arch.components;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.geakw.arch.components.bean.User;

import java.util.List;

/**
 * Created by wlq on 14/6/17.
 */

public class RotateActivityViewModel extends ViewModel {
    private MutableLiveData<List<User>> users;

    public MutableLiveData<List<User>> getUsers(){
        if(users == null){
            users = new MutableLiveData<>();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        List<User> newUsers = Repository.getUsers();
                        users.postValue(newUsers);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
        return users;
    }

}
