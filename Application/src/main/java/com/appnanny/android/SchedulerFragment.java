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

package com.appnanny.android;

import com.appnanny.android.monitors.Monitor;
import com.appnanny.android.monitors.MonitorAdapter;
import com.appnanny.android.monitors.MonitorStorage;
import com.appnanny.android.monitors.MonitorUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Fragment that registers scheduled Monitors.
 */
public class SchedulerFragment extends Fragment {

    private static final String FRAGMENT_MONITOR_PICKER_TAG = "fragment_monitor_picker";

    private MonitorAdapter mMonitorAdapter;
    private MonitorUtil mMonitorUtil;
    private TextView mTextViewIntroMessage;
    private BroadcastReceiver mMonitorWentOffBroadcastReceiver;
    private static final String TAG = "SchedulerFragment";

    public static SchedulerFragment newInstance() {
        SchedulerFragment fragment = new SchedulerFragment();
        Log.i(TAG, String.format("[SchedulerFragment] - End newInstance"));
        return fragment;
    }

    public SchedulerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMonitorWentOffBroadcastReceiver = new MonitorWentOffReceiver();
        Log.i(TAG, String.format("[SchedulerFragment] - End OnActivityCreated"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.i(TAG, String.format("[SchedulerFragment] - End OnCreateView"));
        return inflater.inflate(R.layout.fragment_monitor_scheduler, container, false);
    }

    @Override
    public void onViewCreated(final View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_add_alarm);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MonitorPickerFragment fragment = MonitorPickerFragment.newInstance();
                fragment.setMonitorAddListener(new MonitorAddListenerImpl());
                fragment.show(getFragmentManager(), FRAGMENT_MONITOR_PICKER_TAG);
            }
        });
        mTextViewIntroMessage = (TextView) rootView.findViewById(R.id.text_intro_message);
        Activity activity = getActivity();
        MonitorStorage monitorStorage = new MonitorStorage(activity);
        mMonitorAdapter = new MonitorAdapter(activity, monitorStorage.getMonitors());
        if (mMonitorAdapter.getItemCount() == 0) {
            mTextViewIntroMessage.setVisibility(View.VISIBLE);
        }
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_alarms);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(mMonitorAdapter);
        recyclerView.addItemDecoration(new MonitorAdapter.DividerItemDecoration(activity));
        mMonitorUtil = new MonitorUtil(activity);
        Log.i(TAG, String.format("[SchedulerFragment] - End OnViewCreated"));
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(mMonitorWentOffBroadcastReceiver);
        super.onDestroy();
        Log.i(TAG, String.format("[SchedulerFragment] - End onDestroy"));
    }



    /**
     * {@link MonitorPickerFragment.MonitorAddListener} to do actions after an Monitor is added.
     */
    private class MonitorAddListenerImpl implements MonitorPickerFragment.MonitorAddListener {

        @Override
        public void onMonitorAdded(Monitor monitor) {
            mMonitorAdapter.addMonitor(monitor);
            mMonitorUtil.scheduleMonitor(monitor);
            mTextViewIntroMessage.setVisibility(View.GONE);
            Log.i(TAG, String.format("[SchedulerFragment] - End onMonitorAdded"));
        }
    }

    /**
     * A {@link BroadcastReceiver} that receives an intent when an Monitor goes off.
     * This receiver removes the corresponding Monitor from the RecyclerView.
     */
    private class MonitorWentOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, String.format("[SchedulerFragment] - MonitorWentOffReceiver - End onReceive"));
        }
    }
}
