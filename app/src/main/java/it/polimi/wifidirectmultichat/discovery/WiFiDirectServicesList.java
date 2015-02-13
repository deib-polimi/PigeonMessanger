
package it.polimi.wifidirectmultichat.discovery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.polimi.wifidirectmultichat.discovery.dialog.TabChoosedDialog;

import it.polimi.wifidirectmultichat.R;
import lombok.Getter;

/**
 * A simple ListFragment that shows the available services as published by the
 * peers
 */
public class WiFiDirectServicesList extends Fragment implements WiFiDevicesAdapter.ItemClickListener {
    private static final String TAG = "RecyclerViewFragment";

    private static final int TABCHOOSER = 5; //numero costante scelto a caso

    private RecyclerView mRecyclerView;
    private TextView localDeviceNameText, localDeviceAddressText;
    @Getter private WiFiDevicesAdapter mAdapter;

    interface DeviceClickListener {
        public void connectP2p(WiFiP2pService wifiP2pService, int tabNum);

        public void setWifiP2pDevice(WiFiP2pService service1, int tabNum);
    }

    public static WiFiDirectServicesList newInstance() {
        WiFiDirectServicesList fragment = new WiFiDirectServicesList();
        return fragment;
    }

    public WiFiDirectServicesList() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.devices_list, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);// allows for optimizations if all item views are of the same size:

        mAdapter = new WiFiDevicesAdapter(this.getActivity(),this);
        mRecyclerView.setAdapter(mAdapter);

        localDeviceNameText = (TextView) rootView.findViewById(R.id.localDeviceName);
        localDeviceAddressText = (TextView) rootView.findViewById(R.id.localDeviceAddress);

        localDeviceNameText.setText(LocalP2PDevice.getInstance().getLocalDevice().deviceName);
        localDeviceAddressText.setText(LocalP2PDevice.getInstance().getLocalDevice().deviceAddress);

        return rootView;
    }

    @Override
    public void itemClicked(final View view) {
        Log.d("onArticleSelected", "catturato clic");

        TabChoosedDialog tabChoosedDialog = (TabChoosedDialog) getFragmentManager().findFragmentByTag("tabchooserdialog");

        if (tabChoosedDialog == null) {
            tabChoosedDialog = TabChoosedDialog.newInstance(mRecyclerView.getChildPosition(view));

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


                    WiFiP2pService service = ServiceList.getInstance().getServiceList().get(position);
                    ((DeviceClickListener) getActivity()).setWifiP2pDevice(service, tabnum);
                    ((DeviceClickListener) getActivity()).connectP2p(service, tabnum);

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // After Cancel code.
                    Log.d("Tabchooserdialog", "Non ho premuto i pulsanti");
                }

                break;
            default:
                break;
        }

    }
}


