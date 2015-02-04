
package com.example.android.wifidirect.discovery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.wifidirect.discovery.dialog.TabChoosedDialog;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * A simple ListFragment that shows the available services as published by the
 * peers
 */
public class WiFiDirectServicesList extends ListFragment {

    private static final int TABCHOOSER = 5; //numero costante scelto a caso
    private WiFiDevicesAdapter listAdapter = null;

    interface DeviceClickListener {
        public void connectP2p(WiFiP2pService wifiP2pService, int tabNum);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.devices_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listAdapter = new WiFiDevicesAdapter(this.getActivity(),
                android.R.layout.simple_list_item_2, android.R.id.text1,
                ServiceList.getInstance().getServiceList());
        setListAdapter(listAdapter);


//        ((WiFiServiceDiscoveryActivity) getActivity()).setupToolBar();

//        statusTxtView = (TextView) this.getActivity().findViewById(R.id.status_text);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        TabChoosedDialog tabChoosedDialog = (TabChoosedDialog) getFragmentManager().findFragmentByTag("tabchooserdialog");

        if (tabChoosedDialog == null) {
            tabChoosedDialog = TabChoosedDialog.newInstance(position);

            tabChoosedDialog.setTargetFragment(this, TABCHOOSER);

            tabChoosedDialog.show(getFragmentManager(), "tabchooserdialog");
            getFragmentManager().executePendingTransactions();
        }




    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TABCHOOSER:
                if (resultCode == Activity.RESULT_OK) {
                    // After Ok code.
                    Bundle bundle = data.getExtras();
                    int tabnum = bundle.getInt("tab");
                    int position = bundle.getInt("position");
                    Log.d("Tabchooseddialog", "tabnum: " + tabnum + ", position: " + position);


                    ((DeviceClickListener) getActivity()).connectP2p((WiFiP2pService)getListView().getItemAtPosition(position),tabnum);
                    ((TextView) this.getView().findViewById(android.R.id.text2)).setText("Connecting");


                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // After Cancel code.
                    Log.d("Tabchooserdialog", "Non ho premuto i pulsanti");
                }

                break;
            default:
                break;
        }

    }


    public class WiFiDevicesAdapter extends ArrayAdapter<WiFiP2pService> {

//        private List<WiFiP2pService> items;

        public WiFiDevicesAdapter(Context context, int resource,
                                  int textViewResourceId, List<WiFiP2pService> items) {
            super(context, resource, textViewResourceId, items);
//            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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


//    public void appendStatus(String status) {
//        String current = statusTxtView.getText().toString();
//        statusTxtView.setText(current + "\n" + StatusText.getInstance().getStatus());
//    }

}
