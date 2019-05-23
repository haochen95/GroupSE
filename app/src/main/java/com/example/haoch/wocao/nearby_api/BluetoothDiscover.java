package com.example.haoch.wocao.nearby_api;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BluetoothDiscover extends Service {

    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final String NEARBY = "nearby device";
    private String folder_from_sensor;
    ArrayList<String> stringArrayList = new ArrayList<String>();


    @Override
    public void onCreate() {


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(myReceiver, intentFilter);

        // start function
        btAdapter.startDiscovery();
        Log.i("Smartphone DDDDD", "startDiscovery ");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    BroadcastReceiver myReceiver = new BroadcastReceiver() {

        private List<BluetoothItem> dataList = new ArrayList<>();

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

//                if (! device.getName().equals("RECO")){
                    short rssis = intent.getExtras().getShort(
                            BluetoothDevice.EXTRA_RSSI);
                    String Devicename = device.getName();
                    String mac = device.getAddress();
                    Integer rssi = Integer.valueOf(rssis+"");
                    String time = System.currentTimeMillis()+"";
                    BluetoothItem bluetoothItem = new BluetoothItem(Devicename,mac,rssi,time);
                    Log.e("bluetooth",bluetoothItem.toString());
                    dataList.add(bluetoothItem);
//                }

                Log.i("Smartphone DDDDD", "how many?? " + dataList.size());

                // send data to mainUI
//                Intent near = new Intent(NEARBY);
//                near.putExtra("Nearby", (Serializable)dataList);
//                sendBroadcast(intent);

            }
        }
    };


}
