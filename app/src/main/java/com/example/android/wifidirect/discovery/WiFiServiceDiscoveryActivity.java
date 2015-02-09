
package com.example.android.wifidirect.discovery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.wifidirect.discovery.WiFiChatFragment.MessageTarget;
import com.example.android.wifidirect.discovery.WiFiDirectServicesList.DeviceClickListener;
import com.example.android.wifidirect.discovery.WiFiDirectServicesList.WiFiDevicesAdapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * The main activity for the sample. This activity registers a local service and
 * perform discovery over Wi-Fi p2p network. It also hosts a couple of fragments
 * to manage chat operations. When the app is launched, the device publishes a
 * chat service and also tries to discover services published by other peers. On
 * selecting a peer published service, the app initiates a Wi-Fi P2P (Direct)
 * connection with the peer. On successful connection with a peer advertising
 * the same service, the app opens up sockets to initiate a chat.
 * {@code WiFiChatFragment} is then added to the the main activity which manages
 * the interface and messaging needs for a chat session.
 */
public class WiFiServiceDiscoveryActivity extends ActionBarActivity implements
        DeviceClickListener, Handler.Callback, MessageTarget,
        ConnectionInfoListener {

    public static final String TAG = "wifidirectdemo";

    @Getter
    private int tabNum = 1;

    private WifiP2pDnsSdServiceInfo service;

    private TabFragment tabFragment;

    private boolean discoveryStatus = true, disconnectStatus, refreshStatus;

    @Getter
    @Setter
    private Toolbar toolbar;

    // TXT RECORD properties
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE = "_wifidemotest";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";

    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;
    private WifiP2pManager manager;

    static final int SERVER_PORT = 4545;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    private WifiP2pDnsSdServiceRequest serviceRequest;

    private Thread socketHandler;

    private Handler handler = new Handler(this);

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        this.setupToolBar();

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        startRegistrationAndDiscovery();

        tabFragment = TabFragment.newInstance();

        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_root, tabFragment, "tabfragment")
                .commit();

        this.getSupportFragmentManager().executePendingTransactions();
    }


    public void setupToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setTitle("Multi-Group Chat");
            toolbar.setTitleTextColor(Color.WHITE);

            toolbar.inflateMenu(R.menu.action_items);

            this.setSupportActionBar(toolbar);
        }
    }

    @Override
    protected void onRestart() {

        Fragment frag = getSupportFragmentManager().findFragmentByTag("services");
        if (frag != null) {
            getSupportFragmentManager().beginTransaction().remove(frag).commit();
        }
        super.onRestart();
    }

    @Override
    protected void onStop() {
//        tabFragment.getWiFiChatFragment1().getChatManager().setDisable(true);

        this.disconnect();
        super.onStop();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.discovery:
                if (discoveryStatus) {
                    discoveryStatus = false;
                    manager.removeLocalService(channel, service, new ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.d("TAG", "removeLocalService success");
                        }

                        @Override
                        public void onFailure(int reason) {
                            Log.d("TAG", "removeLocalService failure " + reason);

                        }
                    });
                    ServiceList.getInstance().getServiceList().clear();
                    item.setIcon(R.drawable.ic_action_search_stopped);
                    manager.stopPeerDiscovery(channel, new ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Discovery stopped");
                            Toast.makeText(WiFiServiceDiscoveryActivity.this, "Discovery stopped", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int reason) {
                            Log.d(TAG, "Discovery stop failed. Reason :" + reason);
                            Toast.makeText(WiFiServiceDiscoveryActivity.this, "Discovery stop failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    manager.clearServiceRequests(channel, new ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "clearServiceRequests success");
                        }

                        @Override
                        public void onFailure(int reason) {
                            Log.d(TAG, "clearServiceRequests failed: " + reason);
                        }
                    });
                } else {
                    item.setIcon(R.drawable.ic_action_search_searching);
                    ServiceList.getInstance().getServiceList().clear();
                    discoveryStatus = true;
                    startRegistrationAndDiscovery();
                }

                WiFiDirectServicesList fragment = tabFragment.getWiFiDirectServicesList();
//                                    (WiFiDirectServicesList) getSupportFragmentManager()
//                                    .findFragmentByTag("services");
                if (fragment != null) {
                    WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) fragment
                            .getListAdapter());
                    adapter.notifyDataSetChanged();
                }
                return true;
            case R.id.disconenct:
                this.disconnectAndStartDiscovery();
                return true;
            case R.id.refresh:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void stopDiscoveryForced () {
        if (discoveryStatus) {
            discoveryStatus = false;
            manager.removeLocalService(channel, service, new ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d("TAG", "removeLocalService success");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d("TAG", "removeLocalService failure " + reason);

                }
            });
            ServiceList.getInstance().getServiceList().clear();
