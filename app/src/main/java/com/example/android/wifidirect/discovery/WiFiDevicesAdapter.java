package com.example.android.wifidirect.discovery;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Stefano Cappa on 10/02/15.
 */
public class WiFiDevicesAdapter extends RecyclerView.Adapter<WiFiDevicesAdapter.ViewHolder> {

    //        private List<WiFiP2pService> items;
    private Activity context;
    private ItemClickListener itemClickListener;

    public WiFiDevicesAdapter(Activity context, @NonNull ItemClickListener itemClickListener) {
//        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.itemClickListener = itemClickListener;
        setHasStableIds(true);
    }


    /**
     * Classe statica viewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View parent;
        private TextView nameText;
        private TextView statusText;

        private Activity context;

        public ViewHolder(View view, Activity context) {
            super(view);

            this.context = context;
            this.parent = view;

            nameText = (TextView) view.findViewById(R.id.text1);
            statusText = (TextView) view.findViewById(R.id.text2);
        }


        public void setOnClickListener(View.OnClickListener listener) {
            parent.setOnClickListener(listener);
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.service_row, viewGroup, false);

        return new ViewHolder(v,context);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        WiFiP2pService service = ServiceList.getInstance().getServiceList().get(position);
        if (service != null) {
            viewHolder.nameText.setText(service.device.deviceName + " - " + service.instanceName);
            viewHolder.statusText.setText(getDeviceStatus(service.device.status));
        }

        viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v);
            }
        });
    }


    @Override
    public int getItemCount() {
        return ServiceList.getInstance().getServiceList().size();
    }

    public interface ItemClickListener {
        void itemClicked(final View view);
    }
//
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View v = convertView;
//        if (v == null) {
//            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            v = vi.inflate(android.R.layout.simple_list_item_2, null);
//        }
//        WiFiP2pService service = ServiceList.getInstance().getServiceList().get(position);
//        if (service != null) {
//            TextView nameText = (TextView) v
//                    .findViewById(android.R.id.text1);
//
//            if (nameText != null) {
//                nameText.setText(service.device.deviceName + " - " + service.instanceName);
//            }
//            TextView statusText = (TextView) v
//                    .findViewById(android.R.id.text2);
//            statusText.setText(getDeviceStatus(service.device.status));
//        }
//        return v;
//    }

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