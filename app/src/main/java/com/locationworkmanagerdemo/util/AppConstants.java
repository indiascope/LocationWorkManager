package com.locationworkmanagerdemo.util;

public class AppConstants {


    public interface Location {
        String START_FOREGROUND_SERVICE = "start_foreground_service";
        int TIME_INTERVAL = 60;
        int MAXIMUM_TIME_INTERVAL = 180;
        String KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates";

        String START_JOB_BROADCAST_RECEIVER = "com.locationworkmanagerdemo.intent.action.START_JOB_FIRSTTIME";
        String KEY_LOCATION_UPDATES_REQUESTED = "location-updates-requested";
        String KEY_LOCATION_UPDATES_RESULT = "location-update-result";

        String LOCATION_NOTIFICATION_CHANNEL_ID = "location_notification_channel";

        String PACKAGE_NAME =
                "com.locationworkmanagerdemo";
        String ACTION_PERMISSION_DENIED = PACKAGE_NAME + ".location.denied";

        String EXTRA_LOCATION = PACKAGE_NAME + ".location";
        String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
                ".started_from_notification";
        /**
         * The identifier for the notification displayed for the foreground service.
         */
        int NOTIFICATION_ID = 123;
        String TAG_BACKGROUND_LOCATION_PERIODIC = "backgroud_location_periodic";
        String TAG_BACKGROUND_LOCATION_TAG_DAILY_START = "backgroud_location_daily_satrt";
        String TAG_BACKGROUND_LOCATION_DAILY_STOP = "backgroud_location_daily_stop";

        String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
        String EXTRA_LOCATON_FOUND ="location_found" ;
    }
}
