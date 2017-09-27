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
//
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

package com.appnanny.android;

import com.appnanny.android.monitors.Monitor;
import com.appnanny.android.monitors.MonitorStorage;
import com.appnanny.android.monitors.MonitorUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.os.BuildCompat;
import android.support.v4.os.UserManagerCompat;
import android.util.Log;

/**
 * BroadcastReceiver that receives the following implicit broadcasts:
 * <ul>
 *     <li>Intent.ACTION_BOOT_COMPLETED</li>
 *     <li>Intent.ACTION_LOCKED_BOOT_COMPLETED</li>
 * </ul>
 *
 * To receive the Intent.ACTION_LOCKED_BOOT_COMPLETED broadcast, the receiver needs to have
 * <code>directBootAware="true"</code> property in the manifest.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BootBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean bootCompleted;
        String action = intent.getAction();
        Log.i(TAG, "Received action: " + action + ", user unlocked: " + UserManagerCompat
                .isUserUnlocked(context));
        if (BuildCompat.isAtLeastN()) {
            bootCompleted = Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action);
        } else {
            bootCompleted = Intent.ACTION_BOOT_COMPLETED.equals(action);
        }
        if (!bootCompleted) {
            return;
        }
        MonitorUtil util = new MonitorUtil(context);
        MonitorStorage monitorStorage = new MonitorStorage(context);
        for (Monitor monitor : monitorStorage.getMonitors()) {
            util.scheduleMonitor(monitor);
        }
    }
}
