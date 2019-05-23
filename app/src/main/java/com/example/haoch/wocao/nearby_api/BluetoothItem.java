package com.example.haoch.wocao.nearby_api;

import java.io.Serializable;
import java.util.Locale;

public class BluetoothItem implements Serializable{
    public String name;
    public String mac;
    public Integer rssi;
    public String time;

    public BluetoothItem(String name, String mac, Integer rssi, String time) {
        this.name = name;
        this.mac = mac;
        this.rssi = rssi;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getMac() {
        return mac;
    }

    public Integer getRssi() {
        return rssi;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format(Locale.KOREA, "%s,%s, %s, %d",
                this.time, this.name,this.mac, this.rssi);
    }
}
