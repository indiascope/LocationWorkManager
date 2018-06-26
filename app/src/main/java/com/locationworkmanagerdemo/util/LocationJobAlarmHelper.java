package com.locationworkmanagerdemo.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;


import com.locationworkmanagerdemo.jobService.LocationUpdatesJobService;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkManager;

public class LocationJobAlarmHelper {

    private static final String TAG = "LocationJobAlarmHelper";

    private LocationJobAlarmHelper() {

    }

    private void cancelJob(Context context, String jobTag) {
        if (CommonMethods.getFormattedString(jobTag).equalsIgnoreCase("")) {
            WorkManager.getInstance().cancelAllWork();
        } else {
            WorkManager.getInstance().cancelAllWorkByTag(jobTag);
        }
    }


    public static void cancelJobScheduler(String workTag) {
        WorkManager.getInstance().cancelAllWorkByTag(workTag);
    }

    public static void cancelAllJobScheduler() {
        WorkManager.getInstance().cancelAllWork();

    }


    public static void setJobScheduler() {
        Log.v(TAG, "Job Scheduler is starting");
        Constraints constraints;
        Constraints.Builder builder = new Constraints.Builder().
                setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(false)
                .setRequiresCharging(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setRequiresDeviceIdle(false);
        }
        constraints = builder.setRequiresStorageNotLow(false).build();

        OneTimeWorkRequest uploadWork = new OneTimeWorkRequest.
                Builder(LocationUpdatesJobService.class)
                .addTag(AppConstants.Location.TAG_BACKGROUND_LOCATION_PERIODIC)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .setConstraints(constraints).build();

//        WorkManager.getInstance().enqueue(uploadWork);

        WorkContinuation workContinuation = WorkManager.getInstance().beginUniqueWork(
                AppConstants.Location.TAG_BACKGROUND_LOCATION_PERIODIC,
                ExistingWorkPolicy.APPEND, uploadWork);

        workContinuation.enqueue();

    /*  ExistingWorkPolicy.REPLACE - Cancel the existing sequence and replace it with the new one
        ExistingWorkPolicy.KEEP - Keep the existing sequence and ignore your new request
        ExistingWorkPolicy.APPEND - Append your new sequence to the existing one,
        running the new sequence's first task after the existing sequence's last task finishes
     */
    }


}
