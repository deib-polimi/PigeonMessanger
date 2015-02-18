package it.polimi.wifidirectmultichat.discovery.services;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.polimi.wifidirectmultichat.R;

/**
 * Class WiFiServicesAdapter with the new RecyclerView (Lollipop) and
 * {@link it.polimi.wifidirectmultichat.discovery.services.WiFiServicesAdapter.ViewHolder}
 * for performance reasons.
 * This class is the Adapter to represents data inside the {@link it.polimi.wifidirectmultichat.discovery.services.WiFiP2pServicesFragment}
 * Created by Stefano Cappa on 10/02/15.
 */
public class WiFiServicesAdapter extends RecyclerView.Adapter<WiFiServicesAdapter.ViewHolder> {

    private final ItemClickListener itemClickListener;

    /**
     * Constructor of the adapter.
     * @param itemClickListener ClickListener to obtain click actions over the recyclerview's elements.
     */
    public WiFiServicesAdapter(@NonNull ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        setHasStableIds(true);
    }

    /**
     * {@link it.polimi.wifidirectmultichat.discovery.services.WiFiP2pServicesFragment} implements this interface
     */
    public interface ItemClickListener {
        void itemClicked(final View view);
    }


    /**
     * The ViewHolder of this Adapter, useful to store e recycle element for performance reasons.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View parent;
        private final TextView nameText;
        private final TextView statusText;
        private final TextView macAddressText;

        public ViewHolder(View view) {
            super(view);

            this.parent = view;

            nameText = (TextView) view.findViewById(R.id.message);
            macAddressText = (TextView) view.findViewById(R.id.text2);
            statusText = (TextView) view.findViewById(R.id.text3);
        }


        public void setOnClickListener(View.OnClickListener listener) {
            parent.setOnClickListener(listener);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View v = layoutInflater.inflate(R.layout.service_row, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        WiFiP2pService service = ServiceList.getInstance().getElementByPosition(position);
        if (service != null) {
            viewHolder.nameText.setText(service.getDevice().deviceName + " - " + service.getInstanceName());
            viewHolder.macAddressText.setText(service.getDevice().deviceAddress);
            viewHolder.statusText.setText(getDeviceStatus(service.getDevice().status));
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
        return ServiceList.getInstance().getSize();
    }

    /**
     * Private method used by this adapter to obtain the status, from the status code.
     * @param statusCode int that represents the status code.
     * @return A String that represent the status associated to the statusCode.
     */
    private static String getDeviceStatus(int statusCode) {
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