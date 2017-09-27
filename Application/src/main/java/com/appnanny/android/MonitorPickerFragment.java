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

import com.appnanny.android.monitors.BluetoothMonitoringService;
import com.appnanny.android.monitors.Monitor;
import com.appnanny.android.monitors.MonitorStorage;
import com.appnanny.android.monitors.MonitorUtil;
import com.appnanny.android.monitors.BatteryMonitoringService;
import com.appnanny.android.monitors.NetworkMonitoringService;
import com.appnanny.android.monitors.SDCardMonitoringService;
import com.appnanny.android.monitors.WiFiMonitoringService;
import com.appnanny.android.monitors.AccountsMonitoringService;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.util.Log;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.lang.Integer;
import android.widget.ListView;
import android.widget.ArrayAdapter;

/**
 * DialogFragment for showing a MonitorPicker.
 */
public class MonitorPickerFragment extends DialogFragment {

    private ArrayAdapter adapter;
    private ListView mListView;
    private MonitorStorage mMonitorStorage;
    private MonitorAddListener mMonitorAddListener;
    private MonitorUtil mMonitorUtil;
    private static final String TAG = "MonitorPickerFragment";
    //String[] mobileArray = {"Bluetooth","Battery","NetworkConnections","Vibrate", "WiFi"};
    public final static String EXTRA_MESSAGE = "appnanny.monitoring.app";
    private static final int WIFI = 1;
    private static final int BLUETOOTH = 2;
    private static final int NETWORK = 3;
    private static final int SD = 4;
    private static final int BATTERY = 5;
    private static final int ACCOUNTS = 6;


    public MonitorPickerFragment() {}

    public static MonitorPickerFragment newInstance() {
        return new MonitorPickerFragment();
    }

