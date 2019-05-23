package com.example.haoch.wocao.beacon_api;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECORangingListener;
import com.perples.recosdk.RECOServiceConnectListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class RecoBackgroundRangingService extends Service implements RECORangingListener, RECOServiceConnectListener{

    protected RECOBeaconManager mRecoManager;
    protected ArrayList<RECOBeaconRegion> mRegions;
    private int rssi1, rssi2, rssi3;
    private int major, minor1,minor2, minor3;
    private double accuracy1, accuracy2, accuracy3;
    private String uuid;
    private static String folder_from_sensor;
    private PowerManager.WakeLock mWakeLock;  // when the screen shut down, I can still collect data


    private static final Long FREQUENCY_BEACON = 1*1000L;
    private static final String SIGNAL = "from service data";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("foldername"));
        acquireWakeLock();
        mRecoManager = RECOBeaconManager.getInstance(getApplicationContext(), true, false);
        this.bindRECOService();
        return START_STICKY;
    }

    private void bindRECOService() {
        Log.i("BackRangingService", "bindRECOService()");

        mRegions = new ArrayList<RECOBeaconRegion>();
        this.generateBeaconRegion();

        mRecoManager.setRangingListener(this);
        mRecoManager.bind(this);
    }

    private void generateBeaconRegion() {
        Log.i("BackRangingService", "generateBeaconRegion()");

        RECOBeaconRegion recoRegion;
        recoRegion = new RECOBeaconRegion("24DDF411-8CF1-440C-87CD-E368DAF9C93E", "RECO Sample Region");
        mRegions.add(recoRegion);
    }

    @Override
    public void onServiceConnect() {
        Log.i("BackRangingService", "onServiceConnect()");
        this.startRangingWithRegion(mRegions);
    }
    @Override
    public void onDestroy() {
        Log.i("BackRangingService", "onDestroy()");
        releaseWakeLock();
        this.tearDown();
        super.onDestroy();
    }

    private void startRangingWithRegion(ArrayList<RECOBeaconRegion> regions){

        for (RECOBeaconRegion region: regions){
            try {
                mRecoManager.startRangingBeaconsInRegion(region);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopRangingWithRegion(ArrayList<RECOBeaconRegion> regions){

        for (RECOBeaconRegion region: regions){
            try {
                mRecoManager.stopRangingBeaconsInRegion(region);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void tearDown() {
        Log.i("BackRangingService", "tearDown()");
        this.stopRangingWithRegion(mRegions);

        try {
            mRecoManager.unbind();
        } catch (RemoteException e) {
            Log.e("BackRangingService", "RemoteException has occured while executing unbind()");
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceFail(RECOErrorCode recoErrorCode) {

    }

    // --- Wake Lock

    private void acquireWakeLock() {    // Service still run the smartphone's light shut down
        if ((mWakeLock == null) || (!mWakeLock.isHeld())) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
            mWakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }


    @Override
    public void didRangeBeaconsInRegion(Collection<RECOBeacon> beacons, RECOBeaconRegion region) {
        Log.i("BackRangingService", "didRangeBeaconsInRegion() - " + region.getUniqueIdentifier() + " with " + beacons.size() + " beacons");

        // send data through braodcast
        // send bacon to main_activity to update UI
        ArrayList<RECOBeacon> scanedBeacon = new ArrayList<RECOBeacon>(beacons);
        // rank beacon by RSSI

        Intent intent = new Intent(SIGNAL);
        intent.putParcelableArrayListExtra("RecoBeacon", scanedBeacon);
        sendBroadcast(intent);

        storeBeaconIntoCSV(scanedBeacon);
    }


    private void storeBeaconIntoCSV(ArrayList<RECOBeacon> scanedBeacon){

        // initiate beacon information
        if (scanedBeacon.size()>=3){
            uuid = scanedBeacon.get(0).getProximityUuid();
            major = scanedBeacon.get(0).getMajor();
            minor1 = scanedBeacon.get(0).getMinor();
            accuracy1 = scanedBeacon.get(0).getAccuracy();
            rssi1 = scanedBeacon.get(0).getRssi();
            minor2 = scanedBeacon.get(1).getMinor();
            accuracy2 = scanedBeacon.get(1).getAccuracy();
            rssi2 = scanedBeacon.get(1).getRssi();
            minor3 = scanedBeacon.get(2).getMinor();
            accuracy3 = scanedBeacon.get(2).getAccuracy();
            rssi3 = scanedBeacon.get(2).getRssi();
        } else {
            uuid = "Null";
            major = 0;
            minor1 = 0;
            accuracy1 = 0;
            rssi1 = 0;
            minor2 = 0;
            accuracy2 = 0;
            rssi2 = 0;
            minor3 = 0;
            accuracy3 = 0;
            rssi3 = 0;
        }

        Blesave bb = new Blesave(System.currentTimeMillis(), FREQUENCY_BEACON, uuid, major,
                minor1, accuracy1, rssi1,
                minor2, accuracy2, rssi2,
                minor3, accuracy3, rssi3);

        // make beacon information as a string (line by line)
        String dataline = bb.getDataInCSV() + "\n";

        // Put this string into CSV file
        // 1. find the folder to create beacon.csv file;
        File allfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GroupSE/");
        File[] subfiles = allfile.listFiles();

        ArrayList<String> names = new ArrayList<String>();
        for (File file:subfiles){
            names.add(file.getName().toString());
        }

        // find the file information, create csv file and put data into csv
        File blefiles = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GroupSE/" + folder_from_sensor);
        blefiles.mkdir();

        File file = new File(blefiles, "RECOBeacon.csv");
        FileOutputStream out = null;
        try{
            out = new FileOutputStream(file, true);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        try{
            out.write(dataline.getBytes());
        }catch (IOException e){
            e.printStackTrace();
        }
        try{
            out.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            folder_from_sensor = intent.getStringExtra("get_folder_name");
        }
    };

    @Override
    public void rangingBeaconsDidFailForRegion(RECOBeaconRegion recoBeaconRegion, RECOErrorCode recoErrorCode) {

    }

}
