package com.stark.smartwearableheadset.services;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.stark.smartwearableheadset.MainActivity;
import com.stark.smartwearableheadset.models.RealTimeStat;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BackgroundService extends Service {
    StatsService statsService;
    LocationManager locationManager;
    SharedPreferences preferences;
    private boolean transmitterLoop;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Service", "Service Started !");

        // init
        Context context = getApplicationContext();
        preferences = getSharedPreferences("user_details", MODE_PRIVATE);
        statsService = RetrofitClient.getClient().create(StatsService.class);
        transmitterLoop = true;

        // avoid the service from stopping when the screen is off
//        PowerManager powerManager = (PowerManager) context.getSystemService(context.POWER_SERVICE);
//        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SmartHeadset:keepawake");
//        wakeLock.acquire();
//        wakeLock.release();

        // start the transmission thread
        startTransmitterThread();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        transmitterLoop = false;
        Log.i("Service", "Service Stopped !");
    }

    private void startTransmitterThread() {
        Thread fetchLatestLocationThread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (transmitterLoop) {
                    try {
                        Log.i("Test", "Transmitting data to server...");
                        transmitDataBundle();
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                }
            }
        };
        fetchLatestLocationThread.start();
    }

    // build a bundle and send to the server
    private void transmitDataBundle() {
        RealTimeStat realTimeStat = buildBundle();
        if (realTimeStat.isValidObject()) {
            Call call = statsService.loginUser(realTimeStat);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        ResponseBody responseBody;
                        if (response.isSuccessful()) {
                            responseBody = (ResponseBody) response.body();
                        } else {
                            responseBody = (ResponseBody) response.errorBody();
                        }
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        Log.i("Test", jsonObject.toString());
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Log.i("Error", t.getMessage());
                    Toast.makeText(BackgroundService.this, "An error occurred", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // build the real time stat object with all the data
    private RealTimeStat buildBundle() {
        RealTimeStat realTimeStat = new RealTimeStat();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // check if location is enabled in device
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                // get user location
                Location location = getLocation();
                if (location != null) {
                    realTimeStat.setLatitude(location.getLatitude());
                    realTimeStat.setLongitude(location.getLongitude());
                } else {
                    realTimeStat.setLatitude(0);
                    realTimeStat.setLongitude(0);
                }

                // set date and time
                realTimeStat.setDate(getDate());
                realTimeStat.setTime(getTime());

                // set username
                realTimeStat.setUsername(preferences.getString("username", ""));

                // set heart rate
                // using a dummy heart beat
                realTimeStat.setBpm(getBPM());

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        }
        return realTimeStat;
    }

    // get the current location and set it to the real time stat object
    public Location getLocation() throws SecurityException {
//        if (ActivityCompat.checkSelfPermission(BackgroundService.this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
//                (BackgroundService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(BackgroundService.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            Log.i("Test","01");
        String latitude = "0", longitude = "0";

        Location locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        if (locationGps != null) {
            double lati = locationGps.getLatitude();
            double longi = locationGps.getLongitude();
            latitude = String.valueOf(lati);
            longitude = String.valueOf(longi);

            Log.i("Test", "GPS Location \n Latitude = " + latitude + "\n" + "Longitude = " + longitude);
            return locationGps;
        } else if (locationNetwork != null) {
            double lati = locationNetwork.getLatitude();
            double longi = locationNetwork.getLongitude();
            latitude = String.valueOf(lati);
            longitude = String.valueOf(longi);

            Log.i("Test", "Network Location \n Latitude = " + latitude + "\n" + "Longitude = " + longitude);
            return locationNetwork;
        } else if (locationPassive != null) {
            double lati = locationPassive.getLatitude();
            double longi = locationPassive.getLongitude();
            latitude = String.valueOf(lati);
            longitude = String.valueOf(longi);

            Log.i("Test", "Passive Location \n Latitude = " + latitude + "\n" + "Longitude = " + longitude);
            return locationPassive;
        }
        Log.i("Test", "Location Unavailable !");
        return null;
    }

    // get the user's heart rate from the raspberry pi // current is a dummy random bpm
    public int getBPM() {
        Random r = new Random();
        int low = 40;
        int high = 120;
        int result = r.nextInt(high - low) + low;
        return result;
    }

    public String getDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-d");
        return (formatter.format(date));
    }

    public String getTime() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return (formatter.format(date));
    }

    protected void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
