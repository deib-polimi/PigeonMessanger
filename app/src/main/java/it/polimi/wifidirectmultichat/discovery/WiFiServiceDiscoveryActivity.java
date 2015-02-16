
package it.polimi.wifidirectmultichat.discovery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.splunk.mint.Mint;

import it.polimi.wifidirectmultichat.discovery.chatmessages.WiFiChatFragment;
import it.polimi.wifidirectmultichat.discovery.chatmessages.WiFiChatFragment.MessageTarget;
import it.polimi.wifidirectmultichat.discovery.chatmessages.waitingtosend.WaitingToSendQueue;
import it.polimi.wifidirectmultichat.discovery.services.ServiceList;
import it.polimi.wifidirectmultichat.discovery.services.WiFiP2pServicesListFragment;
import it.polimi.wifidirectmultichat.discovery.services.WiFiP2pServicesListFragment.DeviceClickListener;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import it.polimi.wifidirectmultichat.R;
import it.polimi.wifidirectmultichat.discovery.services.WiFiP2pService;
import it.polimi.wifidirectmultichat.discovery.services.WiFiServicesAdapter;
import it.polimi.wifidirectmultichat.discovery.socketmanagers.ChatManager;
import it.polimi.wifidirectmultichat.discovery.socketmanagers.ClientSocketHandler;
import it.polimi.wifidirectmultichat.discovery.socketmanagers.GroupOwnerSocketHandler;
import lombok.Getter;
import lombok.Setter;

