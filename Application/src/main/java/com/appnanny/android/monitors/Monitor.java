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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Objects;

/**
 * Class represents a single monitor.
 */
public class Monitor implements Comparable<Monitor>, Parcelable {

    public int id;
    public int type;
    public int month;
    public int date;
    /** Integer as a 24-hour format */
    public int hour;
    public int minute;

    public Monitor() {}

    protected Monitor(Parcel in) {
        id = in.readInt();
        type = in.readInt();
        month = in.readInt();
        date = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
    }

    public static final Creator<Monitor> CREATOR = new Creator<Monitor>() {
        @Override
        public Monitor createFromParcel(Parcel in) {
            return new Monitor(in);
        }

        @Override
        public Monitor[] newArray(int size) {
            return new Monitor[size];
        }
    };

    public int getType(){
        return this.type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(type);
        parcel.writeInt(month);
        parcel.writeInt(date);
        parcel.writeInt(hour);
        parcel.writeInt(minute);
    }

    /**
     * Serialize the instance as a JSON String.
     * @return serialized JSON String.
     */
    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("type", type);
            jsonObject.put("month", month);
            jsonObject.put("date", date);
            jsonObject.put("hour", hour);
            jsonObject.put("minute", minute);
        } catch (JSONException e) {
            throw new IllegalStateException("Failed to convert the object to JSON");
        }
        return jsonObject.toString();
    }

    /**
     * Parses a Json string to an {@link Monitor} instance.
     *
     * @param string The String representation of an Monitor
     * @return an instance of {@link Monitor}
     */
    public static Monitor fromJson(String string) {
        JSONObject jsonObject;
        Monitor monitor = new Monitor();
        try {
            jsonObject = new JSONObject(string);
            monitor.id = jsonObject.getInt("id");
            monitor.type = jsonObject.getInt("type");
            monitor.month = jsonObject.getInt("month");
            monitor.date = jsonObject.getInt("date");
            monitor.hour = jsonObject.getInt("hour");
            monitor.minute = jsonObject.getInt("minute");
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to parse the String: " + string);
        }

        return monitor;
    }

    public String getTypeText(){

        switch(type){
            case 1: return "Wifi Monitor";
            case 2: return "Bluetooth Mon";
            case 3: return "Network Mon";
            case 4: return "SD Card Mon";
            case 5: return "Battery Mon";
            case 6: return "Accounts Mon";
            default: return "Unknown";
        }
    }

    @Override
    public String toString() {
        return "Monitor{" +
                "id=" + id +
                ", type=" + type +
                ", month=" + month +
                ", date=" + date +
                ", hour=" + hour +
                ", minute=" + minute +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Monitor)) {
            return false;
        }
        Monitor monitor = (Monitor) o;
        return id == monitor.id &&
                type == monitor.type &&
                month == monitor.month &&
                date == monitor.date &&
                hour == monitor.hour &&
                minute == monitor.minute;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, month, date, hour, minute);
    }

    @Override
    public int compareTo(@NonNull Monitor other) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Calendar otherCal = Calendar.getInstance();
        otherCal.set(Calendar.MONTH, other.month);
        otherCal.set(Calendar.DATE, other.date);
        otherCal.set(Calendar.HOUR_OF_DAY, other.hour);
        otherCal.set(Calendar.MINUTE, other.minute);
        return calendar.compareTo(otherCal);
    }
}
