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
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.os.Vibrator;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;

import com.appnanny.android.MonitorPickerFragment;
import com.appnanny.android.R;

public class BatteryMonitoringService extends Service {

    //public BatteryMonitoringService() {super("BatteryMonitoringService");}

    private static final String TAG = "BatteryMonService";

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
        BatteryMonitoringService getService() {
            return BatteryMonitoringService.this;
        }
    }

    Intent batteryStatus;
    IntentFilter ifilter;
    IntentFilter filter;

    @Override
    public void onCreate() {
        Log.i(TAG, String.format("[BatteryMonServ]- Create"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, String.format("[BatteryMonServ]- Start"));
        String message = intent.getStringExtra(MonitorPickerFragment.EXTRA_MESSAGE);
        Log.i(TAG, String.format("[BatteryMonServ]- start message "+message));
        Log.i(TAG, String.format("[BatteryMonServ]-DisplayBattery"));
        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = getBaseContext().registerReceiver(null, ifilter);
        Log.i(TAG, String.format("[BatteryMonServ]-Create contents"));
        StringBuilder b = new StringBuilder();
        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        Log.i(TAG, String.format("Is charging"));

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        Log.i(TAG, String.format("How charging"));
        if(isCharging)
            b.append("Device is Charging \n");
        if(usbCharge)
            b.append("USB Charging\n");
        if(acCharge)
            b.append("AC Charging \n");
        if(status == BatteryManager.BATTERY_PLUGGED_WIRELESS)
            b.append("Wireless Charging \n");
        if(status == BatteryManager.BATTERY_HEALTH_COLD)
            b.append("Device Battery Health Cold \n");
        if(status == BatteryManager.BATTERY_HEALTH_DEAD)
            b.append("Device Battery Health Dead \n");
        if(status == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE)
            b.append("Device Battery Health Over Volatage \n");
        if(status == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
            b.append("Device Battery Health Over Heat \n");
            // Get instance of Vibrator from current Context
            Vibrator v = (Vibrator) getSystemService(getBaseContext().VIBRATOR_SERVICE);
            // Vibrate for 400 milliseconds
            v.vibrate(400);
            String body = "The battery is reporting Health Over Heating";
            String title = "Battery - Over Heating";
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.t)
                            .setContentTitle(title)
                            .setContentText(body);
            // Sets an ID for the notification
            int mNotificationId = 001;
            /* Gets an instance of the NotificationManager service */
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }
        if(status == BatteryManager.BATTERY_HEALTH_UNKNOWN)
            b.append("Device Battery Health Unknown \n");
        if(status == BatteryManager.BATTERY_HEALTH_GOOD)
            b.append("Device Battery Health Good \n");
        if(status == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE)
            b.append("Device Battery Health Unspecified Failure \n");
        if(status == BatteryManager.BATTERY_STATUS_NOT_CHARGING)
            b.append("Device Battery Status Not charging \n");
        if(status == BatteryManager.BATTERY_STATUS_DISCHARGING)
            b.append("Device Battery Status Discharging \n");
        if(status == BatteryManager.BATTERY_STATUS_FULL)
            b.append("Device Battery Status Full \n");
        if(status == BatteryManager.BATTERY_STATUS_UNKNOWN)
            b.append("Device Battery Status Unknown \n");
        if(status == BatteryManager.BATTERY_PROPERTY_CAPACITY)
            b.append("Device Battery Property Capacity \n");
        if(status == BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
            b.append("Device Battery Propery Charge Counter \n");
        if(status == BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)
            b.append("Device Battery Property Current Average \n");
        if(status == BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
            b.append("Device Battery Property Current Now \n");
        if(status == BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER)
            b.append("Device Battery Property Energy Counter \n");

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;

        Log.i(TAG, String.format("[BatteryMonServ]- batteryPct: "+(new Float(batteryPct)).toString()));
        Log.i(TAG, String.format("[BatteryMonServ]- End "));
        // Register BroadcastReceiver to track connection changes.
        filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED); //ConnectivityManager.CONNECTIVITY_ACTION);
        //filter.addAction("android.intent.action.BATTERY_LOW");
        //filter.addAction("android.intent.action.BATTERY_OKAY");
        //filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        receiver = new BatteryMonitoringService.PowerConnectionReceiver();
        this.registerReceiver(receiver, filter);
        return START_NOT_STICKY;

    }


    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
    }

    private PowerConnectionReceiver receiver;// = new PowerConnectionReceiver();

    public class PowerConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, String.format("[BatteryMonServ]- Battery State Change Received - Start"));
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level / (float)scale;
            Log.i(TAG, String.format("[BatteryMonServ]- Battery State Change Received: "+(new Float(batteryPct)).toString()));
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

            if(status == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
                Vibrator v = (Vibrator) getSystemService(getBaseContext().VIBRATOR_SERVICE);
                v.vibrate(400);
                String body = "Battery Health Overheat";
                String title = "Battery - Monitoring On";
                Log.i(TAG, String.format("[BatteryMonServ]- Battery State Change to Over Heat"));
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
            Log.i(TAG, String.format("[BatteryMonServ]- Battery State Change Received - End"));
        }
    }

}
