package com.example.android.wifidirect.discovery;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Stefano Cappa on 10/02/15.
 */
public class WiFiDevicesAdapter extends ArrayAdapter<WiFiP2pService> {

//        private List<WiFiP2pService> items;
    private Context context;

    public WiFiDevicesAdapter(Context context, int resource,
                              int textViewResourceId, List<WiFiP2pService> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(android.R.layout.simple_list_item_2, null);
        }
        WiFiP2pService service = ServiceList.getInstance().getServiceList().get(position);
        if (service != null) {
            TextView nameText = (TextView) v
                    .findViewById(android.R.id.text1);

            if (nameText != null) {
                nameText.setText(service.device.deviceName + " - " + service.instanceName);
            }
            TextView statusText = (TextView) v
                    .findViewById(android.R.id.text2);
            statusText.setText(getDeviceStatus(service.device.status));
        }
        return v;
    }

    public static String getDeviceStatus(int statusCode) {
        switch (statusCode) {
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }
}