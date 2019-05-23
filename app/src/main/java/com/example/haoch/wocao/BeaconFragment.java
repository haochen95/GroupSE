package com.example.haoch.wocao;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.haoch.wocao.beacon_api.Blesave;
import com.example.haoch.wocao.beacon_api.RecoBackgroundRangingService;
import com.example.haoch.wocao.beacon_api.RecoRangingListAdapter;
import com.example.haoch.wocao.nearby_api.BluetoothDiscover;
import com.example.haoch.wocao.sensor_api.SensingService;
import com.perples.recosdk.RECOBeacon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class BeaconFragment extends Fragment {

    // initiate class
    private Button startBT, stopBT;
    private TextView number_beacons;
    private RecoRangingListAdapter mRangingListAdapter;
    private ListView mRegionListView;
    private ArrayList<RECOBeacon> receivedBeacon;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private SensingService mSensingService;
    private PowerManager.WakeLock mWakeLock;  // when the screen shut down, I can still collect data


    private static final int REQUEST_ENABLE_BT = 1;
    private static final int permissions_code = 42;
    private boolean isBound = false;
    private static final String SIGNAL = "from service data";

    public BeaconFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beacon, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startBT = (Button)getActivity().findViewById(R.id.Start);
        stopBT = (Button)getActivity().findViewById(R.id.Stop);
        number_beacons = (TextView)getActivity().findViewById(R.id.number_of_beacon);
        checkBLE();

        startBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                acquireWakeLock();

                startBT.setText("Running");
                startBT.setEnabled(false);
                Thread t1 = new Thread(){
                    @Override
                    public void run() {
                        getActivity().startService(new Intent().setClass(getActivity(), RecoBackgroundRangingService.class));
                    }
                };
                    t1.start();

                Thread t2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent1 = new Intent(getActivity(), SensingService.class);

                        if (!isSensingServiceRunning()) {
                            getActivity().startService(intent1);
                        }
                        getActivity().bindService(intent1, mconnection, Context.BIND_AUTO_CREATE);
                    }
                });
                t2.start();

//                Thread t3 = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        getActivity().startService(new Intent().setClass(getActivity(), BluetoothDiscover.class));
//                        Log.i("SmartPhone Discover", "service open");
//                    }
//                });
//                t3.start();

            }
        });


        stopBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releaseWakeLock();
                if (startBT.getText().equals("Running")){
                    startBT.setText("Start");
                    startBT.setEnabled(true);
                } else if (startBT.getText().equals("Start")){
                    return;
                }

                getActivity().stopService(new Intent().setClass(getActivity(), RecoBackgroundRangingService.class));

//                getActivity().unbindService(mconnection);
                getActivity().stopService(new Intent().setClass(getActivity(), SensingService.class));
//                getActivity().stopService(new Intent().setClass(getActivity(), BluetoothDiscover.class));
                receivedBeacon.clear();
                updateUI(receivedBeacon);

            }
        });
    }

    // --- Wake Lock

    private void acquireWakeLock() {    // Service still run the smartphone's light shut down
        if ((mWakeLock == null) || (!mWakeLock.isHeld())) {
            PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
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
    public void onResume() {
        super.onResume();
        mRangingListAdapter = new RecoRangingListAdapter(getActivity().getApplicationContext());
        mRegionListView = (ListView) getActivity().findViewById(R.id.beacon_recycler);
        mRegionListView.setAdapter(mRangingListAdapter);
        getActivity().registerReceiver(UpdateUIBroadcastReceiver, new IntentFilter(SIGNAL));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(UpdateUIBroadcastReceiver);
    }

    @Override
    public void onStop() {
        // Unbind from service
        if (isBound) {
            getActivity().unbindService(mconnection);
            isBound = false;
        }
        super.onStop();
    }

    // Receive ranged beacon from RECOservice to update UI;
    BroadcastReceiver UpdateUIBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            receivedBeacon = intent.getParcelableArrayListExtra("RecoBeacon");
            updateUI(receivedBeacon);
        }
    };

    private void updateUI(ArrayList<RECOBeacon> recoBeacons){
        mRangingListAdapter.updateAllBeacons(recoBeacons);   // use collected beacon to update beacon
        mRangingListAdapter.notifyDataSetChanged();         // Modify data and add beacon into mRangedBeacon
        number_beacons.setText(String.valueOf(recoBeacons.size()) + " Beacons");
    }

    private void checkBLE(){
        // check if user opened bluetooth, if not, notify user
        // We also need ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION authority in order to use RECObeacon SDK;
        mBluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WAKE_LOCK,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO, Manifest.permission.WAKE_LOCK};
            ActivityCompat.requestPermissions(getActivity(), permissions, permissions_code);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT & resultCode == Activity.RESULT_CANCELED) {
            // if the request to turn on bluetooth is denied, the app will be finished
            getActivity().finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Sensor part

    private boolean isSensingServiceRunning() {

        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SensingService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
            return false;
    }

    private boolean isRECOServiceRunning() {

        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (RecoBackgroundRangingService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    // when user bind Sensingservice, it will call back this function and bind to service;
    private final ServiceConnection mconnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SensingService.LocalBinder binder = (SensingService.LocalBinder) service;
            mSensingService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

}
