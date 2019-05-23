package com.example.haoch.wocao.beacon_api;

import java.util.Locale;

/**
 * Created by haoch on 2018/1/2.
 */

public class Blesave {

    private int rssi1, rssi2, rssi3;
    private int major, minor1,minor2, minor3;
    private double accuracy1, accuracy2, accuracy3;
    private long timestamp, timeInternal;
    private String uuid;

    public Blesave(long timestamp, long timeInternal, String uuid, int major,
                   int minor1, double accuracy1, int rssi1,
                   int minor2, double accuracy2, int rssi2,
                   int minor3, double accuracy3, int rssi3){

        this.timestamp = timestamp;
        this.timeInternal = timeInternal;
        this.uuid = uuid;
        this.major = major;
        this.minor1 = minor1;
        this.minor2 = minor2;
        this.minor3 = minor3;
        this.accuracy1 = accuracy1;
        this.accuracy2 = accuracy2;
        this.accuracy3 = accuracy3;
        this.rssi1 = rssi1;
        this.rssi2 = rssi2;
        this.rssi3 = rssi3;
    }

    public String getDataInCSV() {
        return String.format(Locale.KOREA, "%d,%d,%s,%d,%d,%f,%d,%d,%f,%d,%d,%f,%d",
                this.timestamp, this.timeInternal, this.uuid, this.major,
                this.minor1, this.accuracy1,this.rssi1,
                this.minor2, this.accuracy2,this.rssi2,
                this.minor3, this.accuracy3,this.rssi3);
    }
}
