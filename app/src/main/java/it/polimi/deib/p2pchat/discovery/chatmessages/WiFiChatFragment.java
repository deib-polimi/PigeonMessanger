
package it.polimi.deib.p2pchat.discovery.chatmessages;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polimi.deib.p2pchat.R;
import it.polimi.deib.p2pchat.discovery.DestinationDeviceTabList;
import it.polimi.deib.p2pchat.discovery.socketmanagers.ChatManager;
import it.polimi.deib.p2pchat.discovery.services.ServiceList;
import it.polimi.deib.p2pchat.discovery.chatmessages.waitingtosend.WaitingToSendQueue;
import it.polimi.deib.p2pchat.discovery.services.WiFiP2pService;
import lombok.Getter;
import lombok.Setter;

/**
 * Class fragment that handles chat related UI which includes a list view for messages
 * and a message entry field with send button.
 * <p></p>
 * Created by Stefano Cappa on 04/02/15, based on google code samples.
 */
public class WiFiChatFragment extends Fragment {

    private static final String TAG = "WiFiChatFragment";

    @Getter @Setter private Integer tabNumber;
    @Getter @Setter private static boolean firstStartSendAddress;
    @Getter @Setter private boolean grayScale = true;
    @Getter private final List<String> items = new ArrayList<>();

    private TextView chatLine;

    @Getter @Setter private ChatManager chatManager;
    private WiFiChatMessageListAdapter adapter = null;

    /**
     * Callback interface to call methods reconnectToService in {@link it.polimi.deib.p2pchat.discovery.MainActivity}.
     * MainActivity implements this interface.
     */
    public interface AutomaticReconnectionListener {
        public void reconnectToService(WiFiP2pService wifiP2pService);
    }

    /**
     * Method to obtain a new Fragment's instance.
     * @return This Fragment instance.
     */
    public static WiFiChatFragment newInstance() {
        return new WiFiChatFragment();
    }

    /**
     * Default Fragment constructor.
     */
    public WiFiChatFragment() {}


    /**
     * Method that combines all the messages inside the
     * {@link it.polimi.deib.p2pchat.discovery.chatmessages.waitingtosend.WaitingToSendQueue}
     * in one String and pass this one to the {@link it.polimi.deib.p2pchat.discovery.socketmanagers.ChatManager}
     * to send the message to other devices.
     */
    public void sendForcedWaitingToSendQueue() {

        Log.d(TAG, "sendForcedWaitingToSendQueue() called");

        String combineMessages = "";
        List<String> listCopy = WaitingToSendQueue.getInstance().getWaitingToSendItemsList(tabNumber);
        for (String message : listCopy) {
            if(!message.equals("") && !message.equals("\n")  ) {
                combineMessages = combineMessages + "\n" + message;
            }
        }
        combineMessages = combineMessages + "\n";

        Log.d(TAG, "Queued message to send: " + combineMessages);

        if (chatManager != null) {
            if (!chatManager.isDisable()) {
                chatManager.write((combineMessages).getBytes());
                WaitingToSendQueue.getInstance().getWaitingToSendItemsList(tabNumber).clear();
            } else {
                Log.d(TAG, "Chatmanager disabled, impossible to send the queued combined message");
            }

        }
    }


    /**
     * Method to add a message to the Fragment's listView and notifies this update to
     * {@link it.polimi.deib.p2pchat.discovery.chatmessages.WiFiChatMessageListAdapter}.
     * @param readMessage String that represents the message to add.
     */
    public void pushMessage(String readMessage) {
        items.add(readMessage);
        adapter.notifyDataSetChanged();
    }

    /**
     * Method that updates the {@link it.polimi.deib.p2pchat.discovery.chatmessages.WiFiChatMessageListAdapter}.
     */
    public void updateChatMessageListAdapter() {
        if(adapter!=null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Method that add the text in the chatLine EditText to the WaitingToSendQueue and try to reconnect
     * to the service associated to the device of this tab, with index tabNumber.
     */
    private void addToWaitingToSendQueueAndTryReconnect() {
        //add message to the waiting to send queue
        WaitingToSendQueue.getInstance().getWaitingToSendItemsList(tabNumber).add(chatLine.getText().toString());

        //try to reconnect
        WifiP2pDevice device = DestinationDeviceTabList.getInstance().getDevice(tabNumber - 1);
        if(device!=null) {
            WiFiP2pService service = ServiceList.getInstance().getServiceByDevice(device);
            Log.d(TAG, "device address: " + device.deviceAddress + ", service: " + service);

            //call reconnectToService in MainActivity
            ((AutomaticReconnectionListener) getActivity()).reconnectToService(service);

        } else {
            Log.d(TAG,"addToWaitingToSendQueueAndTryReconnect device == null, i can't do anything");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatmessage_list, container, false);

        chatLine = (TextView) view.findViewById(R.id.txtChatLine);
        ListView listView = (ListView) view.findViewById(R.id.list);

        adapter = new WiFiChatMessageListAdapter(getActivity(),R.id.txtChatLine, this);
        listView.setAdapter(adapter);

        view.findViewById(R.id.sendMessage).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (chatManager != null) {
                            if (!chatManager.isDisable()) {
                                Log.d(TAG, "chatmanager state: enable");

                                //send message to the ChatManager's outputStream.
                                chatManager.write(chatLine.getText().toString().getBytes());
                            } else {
                                Log.d(TAG, "chatmanager disabled, trying to send a message with tabNum= " + tabNumber);

                                addToWaitingToSendQueueAndTryReconnect();
                            }

                            pushMessage("Me: " + chatLine.getText().toString());
                            chatLine.setText("");
                        } else {
                            Log.d(TAG, "chatmanager is null");
                        }
                    }
                });

        return view;
    }


}
