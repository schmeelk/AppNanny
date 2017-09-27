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

import com.appnanny.android.MonitorPickerFragment;
import com.appnanny.android.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.bluetooth.*;
import android.net.Network;
import android.os.BatteryManager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;
import java.util.Iterator;
import android.util.Log;

import static android.net.ConnectivityManager.TYPE_WIFI;


/**
 * Adapter responsible for interactions between the {@link RecyclerView} and the
 * scheduled monitor.
 */
public class MonitorAdapter extends RecyclerView.Adapter<MonitorAdapter.MonitorViewHolder> {

    private static final String FRAGMENT_MONITOR_DISPLAY_TAG = "fragment_monitor_display";
    private static final String TAG = "MonitorAdapter";

    private SortedList<Monitor> mMonitorList;
    private MonitorStorage mMonitorStorage;
    private MonitorUtil mMonitorUtil;
    private DateFormat mDateFormat;
    private DateFormat mTimeFormat;
    private Context mContext;

    public MonitorAdapter(Context context, Set<Monitor> monitors) {
        mMonitorList = new SortedList<>(Monitor.class, new SortedListCallback());
        mMonitorList.addAll(monitors);
        mMonitorStorage = new MonitorStorage(context);
        mContext = context;
        mMonitorUtil = new MonitorUtil(context);
        mDateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        mTimeFormat = new SimpleDateFormat("kk:mm", Locale.getDefault());
    }

