
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
    @Getter private WiFiDevicesAdapter mAdapter;

    interface DeviceClickListener {
        public void connectP2p(WiFiP2pService wifiP2pService, int tabNum);

        public void setWifiP2pDevice(WiFiP2pService service1, int tabNum);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.devices_list, container, false);
        rootView.setTag(TAG);

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);// allows for optimizations if all item views are of the same size:

        mAdapter = new WiFiDevicesAdapter(this.getActivity(),this);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.setItemAnimator(new GarageDoorItemAnimator());

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        listAdapter = new WiFiDevicesAdapter(this.getActivity(),
//                android.R.layout.simple_list_item_2, android.R.id.text1,
//                ServiceList.getInstance().getServiceList());
//        setListAdapter(listAdapter);


//        ((WiFiServiceDiscoveryActivity) getActivity()).setupToolBar();

//        statusTxtView = (TextView) this.getActivity().findViewById(R.id.status_text);
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
//
//        Download download = DownloadList.getInstance().getDownloadList().get(mRecyclerView.getChildPosition(view));
//        ArrayList<String> listDetail = new ArrayList<>();
//        listDetail.add(download.getFileWeb().getUri().toString());
//        listDetail.add(download.getDownloadPath());
//        listDetail.add(download.getFileWeb().getChosenfileName());
//        listDetail.add(download.getFileWeb().getConnectionMode());
//        listDetail.add(download.getFileWeb().getHash());
//        listDetail.add(download.getFileWeb().getHashAlgorithm());
//
//        Intent detailIntent = new Intent(this.getActivity(), ItemDetailActivity.class);
//        detailIntent.putExtra(ItemDetailFragment.ARG_POSITION, mRecyclerView.getChildPosition(view));
//        detailIntent.putStringArrayListExtra("details_list",listDetail);
//        this.getActivity().startActivity(detailIntent);

    }

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//
//        TabChoosedDialog tabChoosedDialog = (TabChoosedDialog) getFragmentManager().findFragmentByTag("tabchooserdialog");
//
//        if (tabChoosedDialog == null) {
//            tabChoosedDialog = TabChoosedDialog.newInstance(position);
//
//            tabChoosedDialog.setTargetFragment(this, TABCHOOSER);
//
//            tabChoosedDialog.show(getFragmentManager(), "tabchooserdialog");
//            getFragmentManager().executePendingTransactions();
//        }
//
//
//    }

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
//                    ((TextView) this.getView().findViewById(android.R.id.text2)).setText("Connecting");


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


