package com.geakw.arch.components;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

/**
 * Created by wlq on 11/6/17.
 */

public class SeekBarViewModel extends ViewModel {

    public MutableLiveData<Integer> seekbarValue = new MutableLiveData<>();
}
