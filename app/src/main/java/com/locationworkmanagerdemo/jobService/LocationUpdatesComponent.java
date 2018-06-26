package com.locationworkmanagerdemo.jobService;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.locationworkmanagerdemo.broadcastReceiver.LocationUpdatesBroadcastReceiver;
import com.locationworkmanagerdemo.util.AppConstants;
import com.locationworkmanagerdemo.util.LocationUtils;
import com.locationworkmanagerdemo.util.WorkerUtils;


/**
 * stand alone component for location updates
 */
public class LocationUpdatesComponent {

    private static final String TAG = LocationUpdatesComponent.class.getSimpleName();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2*60 * 1000; // // Every 2 Minutes.

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2; //// Every 1 Minute

    /**
     * The max time before batched results are delivered by location services. Results may be
     * delivered sooner than this interval.
     */
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL_IN_MILLISECONDS * 5; // Every 5 minutes.


    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;


    /**
     * The current location.
     */
    private Location mLocation;

    public ILocationProvider iLocationProvider;


    private Context context;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    public LocationUpdatesComponent(ILocationProvider iLocationProvider) {
        this.iLocationProvider = iLocationProvider;
    }

    /**
     * create first time to initialize the location components
     *
     * @param context
     */
    public void onCreate(Context context) {

        this.context = context;
        Log.i(TAG, "created...............");
        myReceiver = new MyReceiver();
        LocalBroadcastManager.getInstance(context).registerReceiver(myReceiver,
                new IntentFilter(AppConstants.Location.ACTION_BROADCAST));

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        // create location request
        createLocationRequest();

        WorkerUtils.getNotification(context);
    }

    /**
     * start location updates
     */
    public void onStart() {
        Log.i(TAG, "onStart ");
        //hey request for location updates
        requestLocationUpdates();
    }

    /**
     * remove location updates
     */
    public void onStop() {
        Log.i(TAG, "onStop....");
        LocalBroadcastManager.getInstance(context).unregisterReceiver(myReceiver);
        removeLocationUpdates();
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates");
        try {
            LocationUtils.setRequestingLocationUpdates(context, true);
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,getPendingIntent());
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
            LocationUtils.setRequestingLocationUpdates(context, false);
            sendPermissionDeniedBroadCast();

        }
    }


    private PendingIntent getPendingIntent() {
        // Note: for apps targeting API level 25 ("Nougat") or lower, either
        // PendingIntent.getService() or PendingIntent.getBroadcast() may be used when requesting
        // location updates. For apps targeting API level O, only
        // PendingIntent.getBroadcast() should be used. This is due to the limits placed on services
        // started in the background in "O".

        // TODO(developer): uncomment to use PendingIntent.getService().
//        Intent intent = new Intent(this, LocationUpdatesIntentService.class);
//        intent.setAction(LocationUpdatesIntentService.ACTION_PROCESS_UPDATES);
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(context, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            LocationUtils.setRequestingLocationUpdates(context, false);
            mFusedLocationClient.removeLocationUpdates(getPendingIntent());
        } catch (SecurityException unlikely) {
            LocationUtils.setRequestingLocationUpdates(context, true);
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
            Log.e(TAG, "Lost location permission." + unlikely);
            sendPermissionDeniedBroadCast();
        }
    }

    /**
     * get last location
     */
    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                                Log.i(TAG, "getLastLocation " + mLocation);
//                                Toast.makeText(getApplicationContext(), "" + mLocation, Toast.LENGTH_SHORT).show();
                                onNewLocation(mLocation);
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
            sendPermissionDeniedBroadCast();
        }
    }

    private void onNewLocation(Location location) {
        Log.i(TAG, "New location: " + location);
        mLocation = location;

    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // Sets the maximum time when batched location updates are delivered. Updates may be
        // delivered sooner than this interval.
        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
    }




    /**
     * implements this interface to get call back of location changes
     */
    public interface ILocationProvider {
        void onLocationUpdate(boolean locationFound);
    }


    private void sendPermissionDeniedBroadCast() {
        Intent locationIntent = new Intent();
        locationIntent.setAction(AppConstants.Location.ACTION_PERMISSION_DENIED);
        context.sendBroadcast(locationIntent);
    }


    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesBroadcastReceiver}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean locationFound = intent.getBooleanExtra(AppConstants.Location.EXTRA_LOCATON_FOUND,false);
            if (locationFound) {
                if (iLocationProvider != null) {
                    iLocationProvider.onLocationUpdate(true);
                }
            }
        }
    }


}