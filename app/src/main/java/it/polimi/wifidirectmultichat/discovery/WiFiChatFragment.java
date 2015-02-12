
package it.polimi.wifidirectmultichat.discovery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polimi.wifidirectmultichat.R;
import lombok.Getter;
import lombok.Setter;

/**
 * This fragment handles chat related UI which includes a list view for messages
 * and a message entry field with send button.
 */
@SuppressLint("ValidFragment")
public class WiFiChatFragment extends Fragment {

    private static int tabNumber;
    @Getter @Setter private static boolean firstStartSendAddress;
    @Getter @Setter private boolean grayScale = true;
    private View view;
    @Getter private ChatManager chatManager;
    private TextView chatLine;
    private ListView listView;
    ChatMessageAdapter adapter = null;
    private List<String> items = new ArrayList<>();

    public static WiFiChatFragment newInstance(int tabNumber1) {
        Log.d("WifiChatFragment", "NEW _ INSTANCE CALLED!!!!!!");
        WifiP2pDevice device = DeviceTabList.getInstance().getDeviceList().get(tabNumber1);
        if(device!=null) {
            Log.d("WifiChatFragment", "device: " + device.deviceAddress + ", " + device.deviceName);
        }
        WiFiChatFragment fragment = new WiFiChatFragment();
        tabNumber = tabNumber1;
        return fragment;
    }

    private WiFiChatFragment() {
    }

    interface ReconnectInterface {
        public void reconnectToService(WiFiP2pService wifiP2pService);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        chatLine = (TextView) view.findViewById(R.id.txtChatLine);
        listView = (ListView) view.findViewById(android.R.id.list);
        adapter = new ChatMessageAdapter(getActivity(), android.R.id.text1,
                items);
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
                                Log.d("pippo", "chatmanager disabiltiato, ma ho tentato di inviare un messaggio");
                                WaitingToSendQueue.getInstance().waitingToSendItemsList(tabNumber).add(chatLine.getText().toString());

                                Log.d("pippo", "tento la riconnessione");
                                //tento la riconnessione
                                List<WiFiP2pService> list = ServiceList.getInstance().getServiceList();
                                WifiP2pDevice device = DeviceTabList.getInstance().getDeviceList().get(tabNumber);
                                if(device!=null) {
                                    WiFiP2pService service = ServiceList.getInstance().getServiceByDevice(device);
                                    Log.d("pippo", "device: " + device.deviceName + ", address: " + device.deviceAddress + ", service: " + service);
                                    ((ReconnectInterface) getActivity()).reconnectToService(service);
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
        adapter.add(readMessage);
        adapter.notifyDataSetChanged();
    }

    public void updateAfterColorChange() {
        if(adapter!=null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * ArrayAdapter to manage chat messages.
     */
    public class ChatMessageAdapter extends ArrayAdapter<String> {

        List<String> messages = null;

        public ChatMessageAdapter(Context context, int textViewResourceId,
                                  List<String> items) {
            super(context, textViewResourceId, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(android.R.layout.simple_list_item_1, null);
            }
            String message = items.get(position);
            if (message != null && !message.isEmpty()) {
                TextView nameText = (TextView) v
                        .findViewById(android.R.id.text1);
                if (nameText != null) {
                    nameText.setText(message);
                    nameText.setTextAppearance(getActivity(),R.style.normalText);
                    if(grayScale) {
                        nameText.setTextColor(getResources().getColor(R.color.gray));
                    } else {
                        if (message.startsWith("Me: ")) {
                            nameText.setTextAppearance(getActivity(),
                                    R.style.normalText);
                        } else {
                            nameText.setTextAppearance(getActivity(),
                                    R.style.boldText);
                        }
                    }
                }
            }
            return v;
        }
    }
}
