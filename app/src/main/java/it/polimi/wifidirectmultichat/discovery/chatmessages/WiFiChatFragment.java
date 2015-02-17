
package it.polimi.wifidirectmultichat.discovery.chatmessages;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polimi.wifidirectmultichat.R;
import it.polimi.wifidirectmultichat.discovery.chatmessages.customanimation.GarageDoorItemAnimator;
import it.polimi.wifidirectmultichat.discovery.chatmessages.waitingtosend.WaitingToSendListElement;
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
    private TextView chatLine;
    @Getter WiFiChatMessageListAdapter adapter = null;
    @Getter private List<String> items = new ArrayList<>();
    private ListView listView;

//    private RecyclerView mRecyclerView;

    public static WiFiChatFragment newInstance() {
        Log.d("WifiChatFragment", "NEW _ INSTANCE CALLED!!!!!!");
        WiFiChatFragment fragment = new WiFiChatFragment();
        return fragment;
    }

    public WiFiChatFragment() {
    }

    public interface CallbackActivity {
        public void reconnectToService(WiFiP2pService wifiP2pService);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("WifiChatFragment_oncreateview","tabNumber" + tabNumber);
        view = inflater.inflate(R.layout.chatmessage_list, container, false);

        chatLine = (TextView) view.findViewById(R.id.txtChatLine);
        listView = (ListView) view.findViewById(R.id.list);
        adapter = new WiFiChatMessageListAdapter(getActivity(),R.id.txtChatLine, this);
        listView.setAdapter(adapter);

        view.findViewById(R.id.button1).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (chatManager != null) {
                            if (!chatManager.isDisable()) {
                                Log.d("pippo", "chatmanager state: enable");
                                chatManager.write(chatLine.getText().toString().getBytes());
                            } else {
                                Log.d("pippo", "chatmanager disabiltiato, ma ho tentato di inviare un messaggio con tabNum= " + tabNumber);
                                WaitingToSendQueue.getInstance().waitingToSendItemsList(tabNumber).add(chatLine.getText().toString());

                                List<String> lista = WaitingToSendQueue.getInstance().waitingToSendItemsList(tabNumber);

                                //tento la riconnessione
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

        Log.d("sendForcedWaitingToSendQueue", "Messaggio in coda: " + combineMessages);

        if (chatManager != null) {
            if (!chatManager.isDisable()) {
                chatManager.write((combineMessages).getBytes());
                WaitingToSendQueue.getInstance().waitingToSendItemsList(tabNumber).clear();
            } else {
                Log.d("sendForcedWaitingToSendQueue", "chatmanager disabiltiato, ma ho tentato di inviare un messaggio");
            }

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
//        adapter.notifyItemInserted(items.size() - 1);
        adapter.notifyDataSetChanged();
    }

    public void updateAfterColorChange() {
        Log.d("WifiChatFragment aftercolor","tabNumber" + tabNumber);
        if(adapter!=null) {
            adapter.notifyDataSetChanged();
        }
    }

}
