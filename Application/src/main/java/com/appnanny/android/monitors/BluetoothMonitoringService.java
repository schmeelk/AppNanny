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
import android.bluetooth.BluetoothAdapter;
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

import static android.R.attr.action;

public class BluetoothMonitoringService extends Service {

    //public BluetoothMonitoringService() {super("BluetoothMonitoringService");}

    private static final String TAG = "BluetoothMonService";

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
        BluetoothMonitoringService getService() {return BluetoothMonitoringService.this; }
    }


    @Override
    public void onCreate() {
        Log.i(TAG, String.format("[BluetoothMonServ]- Create"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, String.format("[BluetoothMonServ]- Start"));
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        // Register BroadcastReceiver to track connection changes.
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        receiver = new BluetoothMonitoringService.BluetoothConnectionReceiver();
        this.registerReceiver(receiver, filter);
        Log.i(TAG, String.format("[BluetoothMonServ]- Start"));
        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, String.format("[BluetoothMonServ]- Distroy"));
    }

    protected void onHandleIntent(Intent workIntent) {
        Log.i(TAG, String.format("[BluetoothMonServ]- Start"));

        Log.i(TAG, String.format("[BluetoothMonServ]- End"));
    }

    IntentFilter filter;
    private BluetoothConnectionReceiver receiver;

    public class BluetoothConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, String.format("[BluetoothMonServ]- Bluetooth State Change Received - Start"));
            //int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                    Vibrator v = (Vibrator) getSystemService(getBaseContext().VIBRATOR_SERVICE);
                    v.vibrate(400);
                    String body = "Bluetooth Off";
                    String title = "Bluetooth - Monitoring On";
                    Log.i(TAG, String.format("[BluetoothMonServ]- Bluetooth State Change to OFF"));
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
                else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                    Vibrator v = (Vibrator) getSystemService(getBaseContext().VIBRATOR_SERVICE);
                    v.vibrate(400);
                    String body = "Bluetooth ON";
                    String title = "Bluetooth - Monitoring On";
                    Log.i(TAG, String.format("[BluetoothMonServ]- Bluetooth State Change to ON"));
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

            Log.i(TAG, String.format("[BluetoothMonServ]- Bluetooth State Change Received - End"));
        }

    }
}
