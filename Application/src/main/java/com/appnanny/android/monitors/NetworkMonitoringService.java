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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Network;
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

public class NetworkMonitoringService extends Service {

    //public NetworkMonitoringService() {super("NetworkMonitoringService");}
    private static final String TAG = "NetworkMonService";

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
        NetworkMonitoringService getService() {
            return NetworkMonitoringService.this;
        }
    }


    @Override
    public void onCreate() {
        Log.i(TAG, String.format("[NetworkingMonServ]- Create"));
    }

    protected void onHandleIntent(Intent workIntent) {
        Log.i(TAG, String.format("[NetworkMonServ]- Start"));
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkMonitoringService.NetworkConnectionReceiver();
        this.registerReceiver(receiver, filter);
        Log.i(TAG, String.format("[NetworkMonServ]- End"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, String.format("[NetworkingMonServ]- Start"));
        Log.i("NetworkingMonitoringService", "Received start id " + startId + ": " + intent);
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);  //context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Register BroadcastReceiver to track connection changes.
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkConnectionReceiver();
        this.registerReceiver(receiver, filter);
        Log.i(TAG, String.format("[NetworkingMonServ]- Start"));
        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
    }

    IntentFilter filter;
    private NetworkConnectionReceiver receiver;

    public class NetworkConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            Log.i(TAG, String.format("[NetworkMonServ]- Network State Change Received - Start"));
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                    Log.d("Network", "Internet YAY");
                    Log.i(TAG, String.format("[NetworkMonServ]- Network State Change to ON"));
                } else if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {
                    Log.d("Network", "No internet :(");
                    Vibrator v = (Vibrator) getSystemService(getBaseContext().VIBRATOR_SERVICE);
                    v.vibrate(400);
                    String body = "Network Off";
                    String title = "Network - Monitoring On";
                    Log.i(TAG, String.format("[NetworkMonServ]- Network State Change to OFF"));
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
            Log.i(TAG, String.format("[NetworkMonServ]- Network State Change Received - End"));
        }

    }
}
