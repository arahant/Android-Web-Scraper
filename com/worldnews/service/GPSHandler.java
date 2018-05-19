package com.worldnews.service;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

public class GPSHandler {

    private String provider;
    private Context context;
    private LocationManager gpsManager;
    private static Location location;

    public GPSHandler(Context cont) {
        context = cont;
        gpsManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public boolean isGpsEnabled() {
        return gpsManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private String getProvider() {
        if(provider==null)
            provider=gpsManager.getBestProvider(new Criteria(),true);
        return provider;
    }

    private boolean checkPermission() {
        boolean permission = false;
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            permission = true;
        return permission;
    }

    public void setLocation() {
        if(isGpsEnabled())
            if (checkPermission())
                location = gpsManager.getLastKnownLocation(getProvider());
    }

    public static Location getLocation() {
        return location;
    }

}
