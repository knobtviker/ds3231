package com.knobtviker.android.things.contrib.community.driver.ds3231;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.things.device.TimeManager;
import com.knobtviker.android.things.contrib.community.boards.BoardDefaults;

import java.io.IOException;

/**
 * Created by bojan on 17/01/2018.
 */

public class Ds3231Receiver extends BroadcastReceiver {
    private static final String TAG = Ds3231Receiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            switch (action) {
                case Intent.ACTION_BOOT_COMPLETED:
                    handleBootComplete(context);
                    break;
                case Intent.ACTION_TIME_CHANGED:
                    handleTimeChanged(context);
                    break;
                default:
                    Log.e(TAG, String.format("Unexpected broadcast onReceive action: %s", action));
                    break;
            }
        }
    }

    private void handleBootComplete(@NonNull final Context context) {
        final String i2cPort = getI2cName(context);
        if (!TextUtils.isEmpty(i2cPort)) {
            try {
                final Ds3231 ds3231 = new Ds3231(i2cPort);
                final long ds3231Timestamp = ds3231.getTimeInMillis();
                final long systemTimeStamp = System.currentTimeMillis();
                // If the DS3231 has a sane timestamp, set the system clock using the DS3231.
                // Otherwise, set the DS3231 using the system time if the system time appears sane
                if (isSaneTimestamp(ds3231Timestamp)) {
                    Log.i(TAG, "Setting system clock using DS3231");
                    final TimeManager timeManager = new TimeManager();
                    timeManager.setTime(ds3231Timestamp);

                    // Re-enable NTP updates.
                    // The call to setTime() disables them automatically, but that's what we use to update our DS3231.
                    timeManager.setAutoTimeEnabled(true);
                } else if (isSaneTimestamp(systemTimeStamp)) {
                    Log.i(TAG, "Setting DS3231 time using system clock");
                    ds3231.setTime(systemTimeStamp);
                }
                ds3231.close();
            } catch (IOException e) {
                Log.e(TAG, "Error accessing DS3231", e);
            }
        }
    }

    private void handleTimeChanged(@NonNull final Context context) {
        final String i2cPort = getI2cName(context);
        if (!TextUtils.isEmpty(i2cPort)) {
            try {
                final Ds3231 ds3231 = new Ds3231(i2cPort);
                final long timestamp = System.currentTimeMillis();
                if (isSaneTimestamp(timestamp)) {
                    Log.i(TAG, "Time changed. Setting DS3231 time using system clock");
                    ds3231.setTime(timestamp);
                } else {
                    Log.w(TAG, "Time changed. Ignoring non-sane timestamp");
                }
                ds3231.close();
            } catch (IOException e) {
                Log.e(TAG, "Error accessing DS3231", e);
            }
        }
    }

    // Assume timestamp is not sane if the timestamp predates the build time of this image. Borrowed this logic from AlarmManagerService
    private boolean isSaneTimestamp(final long timestamp) {
        final long systemBuildTime = Environment.getRootDirectory().lastModified();
        return timestamp >= systemBuildTime;
    }

    private String getI2cName(@NonNull final Context context) {
        String i2cPort = null;
        // try to get I2C port from application manifest metadata
        try {
            i2cPort = context
                .getPackageManager()
                .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                .metaData
                .getString("ds3231_i2c");
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.w(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        // if previous failed, fallback to built int dependency
        if (i2cPort == null) {
            i2cPort = BoardDefaults.getI2CPort();
        }

        // nothing worked, show error, this should never happen
        if (i2cPort == null) {
            Log.e(TAG, "Unable to get I2C port name.");
        }

        return i2cPort;
    }
}
