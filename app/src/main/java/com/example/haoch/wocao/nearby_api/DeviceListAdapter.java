package com.example.haoch.wocao.nearby_api;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.haoch.wocao.R;

import java.util.ArrayList;
import java.util.List;

public class DeviceListAdapter extends BaseAdapter {

    private List<BluetoothItem> bluetoothItems;
    private LayoutInflater mLayoutInflater;

    public DeviceListAdapter(Context context) {
        super();
        bluetoothItems = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void updateDevice(BluetoothItem item) {
        synchronized (bluetoothItems) {
            if(bluetoothItems.contains(item)) {
                bluetoothItems.remove(item);
            }
            bluetoothItems.add(item);
        }
    }

    public void updateAllDevice(List<BluetoothItem> allItems) {
        synchronized (allItems) {
            bluetoothItems = new ArrayList<BluetoothItem>(allItems);
        }
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.device_adapter, parent, false);
            viewHolder = new DeviceListAdapter.ViewHolder();
            viewHolder.deviceName = (TextView)convertView.findViewById(R.id.deviceName);
            viewHolder.deviceRSSI = (TextView)convertView.findViewById(R.id.deviceRSSI);
            viewHolder.deviceMac = (TextView)convertView.findViewById(R.id.deviceMAC);
            viewHolder.deviceTime = (TextView)convertView.findViewById(R.id.deviceTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        BluetoothItem items = bluetoothItems.get(position);


        viewHolder.deviceName.setText(items.getName());
        viewHolder.deviceRSSI.setText(String.valueOf(items.getRssi()));
        viewHolder.deviceMac.setText(items.getMac());
        viewHolder.deviceTime.setText(items.getTime());

        return convertView;
    }


    static class ViewHolder {
        TextView deviceName;
        TextView deviceRSSI;
        TextView deviceMac;
        TextView deviceTime;
    }
}