    public void setMonitorAddListener(MonitorAddListener listener) {
        mMonitorAddListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMonitorStorage = new MonitorStorage(getActivity());
        mMonitorUtil = new MonitorUtil(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_picker, container, false);

        Button buttonWifi = (Button) view.findViewById(R.id.wifi_button);
        buttonWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateFormat dh = new SimpleDateFormat("hh");
                DateFormat dm = new SimpleDateFormat("mm");
                String hour = dh.format(Calendar.getInstance().getTime());
                String min = dm.format(Calendar.getInstance().getTime());
                Integer i = new Integer(hour);
                Integer j = new Integer(min);
                Calendar monitorTime = mMonitorUtil
                        .getNextMonitorTime(WIFI, i.intValue(), j.intValue()); //mTimePicker.getHour(), mTimePicker.getMinute());
                if(monitorTime != null){
                    Monitor monitor = mMonitorStorage
                            .saveMonitor(WIFI, monitorTime.get(Calendar.MONTH), monitorTime.get(Calendar.DATE),
                                    monitorTime.get(Calendar.HOUR_OF_DAY), monitorTime.get(Calendar.MINUTE));

                    String monitorSavedString = "Adding New Watchdog Event";
                    Toast.makeText(getActivity(), monitorSavedString, Toast.LENGTH_SHORT).show();
                if (mMonitorAddListener != null) {
                    mMonitorAddListener.onMonitorAdded(monitor);
                    Log.i(TAG, String.format("Starting Activity for WiFi Monitoring"));
                    getActivity().startService(new Intent(getActivity(),WiFiMonitoringService.class));
                    Log.i(TAG, String.format("Ending start service for WiFi Monitoring from MonitorPickerFragment"));
                    /*Intent intent = new Intent();
                    intent.setClass(getActivity(), WiFiMonitoringService.class);
                    String message = "Starting WiFi Monitoring Service from MonitorPickerFragment";
                    intent.putExtra(EXTRA_MESSAGE, message);
                    getActivity().startService(intent);
                    */
                    //WiFiMonitoringService wms = ((WiFiMonitoringService.LocalBinder)service).getService();
                    //startActivity(intent);
                }}else {
                    String monitorSavedString = "Only one monitor allowed per concern.";
                    Toast.makeText(getActivity(), monitorSavedString, Toast.LENGTH_SHORT).show();
                }

                dismiss();
            }
        });

        Button buttonBluetooth = (Button) view.findViewById(R.id.bluetooth_button);
        buttonBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateFormat dh = new SimpleDateFormat("hh");
                DateFormat dm = new SimpleDateFormat("mm");
                String hour = dh.format(Calendar.getInstance().getTime());
                String min = dm.format(Calendar.getInstance().getTime());
                Integer i = new Integer(hour);
                Integer j = new Integer(min);
                Calendar monitorTime = mMonitorUtil
                        .getNextMonitorTime(BLUETOOTH, i.intValue(), j.intValue()); //mTimePicker.getHour(), mTimePicker.getMinute());
                if(monitorTime != null){
                Monitor monitor = mMonitorStorage
                        .saveMonitor(BLUETOOTH, monitorTime.get(Calendar.MONTH), monitorTime.get(Calendar.DATE),
                                monitorTime.get(Calendar.HOUR_OF_DAY), monitorTime.get(Calendar.MINUTE));
                String monitorSavedString = "Adding New Watchdog Event";
                Toast.makeText(getActivity(), monitorSavedString, Toast.LENGTH_SHORT).show();
                if (mMonitorAddListener != null) {
                    mMonitorAddListener.onMonitorAdded(monitor);
                    Log.i(TAG, String.format("Starting Activity for Bluetooth Monitoring"));
                    getActivity().startService(new Intent(getActivity(), BluetoothMonitoringService.class));
                    Log.i(TAG, String.format("Ending start service for Bluetooth Monitoring from MonitorPickerFragment"));
                }}else {
                    String monitorSavedString = "Only one monitor allowed per concern.";
                    Toast.makeText(getActivity(), monitorSavedString, Toast.LENGTH_SHORT).show();
              }

                dismiss();
            }
        });


        Button buttonNetwork = (Button) view.findViewById(R.id.network_button);
        buttonNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateFormat dh = new SimpleDateFormat("hh");
                DateFormat dm = new SimpleDateFormat("mm");
                String hour = dh.format(Calendar.getInstance().getTime());
                String min = dm.format(Calendar.getInstance().getTime());
                Integer i = new Integer(hour);
                Integer j = new Integer(min);
                Calendar monitorTime = mMonitorUtil
                        .getNextMonitorTime(NETWORK, i.intValue(), j.intValue()); //mTimePicker.getHour(), mTimePicker.getMinute());
                if(monitorTime != null){
                Monitor monitor = mMonitorStorage
                        .saveMonitor(NETWORK, monitorTime.get(Calendar.MONTH), monitorTime.get(Calendar.DATE),
                                monitorTime.get(Calendar.HOUR_OF_DAY), monitorTime.get(Calendar.MINUTE));
                String monitorSavedString = "Adding New Watchdog Event";
                Toast.makeText(getActivity(), monitorSavedString, Toast.LENGTH_SHORT).show();
                if (mMonitorAddListener != null) {
                    mMonitorAddListener.onMonitorAdded(monitor);
                    Log.i(TAG, String.format("Starting Activity for Network Monitoring"));
                    getActivity().startService(new Intent(getActivity(), NetworkMonitoringService.class));
                    Log.i(TAG, String.format("Ending start service for Network Monitoring from MonitorPickerFragment"));
                }}else {
                    String monitorSavedString = "Only one monitor allowed per concern.";
                    Toast.makeText(getActivity(), monitorSavedString, Toast.LENGTH_SHORT).show();
                }

                dismiss();
            }
        });

        Button buttonSDCard = (Button) view.findViewById(R.id.sdcard_button);
        buttonSDCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateFormat dh = new SimpleDateFormat("hh");
                DateFormat dm = new SimpleDateFormat("mm");
                String hour = dh.format(Calendar.getInstance().getTime());
                String min = dm.format(Calendar.getInstance().getTime());
                Integer i = new Integer(hour);
                Integer j = new Integer(min);
                Calendar monitorTime = mMonitorUtil
                        .getNextMonitorTime(SD, i.intValue(), j.intValue()); //mTimePicker.getHour(), mTimePicker.getMinute());
                if(monitorTime != null){
                Monitor monitor = mMonitorStorage
                        .saveMonitor(SD, monitorTime.get(Calendar.MONTH), monitorTime.get(Calendar.DATE),
                                monitorTime.get(Calendar.HOUR_OF_DAY), monitorTime.get(Calendar.MINUTE));
                String monitorSavedString = "Adding New Watchdog Event";
                Toast.makeText(getActivity(), monitorSavedString, Toast.LENGTH_SHORT).show();
                if (mMonitorAddListener != null) {
                    mMonitorAddListener.onMonitorAdded(monitor);
                    Log.i(TAG, String.format("Starting Activity for SDCard Monitoring"));
                    getActivity().startService(new Intent(getActivity(), SDCardMonitoringService.class));
                    Log.i(TAG, String.format("Ending start service for SDCArd Monitoring from MonitorPickerFragment"));

                }}else {
                    String monitorSavedString = "Only one monitor allowed per concern.";
                    Toast.makeText(getActivity(), monitorSavedString, Toast.LENGTH_SHORT).show();
                }

                dismiss();
            }
        });


        Button buttonBattery = (Button) view.findViewById(R.id.battery_button);
        buttonBattery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateFormat dh = new SimpleDateFormat("hh");
                DateFormat dm = new SimpleDateFormat("mm");
                String hour = dh.format(Calendar.getInstance().getTime());
                String min = dm.format(Calendar.getInstance().getTime());
                Integer i = new Integer(hour);
                Integer j = new Integer(min);
                Calendar monitorTime = mMonitorUtil
                        .getNextMonitorTime(BATTERY, i.intValue(), j.intValue()); //mTimePicker.getHour(), mTimePicker.getMinute());
                if(monitorTime != null){
                    Monitor monitor = mMonitorStorage
                            .saveMonitor(BATTERY, monitorTime.get(Calendar.MONTH), monitorTime.get(Calendar.DATE),
                                    monitorTime.get(Calendar.HOUR_OF_DAY), monitorTime.get(Calendar.MINUTE));

                String monitorSavedString = "Adding New Watchdog Event";
                Toast.makeText(getActivity(), monitorSavedString, Toast.LENGTH_SHORT).show();
                if (mMonitorAddListener != null) {
                    mMonitorAddListener.onMonitorAdded(monitor);
                    Log.i(TAG, String.format("Starting Activity for Battery Monitoring"));
                    getActivity().startService(new Intent(getActivity(),BatteryMonitoringService.class));
                    Log.i(TAG, String.format("Ending start service for Battery Monitoring from MonitorPickerFragment"));
                    /*Log.i(TAG, String.format("Starting Activity for Battery Monitoring"));
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), BatteryMonitoringService.class);
                    String message = "Starting Battery Monitoring Service from MonitorPickerFragment";
                    intent.putExtra(EXTRA_MESSAGE, message);
                    getActivity().startService(intent);
                    //startActivity(intent);
                    Log.i(TAG, String.format("Ending start service for Battery Monitoring from MonitorPickerFragment"));*/
                }}else {
                String monitorSavedString = "Only one monitor allowed per concern.";
                Toast.makeText(getActivity(), monitorSavedString, Toast.LENGTH_SHORT).show();
            }

                dismiss();
            }
        });

        Button buttonAccount = (Button) view.findViewById(R.id.accounts_button);
        buttonAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateFormat dh = new SimpleDateFormat("hh");
                DateFormat dm = new SimpleDateFormat("mm");
                String hour = dh.format(Calendar.getInstance().getTime());
                String min = dm.format(Calendar.getInstance().getTime());
                Integer i = new Integer(hour);
                Integer j = new Integer(min);
                Calendar monitorTime = mMonitorUtil
                        .getNextMonitorTime(ACCOUNTS, i.intValue(), j.intValue()); //mTimePicker.getHour(), mTimePicker.getMinute());
                if(monitorTime != null){
                    Monitor monitor = mMonitorStorage
                            .saveMonitor(ACCOUNTS, monitorTime.get(Calendar.MONTH), monitorTime.get(Calendar.DATE),
                                    monitorTime.get(Calendar.HOUR_OF_DAY), monitorTime.get(Calendar.MINUTE));

                    String monitorSavedString = "Adding New Watchdog Event";
                    Toast.makeText(getActivity(), monitorSavedString, Toast.LENGTH_SHORT).show();
                    if (mMonitorAddListener != null) {
                        mMonitorAddListener.onMonitorAdded(monitor);
                        Log.i(TAG, String.format("Starting Activity for Accounts Monitoring"));
                        getActivity().startService(new Intent(getActivity(), AccountsMonitoringService.class));
                        Log.i(TAG, String.format("Ending start service for Accounts Monitoring from MonitorPickerFragment"));
                    }}else {
                    String monitorSavedString = "Only one monitor allowed per concern.";
                    Toast.makeText(getActivity(), monitorSavedString, Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }
        });


        Button buttonCancel = (Button) view.findViewById(R.id.button_cancel_time_picker);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String monitorSavedString = "Canceling New Watchdog Event";
                Toast.makeText(getActivity(), monitorSavedString, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        //ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, listItems);
        //mListView.setAdapter(adapter);
        return view;
    }

    public interface MonitorAddListener {
        void onMonitorAdded(Monitor monitor);
    }
}
