
package it.polimi.wifidirectmultichat.discovery.services;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Method;

import it.polimi.wifidirectmultichat.discovery.LocalP2PDevice;

import it.polimi.wifidirectmultichat.R;
import it.polimi.wifidirectmultichat.discovery.WiFiServiceDiscoveryActivity;
import it.polimi.wifidirectmultichat.discovery.services.localdevicedialog.LocalDeviceDialogFragment;
import lombok.Getter;

/**
 * A simple ListFragment that shows the available services as published by the
 * peers
 */
public class WiFiP2pServicesListFragment extends Fragment implements
        WiFiServicesAdapter.ItemClickListener, LocalDeviceDialogFragment.DialogCallbackInterface {

    private static final String TAG = "RecyclerViewFragment";

    private RecyclerView mRecyclerView;
    private CardView cardviewLocalDevice;
    private TextView localDeviceNameText, localDeviceAddressText;
    @Getter private WiFiServicesAdapter mAdapter;

    public interface DeviceClickListener {
        public void connectP2p(WiFiP2pService wifiP2pService, int tabNum);

        public void setWifiP2pDevice(WiFiP2pService service1);

        public void tryToConnectToAService(int position);
    }

    public static WiFiP2pServicesListFragment newInstance() {
        WiFiP2pServicesListFragment fragment = new WiFiP2pServicesListFragment();
        return fragment;
    }

    public WiFiP2pServicesListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.services_list, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);// allows for optimizations if all item views are of the same size:

        mAdapter = new WiFiServicesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        localDeviceNameText = (TextView) rootView.findViewById(R.id.localDeviceName);
        localDeviceAddressText = (TextView) rootView.findViewById(R.id.localDeviceAddress);

        localDeviceNameText.setText(LocalP2PDevice.getInstance().getLocalDevice().deviceName);
        localDeviceAddressText.setText(LocalP2PDevice.getInstance().getLocalDevice().deviceAddress);

        cardviewLocalDevice = (CardView) rootView.findViewById(R.id.cardviewLocalDevice);
        cardviewLocalDevice.setOnClickListener(new OnClickListenerLocalDevice(this));

        return rootView;
    }

    @Override
    public void changeLocalDeviceName(String deviceName) {
        localDeviceNameText.setText(deviceName);
        ((WiFiServiceDiscoveryActivity)getActivity()).setDeviceNameWithReflection(deviceName);

    }

    /**
     * chiamato quando clicco su un elemento odella recyclerview.
     * @param view
     */
    @Override
    public void itemClicked(final View view) {
        Log.d("onArticleSelected", "catturato clic");

        ((DeviceClickListener) getActivity()).tryToConnectToAService(mRecyclerView.getChildPosition(view));
//        WiFiP2pService service = ServiceList.getInstance().getServiceList().get(mRecyclerView.getChildPosition(view));
//        ((DeviceClickListener) getActivity()).setWifiP2pDevice(service);
//        ((DeviceClickListener) getActivity()).connectP2p(service, 1);
    }


    /**
     * Listener per la cardview con il LocalDevice
     */
    class OnClickListenerLocalDevice implements View.OnClickListener {

        public Fragment fragment;

        public OnClickListenerLocalDevice(Fragment fragment1) {
            fragment = fragment1;
        }

        @Override
        public void onClick(View v) {
            LocalDeviceDialogFragment localDeviceDialogFragment = (LocalDeviceDialogFragment) getFragmentManager().findFragmentByTag("localDeviceDialogFragment");

            if (localDeviceDialogFragment == null) {
                localDeviceDialogFragment = LocalDeviceDialogFragment.newInstance();
                localDeviceDialogFragment.setTargetFragment(fragment, 0);

                localDeviceDialogFragment.show(getFragmentManager(), "localDeviceDialogFragment");
                getFragmentManager().executePendingTransactions();
            }
        }
    }
}


