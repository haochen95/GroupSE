package com.example.haoch.wocao;


import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.haoch.wocao.nearby_api.BluetoothDiscover;
import com.example.haoch.wocao.nearby_api.BluetoothItem;
import com.example.haoch.wocao.nearby_api.DeviceListAdapter;
import com.example.haoch.wocao.sensor_api.SensingService;
import com.perples.recosdk.RECOBeacon;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyDeviceFragment extends Fragment {

    DeviceListAdapter deviceListAdapter;
    TextView nearby_device_number;
    ListView device_list;
    List<BluetoothItem> target_device;

    private static final String NEARBY = "nearby device";

    public NearbyDeviceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_nearby_device, container, false);
        nearby_device_number = (TextView)view.findViewById(R.id.nearby_device_number);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }




}
