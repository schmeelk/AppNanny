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

//import android.app.MonitorManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Utility class for Monitors.
 */
public class MonitorUtil {

    private static final String TAG = "MonitorUtil";
    private final Context mContext;
   // private final MonitorManager mMonitorManager;
    private static int wificount = 0;
    private static int bluecount = 0;
    private static int netcount = 0;
    private static int batcount = 0;
    private static int sdcount = 0;
    private static int acctcount = 0;

    public MonitorUtil(Context context) {
        mContext = context;
        //mMonitorManager = mContext.getSystemService(MonitorManager.class);
        Log.i(TAG, String.format("Create MonitorUtil of type Context"));
    }

    /**
     * Schedules a monitor
     *
     * @param monitor the Monitor to be scheduled
     */
    public void scheduleMonitor(Monitor monitor) {
        Calendar monitorTime = Calendar.getInstance();
        monitorTime.set(Calendar.MONTH, monitor.month);
        monitorTime.set(Calendar.DATE, monitor.date);
        monitorTime.set(Calendar.HOUR_OF_DAY, monitor.hour);
        monitorTime.set(Calendar.MINUTE, monitor.minute);

        //mAlarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
        Log.i(TAG,
                String.format("[MonitorUtil] - Monitor scheduled at (%2d:%02d) Date: %d, Month: %d",
                        monitor.hour, monitor.minute,
                        monitor.month, monitor.date));
    }

    /**
     * Cancels the scheduled monitor.
     *
     * @param monitor the monitor to be canceled.
     */
    public void cancelMonitor(Monitor monitor) {
        this.setType(monitor.getType(), 0);
        Log.i(TAG,
                String.format("[MonitorUtil] - monitor canceled (%2d:%02d) Date: %d, Month: %d",
                        monitor.hour, monitor.minute,
                        monitor.month, monitor.date));
    }

    public static boolean isSet(int type){
        switch(type){
            case 1: return (wificount==0)?false:true;
            case 2: return (bluecount==0)?false:true;
            case 3: return (netcount==0)?false:true;
            case 4: return (batcount==0)?false:true;
            case 5: return (sdcount==0)?false:true;
            case 6: return (acctcount==0)?false:true;
            default: return true;
        }
    }

    public static void setType(int type, int value){
        switch(type){
            case 1: wificount=value;break;
            case 2: bluecount=value;break;
            case 3: netcount=value;break;
            case 4: batcount=value;break;
            case 5: sdcount=value;break;
            case 6: acctcount=value;break;
        }
    }
    /**
     * Returns a next monitor time (nearest day) Calendar instance with the hour and the minute.
     *
     * @param hour the integer of the hour an monitor should go off
     * @param minute the integer of the minute an monitor should go off
     * @return a {@link Calendar} instance an monitor should go off given the passed hour and the
     *         minute
     */
    public Calendar getNextMonitorTime(int type, int hour, int minute) {
        if(this.isSet(type))
            return null;
        this.setType(type, 1);
        Calendar monitorTime = Calendar.getInstance();
        monitorTime.set(Calendar.HOUR_OF_DAY, hour);
        monitorTime.set(Calendar.MINUTE, minute);
        if ((monitorTime.getTimeInMillis() - System.currentTimeMillis()) < 2) {
            monitorTime.add(Calendar.DATE, 1);
        }
        return monitorTime;
    }
}