    @Override
    public MonitorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.monitor_row, parent, false);
        return new MonitorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MonitorViewHolder holder, final int position) {
        Monitor monitor = mMonitorList.get(position);
        Calendar monitorTime = Calendar.getInstance();
        monitorTime.set(Calendar.MONTH, monitor.month);
        monitorTime.set(Calendar.DATE, monitor.date);
        monitorTime.set(Calendar.HOUR_OF_DAY, monitor.hour);
        monitorTime.set(Calendar.MINUTE, monitor.minute);
        holder.mMonitorTypeTextView
                .setText(monitor.getTypeText());
        holder.mMonitorTimeTextView
                .setText(mTimeFormat.format(monitorTime.getTime()));
        holder.mMonitorDateTextView
                .setText(mDateFormat.format(monitorTime.getTime()));
        holder.mShowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Monitor toBeShown = mMonitorList.get(position);
                Log.i(TAG, String.format("Clicked on Bot"));
                clickedOnBot(toBeShown.getType());

            }
        });
        holder.mDeleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Monitor toBeDeleted = mMonitorList.get(position);
                mMonitorList.removeItemAt(position);
                mMonitorStorage.deleteMonitor(toBeDeleted);
                mMonitorUtil.cancelMonitor(toBeDeleted);
                notifyDataSetChanged();
                Toast.makeText(mContext, mContext.getString(R.string.alarm_deleted,
                        toBeDeleted.hour, toBeDeleted.minute), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clickedOnBot(int type){
        switch(type){
            case 1: displayBotWifi();return;
            case 2: displayBotBlue();return;
            case 3: displayBotNetwork();return;
            case 4: displayBotSD();return;
            case 5: displayBotBattery();return;
            case 6: displayBotAccounts();return;
        }
    }

    public void displayBotBlue(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        Log.i(TAG, String.format("Retrieved AllConnected Devices"));
        StringBuilder b = new StringBuilder();
        if(pairedDevices.isEmpty())
            b.append("No bluetooth devices connected.\n");
        Iterator itr = pairedDevices.iterator();
        while(itr.hasNext()) {
            String s = itr.next().toString() + "\n";
            b.append(s);
            Log.i(TAG, String.format(s));
        }
        Log.i(TAG, String.format("Toasting Bluetooth"));
        Toast.makeText(mContext, b.toString(), Toast.LENGTH_LONG).show();
    }

    public void displayBotNetwork(){
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        Log.i(TAG, String.format("Retrieved connMgr"));
        Network[] networks = connMgr.getAllNetworks();
        Log.i(TAG, String.format("Retrieved AllNetworks"));
        StringBuilder b = new StringBuilder();
        for(int i =0; i < networks.length; i++) {
            b.append(connMgr.getNetworkInfo(networks[i]).toString() + "/n");
            Log.i(TAG, String.format(networks[i].toString()));
            Log.i(TAG, String.format(connMgr.getNetworkInfo(networks[i]).toString()));
        }
        if(networks.length == 0)
            b.append("Not connected to any networks.\n");
        Log.i(TAG, String.format("Toasting Network"));
        Toast.makeText(mContext, b.toString(), Toast.LENGTH_LONG).show();
    }

    public void displayBotSD(){
        String state = Environment.getExternalStorageState();
        StringBuilder b = new StringBuilder();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            b.append("No media mounted on device \n");
        }
        state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            b.append("SD Card is Read Only \n");
        }

        File datadir = Environment.getExternalStorageDirectory();
        b.append("Data directory is "+datadir.toString() +"\n");
        b.append("Free Space is " + datadir.getFreeSpace() + "\n");


        datadir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        b.append("Picture data directory is "+datadir.toString() +"\n");
        b.append("Picture free space is " + datadir.getFreeSpace() + "\n");

        Log.i(TAG, String.format("Toasting SD Card"));
        Toast.makeText(mContext, b.toString(), Toast.LENGTH_LONG).show();
    }

    public void displayBotBattery(){
        Log.i(TAG, String.format("DisplayBattery"));
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, ifilter);
        Log.i(TAG, String.format("Create contents"));
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
        if(status == BatteryManager.BATTERY_HEALTH_OVERHEAT)
            b.append("Device Battery Health Over Heat \n");
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
        Log.i(TAG, String.format("Toasting Battery"));
        Toast.makeText(mContext, b.toString(), Toast.LENGTH_LONG).show();
    }

    public void displayBotWifi(){
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        Log.i(TAG, String.format("Retrieved connMgr"));
        Network[] networks = connMgr.getAllNetworks();
        Log.i(TAG, String.format("Retrieved AllNetworks"));
        StringBuilder b = new StringBuilder();
        int count = 0;
        NetworkInfo ni;
        for(int i =0; i < networks.length; i++) {
            ni = connMgr.getNetworkInfo(networks[i]);
            if(ni.getType()== TYPE_WIFI) {
                b.append(ni.toString() + "/n");
                Log.i(TAG, String.format(networks[i].toString()));
                Log.i(TAG, String.format(connMgr.getNetworkInfo(networks[i]).toString()));
                count++;
            }
        }
        if(networks.length == 0 || count == 0)
            b.append("Not connected to any wifi networks.\n");
        Log.i(TAG, String.format("Toasting WiFi"));
        Toast.makeText(mContext, b.toString(), Toast.LENGTH_LONG).show();
    }

    public void displayBotAccounts(){
        //AccountManager am = AccountManager.get(this); // "this" references the current Context
        //Account[] accounts = am.getAccountsByType("com.google");
        //PackageManager pm = this.getPackageManager();
        //int ga = pm.checkPermission("android.permission.GET_ACCOUNTS", this.getPackageName());
        //checkPermission("android.permission.GET_ACCOUNTS", successCallback, errorCallback);
        StringBuilder b = new StringBuilder();
        b.append("[AccountsMonServ]: "+"\n");
        //for (int i = 0; i < accounts.length; i++){
        //    b.append(accounts[i].name + " " + accounts[i].type+"\n");
        //}
        Log.i(TAG, String.format("Toasting Accounts"));
        Toast.makeText(mContext, b.toString(), Toast.LENGTH_LONG).show();
    }


    @Override
    public int getItemCount() {
        return mMonitorList.size();
    }

    public void addMonitor(Monitor monitor) {
        mMonitorList.add(monitor);
        notifyDataSetChanged();
    }

    public void deleteMonitor(Monitor monitor) {
        mMonitorList.remove(monitor);
        notifyDataSetChanged();
    }

    public static class MonitorViewHolder extends RecyclerView.ViewHolder {

        private TextView mMonitorTimeTextView;
        private TextView mMonitorTypeTextView;
        private TextView mMonitorDateTextView;
        private ImageView mShowImageView;
        private ImageView mDeleteImageView;

        public MonitorViewHolder(View itemView) {
            super(itemView);
            mMonitorTypeTextView = (TextView) itemView.findViewById(R.id.text_alarm_type);
            mMonitorTimeTextView = (TextView) itemView.findViewById(R.id.text_alarm_time);
            mMonitorDateTextView = (TextView) itemView.findViewById(R.id.text_alarm_date);
            mShowImageView = (ImageView) itemView.findViewById(R.id.image_show_alarm);
            mDeleteImageView = (ImageView) itemView.findViewById(R.id.image_delete_alarm);
        }
    }


    private static class SortedListCallback extends SortedList.Callback<Monitor> {

        @Override
        public int compare(Monitor o1, Monitor o2) {
            return o1.compareTo(o2);
        }

        @Override
        public void onInserted(int position, int count) {
            //No op
        }

        @Override
        public void onRemoved(int position, int count) {
            //No op
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            //No op
        }

        @Override
        public void onChanged(int position, int count) {
            //No op
        }

        @Override
        public boolean areContentsTheSame(Monitor oldItem, Monitor newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Monitor item1, Monitor item2) {
            return item1.equals(item2);
        }
    }

    /**
     * ItemDecoration that draws an divider between items in a RecyclerView.
     */
    public static class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable mDivider;

        public DividerItemDecoration(Context context) {
            mDivider = context.getDrawable(R.drawable.divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
