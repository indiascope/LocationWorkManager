package com.locationworkmanagerdemo.jobService;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.locationworkmanagerdemo.util.LocationJobAlarmHelper;

import java.util.concurrent.CountDownLatch;

import androidx.work.Worker;


public class LocationUpdatesJobService extends Worker implements LocationUpdatesComponent.ILocationProvider {
    private static final String TAG = LocationUpdatesJobService.class.getSimpleName();

    private LocationUpdatesComponent locationUpdatesComponent;

    private HandlerThread handlerThread;
    private CountDownLatch locationWait;
    private Looper looper;


    @NonNull
    @Override
    public Result doWork() {
        handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();
        looper = handlerThread.getLooper();

        Context mContext = getApplicationContext();
        Log.i(TAG, "Service OnCreate started");


        locationUpdatesComponent = new LocationUpdatesComponent(this);
        locationUpdatesComponent.onCreate(mContext);

        //hey request for location updates
        locationUpdatesComponent.onStart();


        try {
            locationWait = new CountDownLatch(1);
            locationWait.await();
            Log.d(TAG, "doWork: Countdown released");
        } catch (InterruptedException e) {
            Log.d(TAG, "doWork: CountdownLatch interrupted");
            e.printStackTrace();
            return Result.RETRY;
        }

        cleanUp();
        return Result.SUCCESS;
    }

    private void cleanUp() {
        Log.d(TAG, "Work is done");
        handlerThread.quit();
        looper.quit();

    }


    @Override
    public void onLocationUpdate(boolean locationFound) {
        Log.i(TAG, "New location: " + locationFound);
        locationUpdatesComponent.onStop();
        reportFinished();


    }

    private void reportFinished() {
        if (locationWait != null) {
            Log.d(TAG, "doWork: locationWait down by one");
            locationWait.countDown();
            LocationJobAlarmHelper.setJobScheduler();
        }
    }


}
