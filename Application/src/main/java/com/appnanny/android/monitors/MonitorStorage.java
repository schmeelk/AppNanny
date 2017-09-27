/*
* Copyright 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
//Changed by Schmeelk and Aho
///*Copyright 2017 Suzanna Schmeelk and Alfred Aho

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/

package com.appnanny.android.monitors;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.os.BuildCompat;
import android.util.Log;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class responsible for saving/retrieving monitors. This class uses SharedPreferences as storage.
 */
public class MonitorStorage {

    private static final String TAG = MonitorStorage.class.getSimpleName();
    private static final String Monitor_PREFERENCES_NAME = "monitor_preferences";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private SharedPreferences mSharedPreferences;

    public MonitorStorage(Context context) {
        Context storageContext;
        if (BuildCompat.isAtLeastN()) {
            // All N devices have split storage areas, but we may need to
            // move the existing preferences to the new device protected
            // storage area, which is where the data lives from now on.
            final Context deviceContext = context.createDeviceProtectedStorageContext();
            if (!deviceContext.moveSharedPreferencesFrom(context,
                    Monitor_PREFERENCES_NAME)) {
                Log.w(TAG, "Failed to migrate shared preferences.");
            }
            storageContext = deviceContext;
        } else {
            storageContext = context;
        }
        mSharedPreferences = storageContext
                .getSharedPreferences(Monitor_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Stores an Monitor in the SharedPreferences.
     *
     * @param month the integer represents a month
     * @param date the integer represents a date
     * @param hour the integer as 24-hour format the Monitor goes off
     * @param minute the integer of the minute the Monitor goes off
     * @return the saved {@link Monitor} instance
     */
    public Monitor saveMonitor(int type, int month, int date, int hour, int minute) {
        Monitor monitor = new Monitor();
        // Ignore the Id duplication if that happens
        monitor.id = SECURE_RANDOM.nextInt();
        monitor.type = type;
        monitor.month = month;
        monitor.date = date;
        monitor.hour = hour;
        monitor.minute = minute;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(String.valueOf(monitor.id), monitor.toJson());
        editor.apply();
        return monitor;
    }

    /**
     * Retrieves the monitors stored in the SharedPreferences.
     * This method takes linear time as the monitors count.
     *
     * @return a {@link Set} of monitors.
     */
    public Set<Monitor> getMonitors() {
        Set<Monitor> monitors = new HashSet<>();
        for (Map.Entry<String, ?> entry : mSharedPreferences.getAll().entrySet()) {
            monitors.add(Monitor.fromJson(entry.getValue().toString()));
        }
        return monitors;
    }

    /**
     * Delete the monitor instance passed as an argument from the SharedPreferences.
     * This method iterates through the monitors stored in the SharedPreferences, takes linear time
     * as the monitors count.
     *
     * @param toBeDeleted the monitor instance to be deleted
     */
    public void deleteMonitor(Monitor toBeDeleted) {
        for (Map.Entry<String, ?> entry : mSharedPreferences.getAll().entrySet()) {
            Monitor monitor = Monitor.fromJson(entry.getValue().toString());
            if (monitor.id == toBeDeleted.id) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.remove(String.valueOf(monitor.id));
                editor.apply();
                return;
            }
        }
    }
}
