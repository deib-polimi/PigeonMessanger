
package it.polimi.deib.p2pchat.discovery.services;
/*
 * Copyright (C) 2015-2016 Stefano Cappa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.polimi.deib.p2pchat.R;
import it.polimi.deib.p2pchat.discovery.model.LocalP2PDevice;

import it.polimi.deib.p2pchat.discovery.MainActivity;
import it.polimi.deib.p2pchat.discovery.services.localdeviceguielement.LocalDeviceDialogFragment;
import lombok.Getter;

/**
 * Fragment that contains the recyclerview with the WiFiP2pService list and the Cardview with LocalDevice informations.
 * If you click on a item device, the connection to this device starts, using ItemClickListener.
 * If you click on the local device cardview, a {@link it.polimi.deib.p2pchat.discovery.services.localdeviceguielement.LocalDeviceDialogFragment}
 * appears. Here you can change the local device name, and
 * with the DialogCallbackInterface obtain the data inserted in the dialogfragment.
 * <p></p>
 * Created by Stefano Cappa on 04/02/15.
 */
public class WiFiP2pServicesFragment extends Fragment implements
        //ItemClickListener is the interface in the adapter to intercept item's click events.
        //I use this to call itemClicked(v) in this class from WiFiServicesAdapter.
        WiFiServicesAdapter.ItemClickListener,
        //DialogConfirmListener is the interface in LocalDeviceDialogFragment. I use this to call
        //public void changeLocalDeviceName(String deviceName) in this class from the DialogFragment without to pass attributes or other stuff
        LocalDeviceDialogFragment.DialogConfirmListener {

    private static final String TAG = "WiFiP2pServicesFragment";

    private RecyclerView mRecyclerView;
    @Getter private WiFiServicesAdapter mAdapter;

    private TextView localDeviceNameText;

    /**
     * Callback interface to call methods tryToConnectToAService in {@link it.polimi.deib.p2pchat.discovery.MainActivity}.
     * MainActivity implements this interface.
     */
    public interface DeviceClickListener {
        public void tryToConnectToAService(int position);
    }

    /**
     * Method to obtain a new Fragment's instance.
     * @return This Fragment instance.
     */
    public static WiFiP2pServicesFragment newInstance() {
        return new WiFiP2pServicesFragment();
    }

    /**
     * Default Fragment constructor.
     */
    public WiFiP2pServicesFragment() {}


    /**
     * Method to change the local device name and update the GUI element.
     * @param deviceName String that represents the device name.
     */
    @Override
    public void changeLocalDeviceName(String deviceName) {
        if(deviceName==null) {
            return;
        }

        localDeviceNameText.setText(deviceName);
        ((MainActivity)getActivity()).setDeviceNameWithReflection(deviceName);
    }

    /**
     * Method called by {@link it.polimi.deib.p2pchat.discovery.services.WiFiServicesAdapter}
     * with the {@link it.polimi.deib.p2pchat.discovery.services.WiFiServicesAdapter.ItemClickListener}
     * interface, when the user click on an element of the {@link android.support.v7.widget.RecyclerView}.
     * @param view The clicked view.
     */
    @Override
    public void itemClicked(View view) {
        int clickedPosition = mRecyclerView.getChildPosition(view);

        if(clickedPosition>=0) { //a little check :)
            ((DeviceClickListener) getActivity()).tryToConnectToAService(clickedPosition);
        }
    }

    /**
     * Method to show a GO Icon inside the local device card view.
     * This is useful to identify which device is a GO.
     */
    public void showLocalDeviceGoIcon(){
        if(getView() !=null && getView().findViewById(R.id.go_logo)!=null && getView().findViewById(R.id.i_am_a_go_textview)!=null) {
            ImageView goLogoImageView = (ImageView) getView().findViewById(R.id.go_logo);
            TextView i_am_a_go_textView = (TextView) getView().findViewById(R.id.i_am_a_go_textview);

            goLogoImageView.setImageDrawable(getResources().getDrawable(R.drawable.go_logo));
            goLogoImageView.setVisibility(View.VISIBLE);
            i_am_a_go_textView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Method that remove the ip address in the local device card view to replace
     * with a warning that the ip is not available.
     */
    public void resetLocalDeviceIpAddress() {
        if(getView()!=null && getView().findViewById(R.id.localDeviceIpAddress)!=null) {
            TextView ipAddress = (TextView) getView().findViewById(R.id.localDeviceIpAddress);
            ipAddress.setText(getResources().getString(R.string.ip_not_available));
        }
    }

    /**
     * Method that set the ip address in the local device card view.
     *
     * @param ipAddress String that represent the IP address to set.
     */
    public void setLocalDeviceIpAddress(String ipAddress) {
        if(getView()!=null && getView().findViewById(R.id.localDeviceIpAddress)!=null) {
            TextView ipAddressTextView = (TextView) getView().findViewById(R.id.localDeviceIpAddress);
            ipAddressTextView.setText(ipAddress);
        }
    }

    /**
     * Method to hide a GO Icon inside the local device card view.
     * This is useful to remove the icon after a event, like "disconnect".
     */
    public void hideLocalDeviceGoIcon() {
        if(getView()!=null && getView().findViewById(R.id.go_logo)!=null && getView().findViewById(R.id.i_am_a_go_textview)!=null) {
            ImageView goLogoImageView = (ImageView) getView().findViewById(R.id.go_logo);
            TextView i_am_a_go_textView = (TextView) getView().findViewById(R.id.i_am_a_go_textview);

            goLogoImageView.setVisibility(View.INVISIBLE);
            i_am_a_go_textView.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.services_list, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // allows for optimizations if all item views are of the same size:
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new WiFiServicesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        localDeviceNameText = (TextView) rootView.findViewById(R.id.localDeviceName);
        localDeviceNameText.setText(LocalP2PDevice.getInstance().getLocalDevice().deviceName);

        TextView localDeviceAddressText = (TextView) rootView.findViewById(R.id.localDeviceAddress);
        localDeviceAddressText.setText(LocalP2PDevice.getInstance().getLocalDevice().deviceAddress);

        CardView cardViewLocalDevice = (CardView) rootView.findViewById(R.id.cardviewLocalDevice);
        cardViewLocalDevice.setOnClickListener(new OnClickListenerLocalDevice(this));

        return rootView;
    }


    /**
     * Inner class that implements the Onclicklistener for the local device cardview.
     * It's useful to open the {@link it.polimi.deib.p2pchat.discovery.services.localdeviceguielement.LocalDeviceDialogFragment}
     * after a click's event.
     */
    class OnClickListenerLocalDevice implements View.OnClickListener {

        private final Fragment fragment;

        public OnClickListenerLocalDevice(Fragment fragment1) {
            fragment = fragment1;
        }

        @Override
        public void onClick(View v) {
            LocalDeviceDialogFragment localDeviceDialogFragment = (LocalDeviceDialogFragment) getFragmentManager()
                    .findFragmentByTag("localDeviceDialogFragment");

            if (localDeviceDialogFragment == null) {
                localDeviceDialogFragment = LocalDeviceDialogFragment.newInstance();
                localDeviceDialogFragment.setTargetFragment(fragment, 0);

                localDeviceDialogFragment.show(getFragmentManager(), "localDeviceDialogFragment");
                getFragmentManager().executePendingTransactions();
            }
        }
    }
}


