package com.example.android.wifidirect.discovery;

import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Created by Stefano Cappa on 04/02/15.
 */
public class ServiceList {

    @Getter
    private static List<WiFiP2pService> serviceList;

    private static ServiceList instance = new ServiceList();

    /**
     * Metodo che permette di ottenere l'istanza della classe.
     *
     * @return istanza della classe.
     */
    public static ServiceList getInstance() {
        Log.d("ServiceList", "size: " + serviceList.size());
        return instance;
    }

    private ServiceList() {
        serviceList = new ArrayList<>();
    }

    public void addService(WiFiP2pService service) {
        boolean add = true;
        for (WiFiP2pService element : serviceList) {
            if (element.device.equals(service.device) && element.instanceName.equals(service.instanceName)) {
                add = false; //gia' presente
            }
        }

        if(add) {
            serviceList.add(service);
        }

//        Log.d("serviceListElement", "State: " + add + ". Element: " + service.device + ", "  + service.instanceName + ", " + service.serviceRegistrationType);
    }

}
