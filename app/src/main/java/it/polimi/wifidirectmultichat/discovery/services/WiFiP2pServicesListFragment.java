
package it.polimi.wifidirectmultichat.discovery.services;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.polimi.wifidirectmultichat.discovery.LocalP2PDevice;
import it.polimi.wifidirectmultichat.discovery.dialog.TabChoosedDialog;

import it.polimi.wifidirectmultichat.R;
import lombok.Getter;

/**
 * A simple ListFragment that shows the available services as published by the
 * peers
 */
public class WiFiP2pServicesListFragment extends Fragment implements
        WiFiServicesAdapter.ItemClickListener {

    private static final String TAG = "RecyclerViewFragment";

    private static final int TABCHOOSER = 5; //numero costante scelto a caso, NON LEGATO AL NUMERO DI DEVICE O DI CHAT. NON MODIFICARLO!!!

    private RecyclerView mRecyclerView;
    private TextView localDeviceNameText, localDeviceAddressText;
    @Getter private WiFiServicesAdapter mAdapter;

    public interface DeviceClickListener {
        public void connectP2p(WiFiP2pService wifiP2pService, int tabNum);

        public void setWifiP2pDevice(WiFiP2pService service1);
    }

    public static WiFiP2pServicesListFragment newInstance() {
        WiFiP2pServicesListFragment fragment = new WiFiP2pServicesListFragment();
        return fragment;
    }

    public WiFiP2pServicesListFragment() {}

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

        mAdapter = new WiFiServicesAdapter(this);
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

//        TabChoosedDialog tabChoosedDialog = (TabChoosedDialog) getFragmentManager().findFragmentByTag("tabchooserdialog");
//
//        if (tabChoosedDialog == null) {
//            tabChoosedDialog = TabChoosedDialog.newInstance(mRecyclerView.getChildPosition(view));

//            tabChoosedDialog.setTargetFragment(this, TABCHOOSER);

//            tabChoosedDialog.show(getFragmentManager(), "tabchooserdialog");
//            getFragmentManager().executePendingTransactions();
//        }
        // After Ok code.
//        Log.d("Tabchooseddialog", "tabnum: " + tabnum + ", position: " + position);

        WiFiP2pService service = ServiceList.getInstance().getServiceList().get(mRecyclerView.getChildPosition(view));
        ((DeviceClickListener) getActivity()).setWifiP2pDevice(service);
        ((DeviceClickListener) getActivity()).connectP2p(service, 1);
    }

//    @Override
//    public void startConnection(int tabnum, int position) {
//        // After Ok code.
//        Log.d("Tabchooseddialog", "tabnum: " + tabnum + ", position: " + position);
//
//        WiFiP2pService service = ServiceList.getInstance().getServiceList().get(position);
//        ((DeviceClickListener) getActivity()).setWifiP2pDevice(service);
//        ((DeviceClickListener) getActivity()).connectP2p(service, tabnum);
//    }
}


