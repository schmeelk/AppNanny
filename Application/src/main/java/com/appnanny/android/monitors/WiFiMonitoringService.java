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
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.Binder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Network;
import android.os.Vibrator;
import android.app.NotificationManager;
import static android.net.ConnectivityManager.TYPE_WIFI;


import com.appnanny.android.MonitorPickerFragment;
import com.appnanny.android.R;

public class WiFiMonitoringService extends Service {

    /*public WiFiMonitoringService() {
        super("WiFiMonitoringService");
    }*/

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
        WiFiMonitoringService getService() {
            return WiFiMonitoringService.this;
        }
    }


    @Override
    public void onCreate() {
        Log.i(TAG, String.format("[WiFiMonServ]- Create"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, String.format("[WiFiMonServ]- Start"));
        Log.i("WiFiMonitoringService", "Received start id " + startId + ": " + intent);
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);  //context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Register BroadcastReceiver to track connection changes.
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
        Log.i(TAG, String.format("[WiFiMonServ]- Start"));
        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
    }


    private static final String TAG = "WiFiMonService";
    public static final String WIFI = "WiFi";
    public static final String ANY = "Any";
    // Whether there is a WiFi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver receiver = new NetworkReceiver();
    IntentFilter filter;


    protected void onHandleIntent(Intent workIntent) {
        Log.i(TAG, String.format("[WiFiMonServ]- Start"));
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);  //context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Register BroadcastReceiver to track connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
        Log.i(TAG, String.format("[WiFiMonServ]- End"));
    }


    // Checks the network connection and sets the wifiConnected and mobileConnected variables accordingly.
    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }



    /**
     *
     * This BroadcastReceiver intercepts the android.net.ConnectivityManager.CONNECTIVITY_ACTION,
     * which indicates a connection change. It checks whether the type is TYPE_WIFI.
     * If it is, it checks whether Wi-Fi is connected and sets the wifiConnected flag in the
     * main activity accordingly.
     *
     */
    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            Log.i(TAG, String.format("[WiFiMonServ]- Received Connection Change"));
            // Checks the user prefs and the network connection. Based on the result, decides
            // whether
            // to refresh the display or keep the current display.
            // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
            if (networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // If device has its Wi-Fi connection, sets refreshDisplay
                // to true. This causes the display to be refreshed when the user
                // returns to the app.
                Vibrator v = (Vibrator) getSystemService(getBaseContext().VIBRATOR_SERVICE);
                v.vibrate(400);
                Log.i(TAG, String.format("[WiFiMonServ]- WiFi Connected"));
                String body = "WiFi Connected";
                String title = "WiFi - Monitoring On";
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.t)
                                .setContentTitle(title)
                                .setContentText(body);
                // If the setting is ANY network and there is a network connection
                // (which by process of elimination would be mobile), sets refreshDisplay to true.
                // Sets an ID for the notification
                int mNotificationId = 002;
            /* Gets an instance of the NotificationManager service */
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Builds the notification and issues it.
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            } else if (networkInfo != null) {

                // Otherwise, the app can't download content--either because there is no network
                // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
                // is no Wi-Fi connection.
                // Sets refreshDisplay to false.
            } else {
                Vibrator v = (Vibrator) getSystemService(getBaseContext().VIBRATOR_SERVICE);
                v.vibrate(400);
                String body = "WiFi Connection Lost";
                String title = "WiFi - Monitoring On";
                Log.i(TAG, String.format("[WiFiMonServ]- WiFi Lost"));
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.t)
                                .setContentTitle(title)
                                .setContentText(body);
                // Sets an ID for the notification
                int mNotificationId = 004;
              /* Gets an instance of the NotificationManager service */
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Builds the notification and issues it.
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }
        }
    }



}
