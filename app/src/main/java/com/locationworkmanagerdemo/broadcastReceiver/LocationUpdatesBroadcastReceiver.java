/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.locationworkmanagerdemo.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.LocationResult;
import com.locationworkmanagerdemo.util.AppConstants;
import com.locationworkmanagerdemo.util.LocationUtils;

import java.util.List;

/**
 * Receiver for handling location updates.
 *
 * For apps targeting API level O
 * {@link android.app.PendingIntent#getBroadcast(Context, int, Intent, int)} should be used when
 * requesting location updates. Due to limits on background services,
 * {@link android.app.PendingIntent#getService(Context, int, Intent, int)} should not be used.
 *
 *  Note: Apps running on "O" devices (regardless of targetSdkVersion) may receive updates
 *  less frequently than the interval specified in the
 *  {@link com.google.android.gms.location.LocationRequest} when the app is no longer in the
 *  foreground.
 */
public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "LUBroadcastReceiver";

    public static final String ACTION_PROCESS_UPDATES =
            "com.locationworkmanagerdemo.locationupdatespendingintent.LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    List<Location> locations = result.getLocations();
                    LocationUtils.setLocationUpdatesResult(context, locations);
                    sendLocationBroadcast(context);
                    LocationUtils.sendNotification(context, LocationUtils.getLocationResultTitle(context, locations));
                    Log.i(TAG, LocationUtils.getLocationUpdatesResult(context));
                }
            }
        }
    }

    public void sendLocationBroadcast(Context context){
        // Notify anyone listening for broadcasts about the new location.
        Intent intent = new Intent(AppConstants.Location.ACTION_BROADCAST);
        intent.putExtra(AppConstants.Location.EXTRA_LOCATON_FOUND, true);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