//            item.setIcon(R.drawable.ic_action_search_stopped);
            manager.stopPeerDiscovery(channel, new ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Discovery stopped");
                    Toast.makeText(WiFiServiceDiscoveryActivity.this, "Discovery stopped", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(TAG, "Discovery stop failed. Reason :" + reason);
                    Toast.makeText(WiFiServiceDiscoveryActivity.this, "Discovery stop failed", Toast.LENGTH_SHORT).show();
                }
            });
            manager.clearServiceRequests(channel, new ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "clearServiceRequests success");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(TAG, "clearServiceRequests failed: " + reason);
                }
            });
        } else {
//            item.setIcon(R.drawable.ic_action_search_searching);
            ServiceList.getInstance().getServiceList().clear();

        }

        discoveryStatus = true;
        startRegistrationAndDiscovery();

        WiFiDirectServicesList fragment = tabFragment.getWiFiDirectServicesList();
//                                    (WiFiDirectServicesList) getSupportFragmentManager()
//                                    .findFragmentByTag("services");
        if (fragment != null) {
            WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) fragment
                    .getListAdapter());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }

    public void setDisableAllChatManagers() {
        if (tabNum == 1 && tabFragment.getWiFiChatFragment1()!=null && tabFragment.getWiFiChatFragment1().getChatManager()!=null) {
            tabFragment.getWiFiChatFragment1().getChatManager().setDisable(true);
        } else if (tabNum == 2 && tabFragment.getWiFiChatFragment2()!=null && tabFragment.getWiFiChatFragment2().getChatManager()!=null) {
            tabFragment.getWiFiChatFragment2().getChatManager().setDisable(true);
        }
    }

    public void disconnect() {

        if(socketHandler instanceof GroupOwnerSocketHandler) {
            ((GroupOwnerSocketHandler)socketHandler).closeSocketAndKillThisThread();
        } else if (socketHandler instanceof ClientSocketHandler) {
            ((ClientSocketHandler)socketHandler).closeSocketAndKillThisThread();
        }

        if (tabNum == 1 && tabFragment.getWiFiChatFragment1()!=null && tabFragment.getWiFiChatFragment1().getChatManager()!=null) {
            tabFragment.getWiFiChatFragment1().getChatManager().setDisable(true);
        } else if (tabNum == 2 && tabFragment.getWiFiChatFragment2()!=null && tabFragment.getWiFiChatFragment2().getChatManager()!=null) {
            tabFragment.getWiFiChatFragment2().getChatManager().setDisable(true);
        }
        this.changeColorToGrayAllChats();

        if (manager != null && channel != null) {
            manager.removeGroup(channel, new ActionListener() {

                @Override
                public void onFailure(int reasonCode) {
                    Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
                    Toast.makeText(WiFiServiceDiscoveryActivity.this, "Disconnect failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG, "Disconnected");
                    Toast.makeText(WiFiServiceDiscoveryActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                }

            });
        } else {
            Log.d(TAG, "Disconnect impossible");
        }
    }

    public void disconnectAndStartDiscovery() {
        if(socketHandler instanceof GroupOwnerSocketHandler) {
            ((GroupOwnerSocketHandler)socketHandler).closeSocketAndKillThisThread();
        } else if (socketHandler instanceof ClientSocketHandler) {
            ((ClientSocketHandler)socketHandler).closeSocketAndKillThisThread();
        }

        if (tabNum == 1 && tabFragment.getWiFiChatFragment1()!=null && tabFragment.getWiFiChatFragment1().getChatManager()!=null) {
            tabFragment.getWiFiChatFragment1().getChatManager().setDisable(true);
        } else if (tabNum == 2 && tabFragment.getWiFiChatFragment2()!=null && tabFragment.getWiFiChatFragment2().getChatManager()!=null) {
            tabFragment.getWiFiChatFragment2().getChatManager().setDisable(true);
        }

        if (manager != null && channel != null) {
            manager.removeGroup(channel, new ActionListener() {

                @Override
                public void onFailure(int reasonCode) {
                    Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
                    Toast.makeText(WiFiServiceDiscoveryActivity.this, "Disconnect failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG, "Disconnected");
                    Toast.makeText(WiFiServiceDiscoveryActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "Discovery status: " + discoveryStatus);

                    if (discoveryStatus) {
                        discoveryStatus = false;
                        manager.removeLocalService(channel, service, new ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d("TAG", "removeLocalService success");
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d("TAG", "removeLocalService failure " + reason);

                            }
                        });
                        ServiceList.getInstance().getServiceList().clear();
//                        item.setIcon(R.drawable.ic_action_search_stopped);
                        manager.stopPeerDiscovery(channel, new ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "Discovery stopped");
                                Toast.makeText(WiFiServiceDiscoveryActivity.this, "Discovery stopped", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d(TAG, "Discovery stop failed. Reason :" + reason);
                                Toast.makeText(WiFiServiceDiscoveryActivity.this, "Discovery stop failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        manager.clearServiceRequests(channel, new ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "clearServiceRequests success");
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d(TAG, "clearServiceRequests failed: " + reason);
                            }
                        });
                    } else {
//                        item.setIcon(R.drawable.ic_action_search_searching);
                        ServiceList.getInstance().getServiceList().clear();
                        discoveryStatus = true;

                    }

                    startRegistrationAndDiscovery();

                    WiFiDirectServicesList fragment = tabFragment.getWiFiDirectServicesList();
//                                    (WiFiDirectServicesList) getSupportFragmentManager()
//                                    .findFragmentByTag("services");
                    if (fragment != null) {
                        WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) fragment
                                .getListAdapter());
                        adapter.notifyDataSetChanged();
                    }

//                    startRegistrationAndDiscovery();
                }

            });
        } else {
            Log.d(TAG, "Disconnect impossible");
        }
    }

    /**
     * Registers a local service and then initiates a service discovery
     */
    public void startRegistrationAndDiscovery() {
        Map<String, String> record = new HashMap<String, String>();
        record.put(TXTRECORD_PROP_AVAILABLE, "visible");

        this.service = WifiP2pDnsSdServiceInfo.newInstance(
                SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
        manager.addLocalService(channel, service, new ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(WiFiServiceDiscoveryActivity.this, "Added Local Service", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Added Local Service");
            }

            @Override
            public void onFailure(int error) {
                Toast.makeText(WiFiServiceDiscoveryActivity.this, "Failed to add a service", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Failed to add a service");
//                appendStatus("Failed to add a service");
            }
        });

        discoverService();

    }

    private void discoverService() {

        /*
         * Register listeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */

        manager.setDnsSdResponseListeners(channel,
                new DnsSdServiceResponseListener() {

                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType, WifiP2pDevice srcDevice) {

                        // A service has been discovered. Is this our app?

                        if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {

                            // update the UI and add the item the discovered
                            // device.
                            WiFiDirectServicesList fragment = tabFragment.getWiFiDirectServicesList();
//                                    (WiFiDirectServicesList) getSupportFragmentManager()
//                                    .findFragmentByTag("services");
                            if (fragment != null) {
                                WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) fragment
                                        .getListAdapter());
                                WiFiP2pService service = new WiFiP2pService();
                                service.device = srcDevice;
                                service.instanceName = instanceName;
                                service.serviceRegistrationType = registrationType;
//                                adapter.add(service);
                                ServiceList.getInstance().getServiceList().add(service);
                                adapter.notifyDataSetChanged();
                                Log.d(TAG, "onBonjourServiceAvailable "
                                        + instanceName);
                            }
                        }

                    }
                }, new DnsSdTxtRecordListener() {

                    /**
                     * A new TXT record is available. Pick up the advertised
                     * buddy name.
                     */
                    @Override
                    public void onDnsSdTxtRecordAvailable(
                            String fullDomainName, Map<String, String> record,
                            WifiP2pDevice device) {
                        Log.d(TAG,
                                device.deviceName + " is "
                                        + record.get(TXTRECORD_PROP_AVAILABLE));
                    }
                });

        // After attaching listeners, create a service request and initiate
        // discovery.
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();

        manager.addServiceRequest(channel, serviceRequest,
                new ActionListener() {

                    @Override
                    public void onSuccess() {

                        Toast.makeText(WiFiServiceDiscoveryActivity.this, "Added service discovery request", Toast.LENGTH_SHORT).show();
//                        appendStatus("Added service discovery request");
                    }

                    @Override
                    public void onFailure(int arg0) {
                        Toast.makeText(WiFiServiceDiscoveryActivity.this, "Failed adding service discovery request", Toast.LENGTH_SHORT).show();
//                        appendStatus("Failed adding service discovery request");
                    }
                });
        manager.discoverServices(channel, new ActionListener() {

            @Override
            public void onSuccess() {

                Toast.makeText(WiFiServiceDiscoveryActivity.this, "Service discovery initiated", Toast.LENGTH_SHORT).show();
//                appendStatus("Service discovery initiated");
            }

            @Override
            public void onFailure(int arg0) {
                Toast.makeText(WiFiServiceDiscoveryActivity.this, "Service discovery failed", Toast.LENGTH_SHORT).show();
//                appendStatus("Service discovery failed");

            }
        });
    }

    public void setWifiP2pDevice(WiFiP2pService service1, int tabNum) {
        if (tabNum == 1 && tabFragment.getWiFiChatFragment1()!=null) {
            tabFragment.getWiFiChatFragment1().setDevice(service1.device);
        } else if (tabNum == 2 && tabFragment.getWiFiChatFragment2()!=null) {
            tabFragment.getWiFiChatFragment2().setDevice(service1.device);
        }


    }

    public void changeColorToGrayAllChats() {
        if(tabFragment!=null) {
            if(tabFragment.getWiFiChatFragment1()!=null) {
                tabFragment.getWiFiChatFragment1().setGrayScale(true);
                tabFragment.getWiFiChatFragment1().updateAfterColorChange();
            }
            if(tabFragment.getWiFiChatFragment2()!=null) {
                tabFragment.getWiFiChatFragment2().setGrayScale(true);
                tabFragment.getWiFiChatFragment2().updateAfterColorChange();
            }
        }
    }

    public void changeColorAllChats() {
        if(tabNum==1 && tabFragment!=null && tabFragment.getWiFiChatFragment1()!=null ) {
            tabFragment.getWiFiChatFragment1().setGrayScale(false);
            tabFragment.getWiFiChatFragment1().updateAfterColorChange();
        } else if(tabNum==2  && tabFragment!=null && tabFragment.getWiFiChatFragment2()!=null ) {
            tabFragment.getWiFiChatFragment2().setGrayScale(false);
            tabFragment.getWiFiChatFragment2().updateAfterColorChange();
        }
    }

    @Override
    public void connectP2p(WiFiP2pService service, final int tabNum) {
        Log.d(TAG, "connectP2p " + tabNum);
        this.tabNum = tabNum;

        if(tabFragment.getWiFiChatFragment1().getDevice().deviceAddress.equals(service.device.deviceAddress)) {
            this.tabNum = 1;
        } else if(tabFragment.getWiFiChatFragment2().getDevice().deviceAddress.equals(service.device.deviceAddress)) {
            this.tabNum = 2;
        }

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = service.device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 0; //per farlo collegare come client
        if (serviceRequest != null)
            manager.removeServiceRequest(channel, serviceRequest,
                    new ActionListener() {

                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(int arg0) {
                        }
                    });

        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(WiFiServiceDiscoveryActivity.this, "Connecting to service", Toast.LENGTH_SHORT).show();

//                appendStatus("Connecting to service");
            }

            @Override
            public void onFailure(int errorCode) {
                Toast.makeText(WiFiServiceDiscoveryActivity.this, "Failed connecting to service. Reason: " + errorCode, Toast.LENGTH_SHORT).show();

//                appendStatus("Failed connecting to service");
            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {

        Log.d("handleMessage","handleMessage");

        switch (msg.what) {
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                Log.d(TAG, readMessage);
                if (tabNum == 1) {
                    (tabFragment.getWiFiChatFragment1()).pushMessage("Buddy: " + readMessage);
                } else if (tabNum == 2) {
                    (tabFragment.getWiFiChatFragment2()).pushMessage("Buddy: " + readMessage);
                }


                if(!WaitingToSendQueue.getInstance().waitingToSendItemsList(tabNum).isEmpty()) {

                    if (tabNum == 1) {
                        Log.d(TAG, "MESSAGE_READ-svuoto la coda 1");
                        tabFragment.getWiFiChatFragment1().sendForcedWaitingToSendQueue();
                    } else if (tabNum == 2) {
                        Log.d(TAG, "MESSAGE_READ-svuoto la coda 2");
                        tabFragment.getWiFiChatFragment2().sendForcedWaitingToSendQueue();
                    }
                }
                break;

            case MY_HANDLE:
                Object obj = msg.obj;
                Log.d("handleMessage","MY_HANDLE");
                if (tabNum == 1) {
                    (tabFragment.getWiFiChatFragment1()).setChatManager((ChatManager) obj);
                } else if (tabNum == 2) {
                    (tabFragment.getWiFiChatFragment2()).setChatManager((ChatManager) obj);
                }

                if(tabNum==1) {
                    Log.d(TAG, "MY_HANDLE-svuoto la coda 1");
                    tabFragment.getWiFiChatFragment1().sendForcedWaitingToSendQueue();
                } else if (tabNum==2) {
                    Log.d(TAG, "MY_HANDLE-svuoto la coda 2");
                    tabFragment.getWiFiChatFragment2().sendForcedWaitingToSendQueue();
                }
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

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
                socketHandler = new GroupOwnerSocketHandler(((MessageTarget) this).getHandler());
                socketHandler.start();
            } catch (IOException e) {
                Log.d(TAG,
                        "Failed to create a server thread - " + e.getMessage());
                return;
            }
        } else {
            Log.d(TAG, "Connected as peer");
            socketHandler = new ClientSocketHandler(((MessageTarget) this).getHandler(),p2pInfo.groupOwnerAddress);
            socketHandler.start();
        }


        TabFragment tabfrag = ((TabFragment) getSupportFragmentManager().findFragmentByTag("tabfragment"));
        tabfrag.getMViewPager().setCurrentItem(tabNum);
    }


}
