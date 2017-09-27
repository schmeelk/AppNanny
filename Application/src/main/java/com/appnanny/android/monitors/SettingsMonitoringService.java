<!--
 Copyright 2016 The Android Open Source Project

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
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

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.os.Vibrator;
import android.app.NotificationManager;


import com.appnanny.android.MonitorPickerFragment;
import com.appnanny.android.R;

public class SettingsMonitoringService extends Service {

    //public SettingsMonitoringService() {super("SettingsMonitoringService");}

    private static final String TAG = "SettingsMonService";

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        SettingsMonitoringService getService() {
            return SettingsMonitoringService.this;
        }
    }


    @Override
    public void onCreate() {
        Log.i(TAG, String.format("[SettingsMonServ]- Create"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, String.format("[SettingsMonServ]- Start"));
        //Log.i("SettingsMonitoringService received start id " + startId + ": " + intent);
        // Register BroadcastReceiver to track connection changes.
        filter = new IntentFilter(ACTION_USER_SWITCHED);
        filter.addAction(ACTION_USER_ADDED);
        filter.addAction(ACTION_USER_REMOVED);
        receiver = new SettingsMonitoringService.SettingsReceiver();
        this.registerReceiver(receiver, filter);
        Log.i(TAG, String.format("[SettingsMonServ]- Start"));
        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
    }

    protected void onHandleIntent(Intent workIntent) {
        Log.i(TAG, String.format("[SettingsMonServ]- Start"));

        Log.i(TAG, String.format("[SettingsMonServ]- End"));
    }

    private SettingsReceiver receiver;
    IntentFilter filter;

    public static final String ACTION_USER_SWITCHED = "android.intent.action.USER_SWITCHED";
    public static final String ACTION_USER_ADDED    = "android.intent.action.USER_ADDED";
    public static final String ACTION_USER_REMOVED  = "android.intent.action.USER_REMOVED";
    public static final String EXTRA_USER_HANDLE = "android.intent.extra.user_handle";


    public class SettingsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, String.format("[SettingsMonServ]- Received User Change - Start"));

            if (ACTION_USER_SWITCHED.equals(intent.getAction())) {
                Vibrator v = (Vibrator) getSystemService(getBaseContext().VIBRATOR_SERVICE);
                v.vibrate(400);
                Log.i(TAG, String.format("[DeviceUersMonServ]- User Switched"));
                String body = "Device Users Switched";
                String title = "Settings - Monitoring On";
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.t)
                                .setContentTitle(title)
                                .setContentText(body);
                int mNotificationId = 002;
            /* Gets an instance of the NotificationManager service */
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Builds the notification and issues it.
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }
            Log.i(TAG, String.format("[SettingsMonServ]- Received User Change - End"));
        }
    }



}
