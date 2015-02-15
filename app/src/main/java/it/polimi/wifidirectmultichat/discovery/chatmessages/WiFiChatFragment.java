
package it.polimi.wifidirectmultichat.discovery.chatmessages;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polimi.wifidirectmultichat.R;
import it.polimi.wifidirectmultichat.discovery.socketmanagers.ChatManager;
import it.polimi.wifidirectmultichat.discovery.DeviceTabList;
import it.polimi.wifidirectmultichat.discovery.services.ServiceList;
import it.polimi.wifidirectmultichat.discovery.chatmessages.waitingtosend.WaitingToSendQueue;
import it.polimi.wifidirectmultichat.discovery.services.WiFiP2pService;
import lombok.Getter;
import lombok.Setter;

/**
 * This fragment handles chat related UI which includes a list view for messages
 * and a message entry field with send button.
 */
public class WiFiChatFragment extends Fragment {

    @Getter @Setter private Integer tabNumber;
    @Getter @Setter private static boolean firstStartSendAddress;
    @Getter @Setter private boolean grayScale = true;
    private View view;
    @Getter private ChatManager chatManager;
    private static WiFiChatFragment chatFrag;
    private TextView chatLine;
    @Getter WiFiChatMessageAdapter adapter = null;
    @Getter private List<String> items = new ArrayList<>();

    private RecyclerView mRecyclerView;

    public static WiFiChatFragment newInstance() {
        Log.d("WifiChatFragment", "NEW _ INSTANCE CALLED!!!!!!");
//        WifiP2pDevice device = DeviceTabList.getInstance().getDeviceList().get(tabNumber1);
//        if(device!=null) {
//            Log.d("WifiChatFragment", "device: " + device.deviceAddress + ", " + device.deviceName);
//        }
        WiFiChatFragment fragment = new WiFiChatFragment();
        chatFrag = fragment;
        return fragment;
    }

    public WiFiChatFragment() {
    }

    public interface CallbackActivity {
        public void reconnectToService(WiFiP2pService wifiP2pService);
        public int getFragmentPositionInTabList(WiFiChatFragment fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("WifiChatFragment_oncreateview","tabNumber" + tabNumber);
        view = inflater.inflate(R.layout.chatmessage_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewChat);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);// allows for optimizations if all item views are of the same size:

        adapter = new WiFiChatMessageAdapter(this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        chatLine = (TextView) view.findViewById(R.id.txtChatLine);

        view.findViewById(R.id.txtChatLine).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        Log.d("ontouch","down");
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        Log.d("ontouch","up");
//                        break;
//                    case MotionEvent.ACTION_HOVER_ENTER :
//                        Log.d("ontouch","hoverenter");
//                        break;
//                    case MotionEvent.ACTION_HOVER_MOVE :
//                        Log.d("ontouch","hovermove");
//                        break;
//                    case MotionEvent.ACTION_HOVER_EXIT :
//                        Log.d("ontouch","hoverexit");
//                        break;
//                    default:
//                        Log.d("ontouch","default");
//                        break;
//                }

                return false;
            }
        });

//        view.findViewById(R.id.txtChatLine).setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                Log.d("onfocuschange" , "focus: " + hasFocus);
//                if(hasFocus) {
//                    mRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
//                } else {
//                    mRecyclerView.scrollToPosition(0);
//                }
//            }
//        });

        view.findViewById(R.id.button1).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (chatManager != null) {
                            if (!chatManager.isDisable()) {
                                Log.d("pippo", "chatmanager state: enable");
                                chatManager.write(chatLine.getText().toString().getBytes());
                            } else {
                                Log.d("pippo", "chatmanager disabiltiato, ma ho tentato di inviare un messaggio");
                                WaitingToSendQueue.getInstance().waitingToSendItemsList(tabNumber).add(chatLine.getText().toString());

                                Log.d("pippo", "tento la riconnessione. Il valore di tabNum e': " + ((CallbackActivity) getActivity()).getFragmentPositionInTabList(chatFrag));
                                //tento la riconnessione
                                List<WiFiP2pService> list = ServiceList.getInstance().getServiceList();
                                WifiP2pDevice device = DeviceTabList.getInstance().getDevice(tabNumber - 1);
                                if(device!=null) {
                                    WiFiP2pService service = ServiceList.getInstance().getServiceByDevice(device);
                                    Log.d("pippo", "device: " + device.deviceName + ", address: " + device.deviceAddress + ", service: " + service);
                                    ((CallbackActivity) getActivity()).reconnectToService(service);
                                } else {
                                    Log.d("pippo","device = null, non posso fare nulla");
                                }
                            }
                            pushMessage("Me: " + chatLine.getText().toString());
                            chatLine.setText("");
                            chatLine.clearFocus();
                        }
                    }
                });

        return view;
    }


    public void sendForcedWaitingToSendQueue() {
        String combineMessages = new String();
        List<String> listCopy = WaitingToSendQueue.getInstance().waitingToSendItemsList(tabNumber);
        for (String message : listCopy) {
            if(!message.equals("") && !message.equals("\n")  ) {
                combineMessages = combineMessages + "\n" + message;
            }
        }
        combineMessages = combineMessages + "\n";

        if (chatManager != null) {
            if (!chatManager.isDisable()) {
                chatManager.write((combineMessages).getBytes());
                WaitingToSendQueue.getInstance().waitingToSendItemsList(tabNumber).clear();
//                    WaitingToSendQueue.getInstance().waitingToSendItemsList(tabNumber).remove(message);
            } else {
                Log.d("sendForcedWaitingToSendQueue", "chatmanager disabiltiato, ma ho tentato di inviare un messaggio");
            }
//                pushMessage("Me: " + chatLine.getText().toString());
//                chatLine.setText("");
//                chatLine.clearFocus();

        }
    }


    public interface MessageTarget {
        public Handler getHandler();
    }

    public void setChatManager(ChatManager obj) {
        chatManager = obj;
    }

    public void pushMessage(String readMessage) {
        Log.d("WifiChatFragment push","tabNumber" + tabNumber);
        items.add(readMessage);
        adapter.notifyDataSetChanged();
    }

    public void updateAfterColorChange() {
        Log.d("WifiChatFragment aftercolor","tabNumber" + tabNumber);
        if(adapter!=null) {
            adapter.notifyDataSetChanged();
        }
    }

}
