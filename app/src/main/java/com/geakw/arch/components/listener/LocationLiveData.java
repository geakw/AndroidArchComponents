package com.geakw.arch.components.listener;


import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


/**
 * Created by wlq on 10/6/17.
 */

public class LocationLiveData extends LiveData<Location> {

    private static LocationLiveData sInstance;
    private LocationManager locationManager;

    public static LocationLiveData get(Context context) {
        if (sInstance == null) {
            sInstance = new LocationLiveData(context.getApplicationContext());
        }
        return sInstance;
    }

    private LocationLiveData(Context context) {
        locationManager = (LocationManager) context.getSystemService(
                Context.LOCATION_SERVICE);
    }

    private LocationListener listener = new SimpleLocationListener(){
        @Override
        public void onLocationChanged(Location location) {
            super.onLocationChanged(location);
            setValue(location);
        }
    };


    @Override
    protected void onActive() {
        if(checkPermission())
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
    }

    @Override
    protected void onInactive() {
        locationManager.removeUpdates(listener);
    }

    private boolean checkPermission() {
        return true;
    }
}