public class WiFiServiceDiscoveryActivity extends ActionBarActivity implements
        WiFiP2pServicesListFragment.DeviceClickListener, WiFiChatFragment.CallbackActivity, Handler.Callback, MessageTarget,
        ConnectionInfoListener {

    public static final String TAG = "polimip2p";

    @Setter private boolean connected = false;

    @Getter
    private int tabNum = 1;

    @Getter
    @Setter
    private boolean blockForcesDiscoveryInBroadcastReceiver = false;

    private WifiP2pDnsSdServiceInfo service;

    @Getter
    private TabFragment tabFragment;

    private boolean discoveryStatus = true;

    @Getter
    @Setter
    private Toolbar toolbar;

    // TXT RECORD properties
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE = "_polimip2p";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";

    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;
    private WifiP2pManager manager;

    public static final int SERVER_PORT = 4545;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    private WifiP2pDnsSdServiceRequest serviceRequest;

    private Thread socketHandler;

    private Handler handler = new Handler(this);

    public Handler getHandler() {
        return handler;
    }

    @Override
    public void reconnectToService(WiFiP2pService wifiP2pService) {
        if (wifiP2pService != null) {
            //tabnum lo setto a caso, tanto il programma capisce da solo qual'e' quello corretto
            Log.d("reconnectToService", "reconnectToService");
            this.setWifiP2pDevice(wifiP2pService);
            this.connectP2p(wifiP2pService, 1);
        }
    }

//    @Override
//    public int getFragmentPositionInTabList(WiFiChatFragment fragment) {
//        if (tabFragment != null) {
//            for (int i = 0; i < tabFragment.getMSectionsPagerAdapter().getCount(); i++) {
//                if (tabFragment.getMSectionsPagerAdapter().getItem(i) instanceof WiFiChatFragment) {
//                    WiFiChatFragment frag = (WiFiChatFragment) tabFragment.getMSectionsPagerAdapter().getItem(i);
//                    Log.d("fragment_printed", "List: " + frag.getTabNumber() + " ," + fragment.getTabNumber());
//                }
//            }
//
//            return tabFragment.getItemTabNumber(fragment);
//        } else {
//            return -1;
//        }
//    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Mint.initAndStartSession(WiFiServiceDiscoveryActivity.this, "2b171946");

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
            toolbar.setTitle("WiFiDirect Chat");
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

        TabFragment tabfrag = ((TabFragment) getSupportFragmentManager().findFragmentByTag("tabfragment"));
        if (tabfrag != null) {
            tabfrag.getMViewPager().setCurrentItem(0);
        }

        super.onRestart();
    }

    @Override
    protected void onStop() {
        this.disconnectBecauseActivityOnStop();
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
                    manager.clearLocalServices(channel, new ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.d("TAG", "removeLocalService success");
                        }

                        @Override
                        public void onFailure(int reason) {
                            Log.d("TAG", "removeLocalService failure " + reason);
                        }
                    });
                } else {
                    item.setIcon(R.drawable.ic_action_search_searching);
                    ServiceList.getInstance().getServiceList().clear();
                    discoveryStatus = true;
                    startRegistrationAndDiscovery();
                }

                WiFiP2pServicesListFragment fragment = tabFragment.getWiFiP2pServicesListFragment();
                if (fragment != null) {
                    WiFiServicesAdapter adapter = ((WiFiServicesAdapter) fragment.getMAdapter());
                    adapter.notifyDataSetChanged();
                }

                this.setTabFragmentToPage(0);

                return true;
            case R.id.disconenct:

                this.setTabFragmentToPage(0);

                this.manualItemMenuDisconnectAndStartDiscovery();
                return true;
            case R.id.cancelConnection:

                this.setTabFragmentToPage(0);

                this.forcedCancelConnect();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void forcedCancelConnect() {
        manager.cancelConnect(channel, new ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("forcedCancelConnect", "cancel connect success");
                Toast.makeText(WiFiServiceDiscoveryActivity.this, "Cancel connect success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Log.d("forcedCancelConnect", "cancel connect failed, reason: " + reason);
                Toast.makeText(WiFiServiceDiscoveryActivity.this, "Cancel connect failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setTabFragmentToPage(int numPage) {
        TabFragment tabfrag1 = ((TabFragment) getSupportFragmentManager().findFragmentByTag("tabfragment"));
        if (tabfrag1 != null) {
            tabfrag1.getMViewPager().setCurrentItem(numPage);
        }
    }

    public void stopDiscoveryForced() {
        Log.d("stopDiscoveryForced", "stopDiscoveryForced");
        ServiceList.getInstance().getServiceList().clear();

        toolbar.getMenu().findItem(R.id.discovery).setIcon(getResources().getDrawable(R.drawable.ic_action_search_stopped));

        if (discoveryStatus) {
            discoveryStatus = false;

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
            manager.clearLocalServices(channel, new ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "clearLocalServices success");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(TAG, "clearLocalServices failure " + reason);
                }
            });
        }

        discoveryStatus = true;
        startRegistrationAndDiscovery();

        WiFiP2pServicesListFragment fragment = tabFragment.getWiFiP2pServicesListFragment();
        if (fragment != null) {
            WiFiServicesAdapter adapter = ((WiFiServicesAdapter) fragment.getMAdapter());
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }


    public void setDisableAllChatManagers() {

        for (WiFiChatFragment chatFragment : tabFragment.getWiFiChatFragmentList()) {

            if (chatFragment != null && chatFragment.getChatManager() != null) {
                chatFragment.getChatManager().setDisable(true);
            }
        }
    }

    public void disconnectBecauseActivityOnStop() {

        if (socketHandler instanceof GroupOwnerSocketHandler) {
            ((GroupOwnerSocketHandler) socketHandler).closeSocketAndKillThisThread();
        } else if (socketHandler instanceof ClientSocketHandler) {
            ((ClientSocketHandler) socketHandler).closeSocketAndKillThisThread();
        }

        this.setDisableAllChatManagers();

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

    public void manualItemMenuDisconnectAndStartDiscovery() {
        //serve per far si che il broadcast receiver ricevera' la notifica di disconnect, ma essendo che l'ho richiesta io
        //dopo i metodi disconnect e discovery sono eseguiti 2 volte. Quindi per evitarlo, faccio si che se richiesto questo metodo,
        //quello chiamato automaticamente dal broadcast receiver non possa essere chiamato
        this.blockForcesDiscoveryInBroadcastReceiver = true;


        Log.d("manualItemMenuDisconnectAndStartDiscovery", "manualItemMenuDisconnectAndStartDiscovery");
        if (socketHandler instanceof GroupOwnerSocketHandler) {
            ((GroupOwnerSocketHandler) socketHandler).closeSocketAndKillThisThread();
        } else if (socketHandler instanceof ClientSocketHandler) {
            ((ClientSocketHandler) socketHandler).closeSocketAndKillThisThread();
        }

        this.setDisableAllChatManagers();

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

                    ServiceList.getInstance().getServiceList().clear();
                    toolbar.getMenu().findItem(R.id.discovery).setIcon(getResources().getDrawable(R.drawable.ic_action_search_stopped));

                    if (discoveryStatus) {
                        discoveryStatus = false;

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
                        manager.clearLocalServices(channel, new ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d("TAG", "removeLocalService success");
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d("TAG", "removeLocalService failure " + reason);
                            }
                        });
                    } else {
                        discoveryStatus = true;
                    }

                    startRegistrationAndDiscovery();

                    WiFiP2pServicesListFragment fragment = tabFragment.getWiFiP2pServicesListFragment();
                    if (fragment != null) {
                        WiFiServicesAdapter adapter = ((WiFiServicesAdapter) fragment.getMAdapter());
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    }
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
            }
        });

        discoverService();

    }

    private void discoverService() {

        ServiceList.getInstance().getServiceList().clear();

        toolbar.getMenu().findItem(R.id.discovery).setIcon(getResources().getDrawable(R.drawable.ic_action_search_searching));


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
                            WiFiP2pServicesListFragment fragment = tabFragment.getWiFiP2pServicesListFragment();
                            if (fragment != null) {
                                WiFiServicesAdapter adapter = ((WiFiServicesAdapter) fragment.getMAdapter());
                                WiFiP2pService service = new WiFiP2pService();
                                service.setDevice(srcDevice);
                                service.setInstanceName(instanceName);
                                service.setServiceRegistrationType(registrationType);


                                ServiceList.getInstance().addService(service);
                                adapter.notifyItemInserted(ServiceList.getInstance().getServiceList().size()-1);
                                Log.d(TAG, "onBonjourServiceAvailable " + instanceName);
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
                        Log.d("onDnsSdTxtRecordAvail", device.deviceName + " is " + record.get(TXTRECORD_PROP_AVAILABLE));
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
                    }

                    @Override
                    public void onFailure(int arg0) {
                        Toast.makeText(WiFiServiceDiscoveryActivity.this, "Failed adding service discovery request", Toast.LENGTH_SHORT).show();
                    }
                });
        manager.discoverServices(channel, new ActionListener() {

            @Override
            public void onSuccess() {

                Toast.makeText(WiFiServiceDiscoveryActivity.this, "Service discovery initiated", Toast.LENGTH_SHORT).show();
                blockForcesDiscoveryInBroadcastReceiver = false;
            }

            @Override
            public void onFailure(int arg0) {
                Toast.makeText(WiFiServiceDiscoveryActivity.this, "Service discovery failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void setWifiP2pDevice(WiFiP2pService service1) {
        Log.d("setWifiP2pDevice", "setWifiP2pDevice device= " + service1.getDevice());
        DeviceTabList.getInstance().addDevice(service1.getDevice());

        Log.d("setWifiP2pDevice", "setWifiP2pDevice added in tab= " + (DeviceTabList.getInstance().indexOfElement(service1.getDevice()) + 1));

    }

    public void changeColorToGrayAllChats() {
        if (tabFragment != null) {
            for (WiFiChatFragment frag : tabFragment.getWiFiChatFragmentList()) {
                frag.setGrayScale(true);
                frag.updateAfterColorChange();
            }
        }
    }

    public void colorActiveTabs() {
        if (tabFragment != null) {
            for (WiFiChatFragment chatFragment : tabFragment.getWiFiChatFragmentList()) {
                if (chatFragment != null) {
                    chatFragment.setGrayScale(false);
                    chatFragment.updateAfterColorChange();
                }
            }
        }
    }

    @Override
    public void connectP2p(WiFiP2pService service, final int tabNum) {
        Log.d(TAG, "connectP2p " + tabNum);
        this.tabNum = tabNum;

        Log.d("connectP2p-1", DeviceTabList.getInstance().getDevice(tabNum - 1) + "");

        if (DeviceTabList.getInstance().containsElement(service.getDevice())) {
            Log.d("connectP2p-2", "containselement: " + service.getDevice());
            this.tabNum = DeviceTabList.getInstance().indexOfElement(service.getDevice()) + 1;
        }

        if (this.tabNum == -1) {
            Log.d("ERROR", "ERROR TABNUM=-1");
        }

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = service.getDevice().deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 0; //per farlo collegare come client
        if (serviceRequest != null)
            manager.removeServiceRequest(channel, serviceRequest,
                    new ActionListener() {

                        @Override
                        public void onSuccess() {
                            Toast.makeText(WiFiServiceDiscoveryActivity.this, "removeServiceRequest success", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onFailure(int arg0) {
                            Toast.makeText(WiFiServiceDiscoveryActivity.this, "removeServiceRequest failed", Toast.LENGTH_SHORT).show();

                        }
                    });

        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(WiFiServiceDiscoveryActivity.this, "Connecting to service", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int errorCode) {
                Toast.makeText(WiFiServiceDiscoveryActivity.this, "Failed connecting to service. Reason: " + errorCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendAddress(String deviceMacAddress, String name) {
        WiFiChatFragment frag = tabFragment.getChatFragmentByTab(tabNum);
        Log.d("sendAddress", "chatmanager is " + frag.getChatManager());
        if (frag.getChatManager() != null) {
            //uso i "+" come spaziatura iniziale per essere sicuro che il messaggio non sia perso
            //cioe' non devo perndere nessuna lettera di ADDRESS, o l'if in ricezione fallira' e non potro' settare
            //il device nella lista, creando nullpointeresxception.
            frag.getChatManager().write(("+++++++ADDRESS" + "___" + deviceMacAddress + "___" + name).getBytes());
        }
    }

    @Override
    public boolean handleMessage(Message msg) {

        WifiP2pDevice p2pDevice = null;

        Log.d("handleMessage", "handleMessage, il tabNum globale activity e': " + tabNum);

        switch (msg.what) {
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;

                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);

                if(readMessage.length()<=1) {
                    Log.d("handleMessage" ,"filtro messaggio perche' troppo corto: " + readMessage);
                    return true;
                }

                Log.d(TAG, readMessage);
                if (readMessage.contains("ADDRESS") && readMessage.split("___").length == 3) {
                    Log.d("ADDRESS", "+++ADDRESS_____ : " + readMessage);
                    p2pDevice = new WifiP2pDevice();
                    p2pDevice.deviceAddress = readMessage.split("___")[1];
                    p2pDevice.deviceName = readMessage.split("___")[2];

                    Log.d("handlemessage", "p2pDevice ottenuto: " + p2pDevice.deviceName + ", " + p2pDevice.deviceAddress);

                    if (!DeviceTabList.getInstance().containsElement(p2pDevice)) {
                        Log.d("handleMessage", "elemento non presente! OK");

                        if (DeviceTabList.getInstance().getDevice(tabNum - 1) == null) {
                            Log.d("handleMessage", "elemento in tabnum= " + (tabNum - 1) + " nullo");
                            DeviceTabList.getInstance().setDevice(tabNum - 1, p2pDevice);

                            Log.d("handleMessage", "device settato il precendeza = " + DeviceTabList.getInstance().getDevice(tabNum - 1).deviceAddress);

                        } else {
                            Log.d("handleMessage", "elemento in tabnum= " + (tabNum - 1) + " non nullo");
                            DeviceTabList.getInstance().addDevice(p2pDevice);
                        }
                    } else {
                        Log.d("handleMessage", "elemento presente! OK");
                    }

                    if (p2pDevice != null) {
                        Log.d("p2pDevice!=null", "tabNum = " + tabNum);
                        //se ho il p2pdevice diverso da null, vuol dire che lo ho settato e quindi e' la fase di scambio dei macaddress
                        //quindi devo assicurarmi di inviare il messaggio sulla chat giusta, ma per farlo devo avere l'indice
                        //corretto tabNum. Se per puro caso, ho usato il device prima per fare altro ed e' rimasto tabNum settato e ora
                        //questo valore risulta scorretto, rischio di inserire messaggi nel tab sbagliato, allora
                        //cerco l'indice cosi'

                        tabNum = DeviceTabList.getInstance().indexOfElement(p2pDevice) + 1;
                        if(tabNum<=0 || tabFragment.getWiFiChatFragmentList().size() -1 < tabNum || tabFragment.getChatFragmentByTab(tabNum)==null) {
                            tabFragment.addNewTabChatFragmentIfNecessary();
                            Log.d("handleMessage", "handleMessage, MESSAGE_READ tab added with tabnum: " + tabNum);
                            this.setTabFragmentToPage(tabNum);
                            colorActiveTabs();
                        }


                    }
                }

                if(tabNum<=0) {
                    //piuttosto che avere il tabnum sbagliato lo assegno ottenendo il tab visualizzato in quel momento, tanto
                    //e' probabile che l'utente stia nella chat giusta mentre il messagigo viene inviato.
                    Log.e("handleMessage", "errore tabnum=" + tabNum + "<=0, aggiorno tabnum");
                    tabNum = tabFragment.getMViewPager().getCurrentItem();
                    Log.e("handleMessage", "ora tabnum = " + tabNum);

                }


                Log.d("handleMessage", "handleMessage, MESSAGE_READ , il tabNum globale activity ore e': " + tabNum);

                //a volte lanciava eccezione qui perche' tabnum era 0, cioe' in tabNum = DeviceTabList.getInstance().indexOfElement(p2pDevice) + 1;
                //veniva messo a -1 ma poi sommando 1 diventava 0, e in questa riga sotto dava errore.
                //il problema non e' risolto, cosi' semplicemente non pusha a schermo il messaggio ricevuto con il macaddress
                //nel caso in cui sia la prima connessione.
                if(tabNum>=1) {
                    tabFragment.getChatFragmentByTab(tabNum).pushMessage("Buddy: " + readMessage);

                    if (!WaitingToSendQueue.getInstance().waitingToSendItemsList(tabNum).isEmpty()) {
                        Log.d(TAG, "MESSAGE_READ-svuoto la coda " + tabNum);
                        tabFragment.getChatFragmentByTab(tabNum).sendForcedWaitingToSendQueue();
                    }
                } else {
                    Log.d("handleMessage", "errore tabnum<=0,cioe' = " + tabNum);
                }

                break;

            case MY_HANDLE:
                final Object obj = msg.obj;
                Log.d("handleMessage", "MY_HANDLE");

                //aggiungo un nuovo tab
                Log.d("handleMessage", "MY_HANDLE - aggiungo tab");
                if(tabNum<=0 || tabFragment.getWiFiChatFragmentList().size() -1 < tabNum || tabFragment.getChatFragmentByTab(tabNum)==null) {
                    tabFragment.addNewTabChatFragmentIfNecessary();
                    Log.d("handleMessage", "handleMessage, MY_HANDLE tab added with tabnum: " + tabNum);
                    Log.d("handleMessage", "handleMessage, MY_HANDLE settoviepager a pagina: " + tabNum);
                    tabFragment.getMViewPager().setCurrentItem(tabNum);
                    colorActiveTabs();
                }

                manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup group) {
                        //il group owner comunica il suo indirizzo al client
                        if (LocalP2PDevice.getInstance().getLocalDevice() != null) {

                            tabFragment.getChatFragmentByTab(tabNum).setChatManager((ChatManager) obj);

                            Log.d("requestGroupInfo", "isGO= " + group.isGroupOwner() + ". Sending address: " + LocalP2PDevice.getInstance().getLocalDevice().deviceAddress);
                            sendAddress(LocalP2PDevice.getInstance().getLocalDevice().deviceAddress, LocalP2PDevice.getInstance().getLocalDevice().deviceName);
                        }

                        Log.d(TAG, "MY_HANDLE-svuoto la coda " + tabNum);
                        tabFragment.getChatFragmentByTab(tabNum).sendForcedWaitingToSendQueue();

                    }
                });
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
    public void onDestroy() {
        super.onDestroy();

//        Mint.closeSession(WiFiServiceDiscoveryActivity.this);
    }

    /**
     * Metodo che setta il nome del dispositivo tramite refplection.
     * @param deviceName
     */
    public void setDeviceNameWithReflection(String deviceName) {
        try {
            Method m = manager.getClass().getMethod (
                    "setDeviceName",
                    new Class[] { WifiP2pManager.Channel.class, String.class,
                            WifiP2pManager.ActionListener.class });

            m.invoke(manager,channel, deviceName, new WifiP2pManager.ActionListener() {
                public void onSuccess() {
                    //Code for Success in changing name
                    Log.d("reflection","device OK");
                    Toast.makeText(WiFiServiceDiscoveryActivity.this, "Device name changed", Toast.LENGTH_SHORT).show();
                }

                public void onFailure(int reason) {
                    //Code to be done while name change Fails
                    Log.d("reflection","device FAILURE");
                    Toast.makeText(WiFiServiceDiscoveryActivity.this, "Error, device name not changed", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void tryToConnectToAService(int position) {
        WiFiP2pService service = ServiceList.getInstance().getServiceList().get(position);

        if(connected) {
            this.manualItemMenuDisconnectAndStartDiscovery();
        }
        this.setWifiP2pDevice(service);
        this.connectP2p(service, 1);
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

                //se e' group owner METTO il logo GO nella cardview del serviceslistfragment.
                tabFragment.getWiFiP2pServicesListFragment().showLocalDeviceGoIcon();


            } catch (IOException e) {
                Log.d(TAG, "Failed to create a server thread - " + e.getMessage());
                return;
            }
        } else {
            Log.d(TAG, "Connected as peer");
            socketHandler = new ClientSocketHandler(((MessageTarget) this).getHandler(), p2pInfo.groupOwnerAddress);
            socketHandler.start();

            //se non e' group owner TOLGO il logo GO nella cardview del serviceslistfragment, nel casso fosse stato settato in precedenza
            tabFragment.getWiFiP2pServicesListFragment().hideLocalDeviceGoIcon();
        }


        final TabFragment tabfrag = ((TabFragment) getSupportFragmentManager().findFragmentByTag("tabfragment"));
        Log.d("onConnectionInfoAvailable", "onConnectionInfoAvailable tabNum = " + tabNum);
        this.setTabFragmentToPage(tabNum);
    }


}
