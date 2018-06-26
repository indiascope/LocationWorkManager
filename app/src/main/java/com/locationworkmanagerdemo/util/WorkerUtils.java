/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.locationworkmanagerdemo.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.locationworkmanagerdemo.R;
import com.locationworkmanagerdemo.ui.MainActivity;


public final class WorkerUtils {
    private static final String TAG = WorkerUtils.class.getSimpleName();

    private WorkerUtils() {
    }

    /**
     * Create a Notification that is shown as a heads-up notification if possible.
     * <p>
     * For this codelab, this is used to show a notification so that you know when different steps
     * of the background work chain are starting
     *
     * @param context Context needed to create Toast
     */

    public  static void getNotification(Context context) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i(TAG, "Service Notification Channel OnCreate started");

            CharSequence name = context.getString(R.string.app_name);

            NotificationChannel mChannel =
                    new NotificationChannel(AppConstants.Location.LOCATION_NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setDescription("User Location");
            // Add the channel
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Set the Notification Channel for the Notification Manager.
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }


        Intent notificationIntent = new Intent(context, MainActivity.class);
        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        notificationIntent.putExtra(AppConstants.Location.EXTRA_STARTED_FROM_NOTIFICATION, true);
        CharSequence text = "Location Updates";

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);
        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        // Get a notification builder that's compatible with platform versions >= 4
        Notification.Builder notificationBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i(TAG, "Service Notification Channel Android O Channel  started");
            notificationBuilder = new Notification.Builder(context,
                    AppConstants.Location.LOCATION_NOTIFICATION_CHANNEL_ID);
        } else {
            notificationBuilder = new Notification.Builder(context);
        }
        // Define the notification settings.
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(Color.RED);
        }

        notificationBuilder.setContentTitle(text)
                .setContentText("Location Update work in Background")
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        notificationBuilder.setAutoCancel(true);

        // Define the notification settings.
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(Color.RED);
        }


        // Show the notification
        NotificationManagerCompat.from(context).notify(AppConstants.Location.NOTIFICATION_ID, notificationBuilder.build());


    }


}