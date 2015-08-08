
package it.polimi.deib.p2pchat.discovery;

/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import it.polimi.deib.p2pchat.R;
import it.polimi.deib.p2pchat.discovery.actionlisteners.CustomDnsSdTxtRecordListener;
import it.polimi.deib.p2pchat.discovery.actionlisteners.CustomDnsServiceResponseListener;
import it.polimi.deib.p2pchat.discovery.actionlisteners.CustomizableActionListener;
import it.polimi.deib.p2pchat.discovery.chatmessages.WiFiChatFragment;
import it.polimi.deib.p2pchat.discovery.chatmessages.messagefilter.MessageException;
import it.polimi.deib.p2pchat.discovery.chatmessages.messagefilter.MessageFilter;
import it.polimi.deib.p2pchat.discovery.chatmessages.waitingtosend.WaitingToSendQueue;
import it.polimi.deib.p2pchat.discovery.model.LocalP2PDevice;
import it.polimi.deib.p2pchat.discovery.model.P2pDestinationDevice;
import it.polimi.deib.p2pchat.discovery.services.ServiceList;
import it.polimi.deib.p2pchat.discovery.services.WiFiP2pService;
import it.polimi.deib.p2pchat.discovery.services.WiFiP2pServicesFragment;
import it.polimi.deib.p2pchat.discovery.services.WiFiServicesAdapter;
import it.polimi.deib.p2pchat.discovery.socketmanagers.ChatManager;
import it.polimi.deib.p2pchat.discovery.socketmanagers.ClientSocketHandler;
import it.polimi.deib.p2pchat.discovery.socketmanagers.GroupOwnerSocketHandler;
import lombok.Getter;
import lombok.Setter;

/**
 * Main Activity of Pidgeot / WiFiDirect MultiChat
 * <p></p>
 * Created by Stefano Cappa on 04/02/15.
 */
