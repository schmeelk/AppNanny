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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.storage.StorageManager;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.os.Vibrator;
import android.app.NotificationManager;

import com.appnanny.android.MonitorPickerFragment;
import com.appnanny.android.R;

public class SDCardMonitoringService extends Service {


    private static final String TAG = "SDCardMonService";


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
        SDCardMonitoringService getService() {
            return SDCardMonitoringService.this;
        }
    }

    @Override
    public void onCreate() {
            Log.i(TAG, String.format("[SDCardMonServ]- Create"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, String.format("[SDCardMonServ]- Start"));
        IntentFilter filter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
        receiver = new SDCardMonitoringService.SDCardStatusReceiver();
        this.registerReceiver(receiver, filter);
        Log.i(TAG, String.format("[SDCardMonServ]- End"));
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
    }

    protected void onHandleIntent(Intent workIntent) {
        Log.i(TAG, String.format("[SDCardMonServ]- Start"));

        Log.i(TAG, String.format("[SDCArdMonServ]- End"));
    }

    private SDCardStatusReceiver receiver = new SDCardStatusReceiver();

    public class SDCardStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, String.format("[SDCardMonServ]- SDCard State Change Received - Start"));
            final String action = intent.getAction();
            boolean status = getStorageStatus();
            Log.e("SDCARD Status: ", status + "");
            if (action.equals(Intent.ACTION_DEVICE_STORAGE_LOW) || action.equals(Intent.ACTION_MEDIA_EJECT))
            {
                Log.d("SDCARD", "Device Storage Low");
                Vibrator v = (Vibrator) getSystemService(getBaseContext().VIBRATOR_SERVICE);
                    v.vibrate(400);
                    String body = "Storage Low";
                    String title = "SDCard - Monitoring On";
                    Log.i(TAG, String.format("[SDCard]- SDCard State Change to Storage Low"));
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
            Log.i(TAG, String.format("[SDCardMonServ]- SDCard State Change Received - End"));
        }

        public boolean getStorageStatus() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                return false;
            } else {
                return false;
            }
        }
    }
}