public class MainActivity extends ActionBarActivity implements
        WiFiP2pServicesFragment.DeviceClickListener,
        WiFiChatFragment.AutomaticReconnectionListener,
        Handler.Callback,
        ConnectionInfoListener {

    private static final String TAG = "MainActivity";
    private boolean retryChannel = false;
    @Setter
    private boolean connected = false;
    @Getter
    private int tabNum = 1;
    @Getter
    @Setter
    private boolean blockForcedDiscoveryInBroadcastReceiver = false;
    private boolean discoveryStatus = true;

    @Getter
    private TabFragment tabFragment;
    @Getter
    @Setter
    private Toolbar toolbar;

    private WifiP2pManager manager;
    private WifiP2pDnsSdServiceRequest serviceRequest;
    private Channel channel;

    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver receiver = null;

    private Thread socketHandler;
    private final Handler handler = new Handler(this);

    private ChatManager chatManager;

    /**
     * Method to get the {@link android.os.Handler}.
     *
     * @return The handler.
     */
    Handler getHandler() {
        return handler;
    }


    /**
     * Method called by WiFiChatFragment using the
     * {@link it.polimi.deib.p2pchat.discovery.chatmessages.WiFiChatFragment.AutomaticReconnectionListener}
     * interface, implemented here, by this class.
     * If the wifiP2pService is null, this method return directly, without doing anything.
     *
     * @param service A {@link it.polimi.deib.p2pchat.discovery.services.WiFiP2pService}
     *                object that represents the device in which you want to connect.
     */
    @Override
    public void reconnectToService(WiFiP2pService service) {
        if (service != null) {
            Log.d(TAG, "reconnectToService called");

            //Finally, add device to the DeviceTabList, only if required.
            //Go to addDeviceIfRequired()'s javadoc for more informations.
            DestinationDeviceTabList.getInstance().addDeviceIfRequired(new P2pDestinationDevice(service.getDevice()));

            this.connectP2p(service);
        }
    }


    /**
     * Method to cancel a pending connection, used by the MenuItem icon.
     */
    private void forcedCancelConnect() {
        manager.cancelConnect(channel, new ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "forcedCancelConnect success");
                Toast.makeText(MainActivity.this, "Cancel connect success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "forcedCancelConnect failed, reason: " + reason);
                Toast.makeText(MainActivity.this, "Cancel connect failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Method that force to stop the discovery phase of the wifi direct protocol, clear
     * the {@link it.polimi.deib.p2pchat.discovery.services.ServiceList}, update the
     * discovery's menu item and remove all the registered Services.
     */
    public void forceDiscoveryStop() {
        if (discoveryStatus) {
            discoveryStatus = false;

            ServiceList.getInstance().clear();
            toolbar.getMenu().findItem(R.id.discovery).setIcon(getResources().getDrawable(R.drawable.ic_action_search_stopped));

            this.internalStopDiscovery();
        }
    }

    /**
     * Method that asks to the manager to stop discovery phase.
     * <p></p>
     * Attention, Never call this method directly, but you should use for example {@link #forceDiscoveryStop()}
     */
    private void internalStopDiscovery() {
        manager.stopPeerDiscovery(channel,
                new CustomizableActionListener(
                        MainActivity.this,
                        "internalStopDiscovery",
                        "Discovery stopped",
                        "Discovery stopped",
                        "Discovery stop failed",
                        "Discovery stop failed"));
        manager.clearServiceRequests(channel,
                new CustomizableActionListener(
                        MainActivity.this,
                        "internalStopDiscovery",
                        "ClearServiceRequests success",
                        null,
                        "Discovery stop failed",
                        null));
        manager.clearLocalServices(channel,
                new CustomizableActionListener(
                        MainActivity.this,
                        "internalStopDiscovery",
                        "ClearLocalServices success",
                        null,
                        "clearLocalServices failure",
                        null));
    }

    /**
     * Method to restarts the discovery phase and to update the UI.
     */
    public void restartDiscovery() {
        discoveryStatus = true;

        //starts a new registration, restarts discovery and updates the gui
        this.startRegistration();
        this.discoverService();
        this.updateServiceAdapter();
    }

    /**
     * Method to discover services and put the results
     * in {@link it.polimi.deib.p2pchat.discovery.services.ServiceList}.
     * This method updates also the discovery menu item.
     */
    private void discoverService() {

        ServiceList.getInstance().clear();

        toolbar.getMenu().findItem(R.id.discovery).setIcon(getResources().getDrawable(R.drawable.ic_action_search_searching));

        /*
         * Register listeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */
        manager.setDnsSdResponseListeners(channel,
                new CustomDnsServiceResponseListener(), new CustomDnsSdTxtRecordListener());

        // After attaching listeners, create a service request and initiate
        // discovery.
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();

        //inititiates discovery
        manager.addServiceRequest(channel, serviceRequest,
                new CustomizableActionListener(
                        MainActivity.this,
                        "discoverService",
                        "Added service discovery request",
                        null,
                        "Failed adding service discovery request",
                        "Failed adding service discovery request"));

        //starts services disovery
        manager.discoverServices(channel, new ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Service discovery initiated");
                Toast.makeText(MainActivity.this, "Service discovery initiated", Toast.LENGTH_SHORT).show();
                blockForcedDiscoveryInBroadcastReceiver = false;
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Service discovery failed");
                Toast.makeText(MainActivity.this, "Service discovery failed, " + reason, Toast.LENGTH_SHORT).show();

            }
        });
    }


    /**
     * Method to notifyDataSetChanged to the adapter of the
     * {@link it.polimi.deib.p2pchat.discovery.services.WiFiP2pServicesFragment}.
     */
    private void updateServiceAdapter() {
        WiFiP2pServicesFragment fragment = TabFragment.getWiFiP2pServicesFragment();
        if (fragment != null) {
            WiFiServicesAdapter adapter = fragment.getMAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Method to disconnect this device when this Activity calls onStop().
     */
    private void disconnectBecauseOnStop() {

        this.closeAndKillSocketHandler();

        this.setDisableAllChatManagers();

        this.addColorActiveTabs(true);

        if (manager != null && channel != null) {

            manager.removeGroup(channel,
                    new CustomizableActionListener(
                            MainActivity.this,
                            "disconnectBecauseOnStop",
                            "Disconnected",
                            "Disconnected",
                            "Disconnect failed",
                            "Disconnect failed"));
        } else {
            Log.d("disconnectBecauseOnStop", "Impossible to disconnect");
        }
    }

    /**
     * Method to close and kill socketHandler, GO or Client.
     */
    private void closeAndKillSocketHandler() {
        if (socketHandler instanceof GroupOwnerSocketHandler) {
            ((GroupOwnerSocketHandler) socketHandler).closeSocketAndKillThisThread();
        } else if (socketHandler instanceof ClientSocketHandler) {
            ((ClientSocketHandler) socketHandler).closeSocketAndKillThisThread();
        }
    }


    /**
     * Method to disconnect and restart discovery, used by the MenuItem icon.
     * This method tries to remove the WifiP2pGroup.
     * If onSuccess, its clear the {@link it.polimi.deib.p2pchat.discovery.services.ServiceList},
     * completely stops the discovery phase and, at the end, restarts registration and discovery.
     * Finally this method updates the adapter
     */
    private void forceDisconnectAndStartDiscovery() {
        //When BroadcastReceiver gets the disconnect's notification, this method will be executed two times.
        //For this reason, i use a boolean called blockForcedDiscoveryInBroadcastReceiver to check if i
        //need to call this method from BroadcastReceiver or not.
        this.blockForcedDiscoveryInBroadcastReceiver = true;

        this.closeAndKillSocketHandler();

        this.setDisableAllChatManagers();

        if (manager != null && channel != null) {

            manager.removeGroup(channel, new ActionListener() {
                @Override
                public void onFailure(int reasonCode) {
                    Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
                    Toast.makeText(MainActivity.this, "Disconnect failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG, "Disconnected");
                    Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "Discovery status: " + discoveryStatus);

                    forceDiscoveryStop();
                    restartDiscovery();
                }

            });
        } else {
            Log.d(TAG, "Disconnect impossible");
        }
    }

    /**
     * Registers a local service.
     */
    private void startRegistration() {
        Map<String, String> record = new HashMap<>();
        record.put(Configuration.TXTRECORD_PROP_AVAILABLE, "visible");

        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
                Configuration.SERVICE_INSTANCE, Configuration.SERVICE_REG_TYPE, record);
        manager.addLocalService(channel, service,
                new CustomizableActionListener(
                        MainActivity.this,
                        "startRegistration",
                        "Added Local Service",
                        null,
                        "Failed to add a service",
                        "Failed to add a service"));
    }


    /**
     * Method that connects to the specified service.
     *
     * @param service The {@link it.polimi.deib.p2pchat.discovery.services.WiFiP2pService}
     *                to which you want to connect.
     */
    private void connectP2p(WiFiP2pService service) {
        Log.d(TAG, "connectP2p, tabNum before = " + tabNum);

        if (DestinationDeviceTabList.getInstance().containsElement(new P2pDestinationDevice(service.getDevice()))) {
            this.tabNum = DestinationDeviceTabList.getInstance().indexOfElement(new P2pDestinationDevice(service.getDevice())) + 1;
        }

        if (this.tabNum == -1) {
            Log.d("ERROR", "ERROR TABNUM=-1"); //only for testing purposes.
        }

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = service.getDevice().deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 0; //because i want that this device is the client. Attention, sometimes can be a GO, also if i used 0 here.

        if (serviceRequest != null) {
            manager.removeServiceRequest(channel, serviceRequest,
                    new CustomizableActionListener(
                            MainActivity.this,
                            "connectP2p",
                            null,
                            "RemoveServiceRequest success",
                            null,
                            "removeServiceRequest failed"));
        }

        manager.connect(channel, config,
                new CustomizableActionListener(
                        MainActivity.this,
                        "connectP2p",
                        null,
                        "Connecting to service",
                        null,
                        "Failed connecting to service"));
    }


    /**
     * Method called by {@link it.polimi.deib.p2pchat.discovery.services.WiFiP2pServicesFragment}
     * with the {@link it.polimi.deib.p2pchat.discovery.services.WiFiP2pServicesFragment.DeviceClickListener}
     * interface, when the user click on an element of the recyclerview.
     * To be precise, the call comes from {@link it.polimi.deib.p2pchat.discovery.services.WiFiServicesAdapter} to the
     * {@link it.polimi.deib.p2pchat.discovery.services.WiFiP2pServicesFragment} using
     * {@link it.polimi.deib.p2pchat.discovery.services.WiFiP2pServicesFragment.DeviceClickListener} to
     * check if the clickedPosition is correct and finally calls this method.
     *
     * @param position int that represents the lists's clicked position inside
     *                 the {@link it.polimi.deib.p2pchat.discovery.services.WiFiP2pServicesFragment}
     */
    public void tryToConnectToAService(int position) {
        WiFiP2pService service = ServiceList.getInstance().getElementByPosition(position);

        //if connected, force disconnect and restart discovery phase.
        if (connected) {
            this.forceDisconnectAndStartDiscovery();
        }

        //Finally, add device to the DeviceTabList, only if required.
        //Go to addDeviceIfRequired()'s javadoc for more informations.
        DestinationDeviceTabList.getInstance().addDeviceIfRequired(new P2pDestinationDevice(service.getDevice()));

        this.connectP2p(service);
    }

    /**
     * Method to send the {@link it.polimi.deib.p2pchat.discovery.Configuration}.MAGICADDRESSKEYWORD with the macaddress
     * of this device to the other device.
     *
     * @param deviceMacAddress String that represents the macaddress of the destination device.
     * @param name             String that represents the name of the destination device.
     */
    private void sendAddress(String deviceMacAddress, String name, ChatManager chatManager) {
        if (chatManager != null) {
            InetAddress ipAddress;
            if (socketHandler instanceof GroupOwnerSocketHandler) {
                ipAddress = ((GroupOwnerSocketHandler) socketHandler).getIpAddress();

                Log.d(TAG, "sending message with MAGICADDRESSKEYWORD, with ipaddress= " + ipAddress.getHostAddress());

                chatManager.write((Configuration.PLUSSYMBOLS + Configuration.MAGICADDRESSKEYWORD +
                        "___" + deviceMacAddress + "___" + name + "___" + ipAddress.getHostAddress()).getBytes());
            } else {
                Log.d(TAG, "sending message with MAGICADDRESSKEYWORD, without ipaddress");
                //i use "+" symbols as initial spacing to be sure that also if some initial character will be lost i'll have always
                //the Configuration.MAGICADDRESSKEYWORD and i can set the associated device to the correct WifiChatFragment.
                chatManager.write((Configuration.PLUSSYMBOLS + Configuration.MAGICADDRESSKEYWORD +
                        "___" + deviceMacAddress + "___" + name).getBytes());
            }
        }
    }

    /**
     * Method to disable all {@link it.polimi.deib.p2pchat.discovery.socketmanagers.ChatManager}'s.
     * This method iterates over all ChatManagers inside
     * the {@link it.polimi.deib.p2pchat.discovery.chatmessages.WiFiChatFragment}'s list
     * (in {@link it.polimi.deib.p2pchat.discovery.TabFragment} ) and calls "setDisable(true);".
     */
    public void setDisableAllChatManagers() {
        for (WiFiChatFragment chatFragment : TabFragment.getWiFiChatFragmentList()) {
            if (chatFragment != null && chatFragment.getChatManager() != null) {
                chatFragment.getChatManager().setDisable(true);
            }
        }
    }

    /**
     * Method to set the current item of the {@link android.support.v4.view.ViewPager} used
     * in {@link it.polimi.deib.p2pchat.discovery.TabFragment}.
     *
     * @param numPage int that represents the index of the tab to show.
     */
    public void setTabFragmentToPage(int numPage) {
        TabFragment tabfrag1 = ((TabFragment) getSupportFragmentManager().findFragmentByTag("tabfragment"));
        if (tabfrag1 != null && tabfrag1.getMViewPager() != null) {
            tabfrag1.getMViewPager().setCurrentItem(numPage);
        }
    }

    /**
     * This Method changes the color of all messages in
     * {@link it.polimi.deib.p2pchat.discovery.chatmessages.WiFiChatFragment}.
     *
     * @param grayScale a boolean that if is true removes all colors inside
     *                  {@link it.polimi.deib.p2pchat.discovery.chatmessages.WiFiChatFragment},
     *                  if false activates all colors only in the active
     *                  {@link it.polimi.deib.p2pchat.discovery.chatmessages.WiFiChatFragment},
     *                  based on the value of tabNum to select the correct tab in
     *                  {@link it.polimi.deib.p2pchat.discovery.TabFragment}.
     */
    public void addColorActiveTabs(boolean grayScale) {
        Log.d(TAG, "addColorActiveTabs() called, tabNum= " + tabNum);

        //27-02-15 : new implementation of this feature.
        if (tabFragment.isValidTabNum(tabNum) && tabFragment.getChatFragmentByTab(tabNum) != null) {
            tabFragment.getChatFragmentByTab(tabNum).setGrayScale(grayScale);
            tabFragment.getChatFragmentByTab(tabNum).updateChatMessageListAdapter();
        }
    }

    /**
     * This method sets the name of this {@link it.polimi.deib.p2pchat.discovery.model.LocalP2PDevice}
     * in the UI and inside the device. In this way, all other devices can see this updated name during the discovery phase.
     * Attention, WifiP2pManager uses an annotation called @hide to hide the method setDeviceName, in Android SDK.
     * This method uses Java reflection to call this hidden method.
     *
     * @param deviceName String that represents the visible device name of a device, during discovery.
     */
    public void setDeviceNameWithReflection(String deviceName) {
        try {
            Method m = manager.getClass().getMethod(
                    "setDeviceName",
                    new Class[]{WifiP2pManager.Channel.class, String.class,
                            WifiP2pManager.ActionListener.class});

            m.invoke(manager, channel, deviceName,
                    new CustomizableActionListener(
                            MainActivity.this,
                            "setDeviceNameWithReflection",
                            "Device name changed",
                            "Device name changed",
                            "Error, device name not changed",
                            "Error, device name not changed"));
        } catch (Exception e) {
            Log.e(TAG, "Exception during setDeviceNameWithReflection", e);
            Toast.makeText(MainActivity.this, "Impossible to change the device name", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to setup the {@link android.support.v7.widget.Toolbar}
     * as supportActionBar in this {@link android.support.v7.app.ActionBarActivity}.
     */
    private void setupToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getResources().getString(R.string.app_name));
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.inflateMenu(R.menu.action_items);
            this.setSupportActionBar(toolbar);
        }
    }


    /**
     * Method called automatically by Android.
     */
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        /*
         * The group owner accepts connections using a server socket and then spawns a
         * client socket for every client. This is handled by {@code
         * GroupOwnerSocketHandler}
         */
        if (p2pInfo.isGroupOwner) {
            Log.d(TAG, "Connected as group owner");
            try {
                Log.d(TAG, "socketHandler!=null? = " + (socketHandler != null));
                socketHandler = new GroupOwnerSocketHandler(this.getHandler());
                socketHandler.start();

                //set Group Owner ip address
                TabFragment.getWiFiP2pServicesFragment().setLocalDeviceIpAddress(p2pInfo.groupOwnerAddress.getHostAddress());

                //if this device is the Group Owner, i sets the GO's
                //ImageView of the cardview inside the WiFiP2pServicesFragment.
                TabFragment.getWiFiP2pServicesFragment().showLocalDeviceGoIcon();

            } catch (IOException e) {
                Log.e(TAG, "Failed to create a server thread - " + e);
                return;
            }
        } else {
            Log.d(TAG, "Connected as peer");
            socketHandler = new ClientSocketHandler(this.getHandler(), p2pInfo.groupOwnerAddress);
            socketHandler.start();

            //if this device is the Group Owner, i set the GO's ImageView
            //of the cardview inside the WiFiP2pServicesFragment.
            TabFragment.getWiFiP2pServicesFragment().hideLocalDeviceGoIcon();
        }

        Log.d(TAG, "onConnectionInfoAvailable setTabFragmentToPage with tabNum == " + tabNum);

        this.setTabFragmentToPage(tabNum);
    }

    /**
     * Method called automatically by Android when
     * {@link it.polimi.deib.p2pchat.discovery.socketmanagers.ChatManager}
     * calls handler.obtainMessage(***).sendToTarget().
     */
    @Override
    public boolean handleMessage(Message msg) {
        Log.d(TAG, "handleMessage, tabNum in this activity is: " + tabNum);

        switch (msg.what) {
            //called by every device at the beginning of every connection (new or previously removed and now recreated)
            case Configuration.FIRSTMESSAGEXCHANGE:
                final Object obj = msg.obj;

                Log.d(TAG, "handleMessage, " + Configuration.FIRSTMESSAGEXCHANGE_MSG + " case");

                chatManager = (ChatManager) obj;

                sendAddress(LocalP2PDevice.getInstance().getLocalDevice().deviceAddress,
                        LocalP2PDevice.getInstance().getLocalDevice().deviceName,
                        chatManager);

                break;
            case Configuration.MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;

                Log.d(TAG, "handleMessage, " + Configuration.MESSAGE_READ_MSG + " case");

                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);

                Log.d(TAG, "Message: " + readMessage);

                //message filter usage
                try {
                    MessageFilter.getInstance().isFiltered(readMessage);
                } catch (MessageException e) {
                    if (e.getReason() == MessageException.Reason.NULLMESSAGE) {
                        Log.d(TAG, "handleMessage, filter activated because the message is null = " + readMessage);
                        return true;
                    } else {
                        if (e.getReason() == MessageException.Reason.MESSAGETOOSHORT) {
                            Log.d(TAG, "handleMessage, filter activated because the message is too short = " + readMessage);
                            return true;
                        } else {
                            if (e.getReason() == MessageException.Reason.MESSAGEBLACKLISTED) {
                                Log.d(TAG, "handleMessage, filter activated because the message contains blacklisted words. Message = " + readMessage);
                                return true;
                            }
                        }
                    }
                }


                //if the message received contains Configuration.MAGICADDRESSKEYWORD is because now someone want to connect to this device
                if (readMessage.contains(Configuration.MAGICADDRESSKEYWORD)) {

                    WifiP2pDevice p2pDevice = new WifiP2pDevice();
                    p2pDevice.deviceAddress = readMessage.split("___")[1];
                    p2pDevice.deviceName = readMessage.split("___")[2];
                    P2pDestinationDevice device = new P2pDestinationDevice(p2pDevice);

                    if (readMessage.split("___").length == 3) {
                        Log.d(TAG, "handleMessage, p2pDevice created with: " + p2pDevice.deviceName + ", " + p2pDevice.deviceAddress);
                        manageAddressMessageReception(device);
                    } else if (readMessage.split("___").length == 4) {
                        device.setDestinationIpAddress(readMessage.split("___")[3]);

                        //set client ip address
                        TabFragment.getWiFiP2pServicesFragment().setLocalDeviceIpAddress(device.getDestinationIpAddress());

                        Log.d(TAG, "handleMessage, p2pDevice created with: " + p2pDevice.deviceName + ", "
                                + p2pDevice.deviceAddress + ", " + device.getDestinationIpAddress());
                        manageAddressMessageReception(device);
                    }
                }


                //i check if tabNum is valid only to be sure.
                //i using this if, because this peace of code is critical and "sometimes can throw exceptions".
                if (tabFragment.isValidTabNum(tabNum)) {

                    if (Configuration.DEBUG_VERSION) {
                        //i use this to re-format the message (not really necessary because in the "commercial"
                        //version, if a message contains MAGICADDRESSKEYWORD, this message should be removed and used
                        // only by the logic without display anything.
                        if (readMessage.contains(Configuration.MAGICADDRESSKEYWORD)) {
                            readMessage = readMessage.replace("+", "");
                            readMessage = readMessage.replace(Configuration.MAGICADDRESSKEYWORD, "Mac Address");
                        }
                        tabFragment.getChatFragmentByTab(tabNum).pushMessage("Buddy: " + readMessage);
                    } else {
                        if (!readMessage.contains(Configuration.MAGICADDRESSKEYWORD)) {
                            tabFragment.getChatFragmentByTab(tabNum).pushMessage("Buddy: " + readMessage);
                        }
                    }

                    //if the WaitingToSendQueue is not empty, send all his messages to target device.
                    if (!WaitingToSendQueue.getInstance().getWaitingToSendItemsList(tabNum).isEmpty()) {
                        tabFragment.getChatFragmentByTab(tabNum).sendForcedWaitingToSendQueue();
                    }
                } else {
                    Log.e("handleMessage", "Error tabNum = " + tabNum + " because is <=0");
                }
                break;
        }
        return true;
    }

    /**
     * Method to select the correct tab {@link it.polimi.deib.p2pchat.discovery.chatmessages.WiFiChatFragment}
     * in {@link it.polimi.deib.p2pchat.discovery.TabFragment}
     * and to prepare and to initialize everything to make chatting possible.
     * </br>
     * This is a critical method. Don't remove the Log.d messages.
     *
     * @param p2pDevice {@link it.polimi.deib.p2pchat.discovery.model.P2pDestinationDevice} that represent
     *                  the device from the string message obtained in {@link #handleMessage(android.os.Message)} in
     *                  {@code case Configuration.MESSAGE_READ}.
     */
    private void manageAddressMessageReception(P2pDestinationDevice p2pDevice) {

        if (!DestinationDeviceTabList.getInstance().containsElement(p2pDevice)) {
            Log.d(TAG, "handleMessage, p2pDevice IS NOT in the DeviceTabList -> OK! ;)");

            if (DestinationDeviceTabList.getInstance().getDevice(tabNum - 1) == null) {

                DestinationDeviceTabList.getInstance().setDevice(tabNum - 1, p2pDevice);

                Log.d(TAG, "handleMessage, p2pDevice in DeviceTabList at position tabnum= " + (tabNum - 1) + " is null");
            } else {
                DestinationDeviceTabList.getInstance().addDeviceIfRequired(p2pDevice);

                Log.d(TAG, "handleMessage, p2pDevice in DeviceTabList at position tabnum= " + (tabNum - 1) + " isn't null");
            }
        } else {
            Log.d(TAG, "handleMessage, p2pDevice IS already in the DeviceTabList -> OK! ;)");
        }

        //ok, now in this method i want to be sure to send this message to the other device with LocalDevice macaddress.
        //Before, i need to select the correct tabNum index. It's possible that this tabNum index is not correct,
        // and i need to choose a correct index to prevent Exception

        //update tabNum to select the tab associated to p2pDevice
        tabNum = DestinationDeviceTabList.getInstance().indexOfElement(p2pDevice) + 1;

        Log.d(TAG, "handleMessage, updated tabNum = " + tabNum);

        Log.d(TAG, "handleMessage, chatManager!=null? " + (chatManager != null));

        //if chatManager != null i'm receiving the message with MAGICADDRESSKEYWORD from another device
        if (chatManager != null) {
            //add a new tab, only if necessary.
            //i mean that if there is a conversation created and stopped,
            // i must restart this one and i don't create another one.
            if (tabNum > TabFragment.getWiFiChatFragmentList().size()) {
                WiFiChatFragment frag = WiFiChatFragment.newInstance();
                //adds a new fragment, sets the tabNumber with listsize+1, because i want to add an element to this list and get
                //this position, but at the moment the list is not updated. When i use listsize+1
                // i'm considering "+1" as the new element that i want to add.
                frag.setTabNumber(TabFragment.getWiFiChatFragmentList().size() + 1);
                //add new tab
                TabFragment.getWiFiChatFragmentList().add(frag);
                tabFragment.getMSectionsPagerAdapter().notifyDataSetChanged();
            }

            //update current displayed tab and the color.
            this.setTabFragmentToPage(tabNum);
            this.addColorActiveTabs(false);

            Log.d(TAG, "tabNum is : " + tabNum);

            //i set chatmanager, because if i am in Configuration.FIRSTMESSAGEXCHANGE's case is
            //when two devices starting to connect each other for the first time
            //or after a disconnect event and GroupInfo is available.
            tabFragment.getChatFragmentByTab(tabNum).setChatManager(chatManager);

            //because i don't want to re-execute the code inside this if, every received message.
            chatManager = null;
        }
    }

    //NOT IMPLEMENTED BUT PLEASE BE USEFUL
//    @Override
//    public void onChannelDisconnected() {
//        // we will try once more
//        if (manager != null && !retryChannel) {
//            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
////            resetData();
//            this.setTabFragmentToPage(0);
//            retryChannel = true;
//            manager.initialize(this, getMainLooper(), this);
//        } else {
//            Toast.makeText(this, "Severe! Channel is probably lost permanently. Try Disable/Re-Enable P2P.", Toast.LENGTH_LONG).show();
////            P2PGroups.getInstance().getGroupList().clear();
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        //activate the wakelock
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        this.setupToolBar();

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        tabFragment = TabFragment.newInstance();

        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_root, tabFragment, "tabfragment")
                .commit();

        this.getSupportFragmentManager().executePendingTransactions();
    }


    @Override
    protected void onRestart() {

        Fragment frag = getSupportFragmentManager().findFragmentByTag("services");
        if (frag != null) {
            getSupportFragmentManager().beginTransaction().remove(frag).commit();
        }

        TabFragment tabfrag = ((TabFragment) getSupportFragmentManager().findFragmentByTag("tabfragment"));
        if (tabfrag != null) {
            tabfrag.getMViewPager().setCurrentItem(0);
        }

        super.onRestart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.discovery:
                ServiceList.getInstance().clear();

                if (discoveryStatus) {
                    discoveryStatus = false;

                    item.setIcon(R.drawable.ic_action_search_stopped);

                    internalStopDiscovery();

                } else {
                    discoveryStatus = true;

                    item.setIcon(R.drawable.ic_action_search_searching);

                    startRegistration();
                    discoverService();
                }

                updateServiceAdapter();

                this.setTabFragmentToPage(0);

                return true;
            case R.id.disconenct:

                this.setTabFragmentToPage(0);

                this.forceDisconnectAndStartDiscovery();
                return true;
            case R.id.cancelConnection:

                this.setTabFragmentToPage(0);

                this.forcedCancelConnect();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiP2pBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onStop() {
        this.disconnectBecauseOnStop();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_items, menu);
        return true;
    }

}