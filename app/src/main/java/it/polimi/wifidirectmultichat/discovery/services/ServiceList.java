package it.polimi.wifidirectmultichat.discovery.services;

import android.net.wifi.p2p.WifiP2pDevice;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Created by Stefano Cappa on 04/02/15.
 */
public class ServiceList {

    @Getter private List<WiFiP2pService> serviceList;

    private static ServiceList instance = new ServiceList();

    /**
     * Metodo che permette di ottenere l'istanza della classe.
     *
     * @return istanza della classe.
     */
    public static ServiceList getInstance() {
        return instance;
    }

    private ServiceList() {
        serviceList = new ArrayList<>();
    }

    public void addService(WiFiP2pService service) {
        boolean add = true;
        for (WiFiP2pService element : serviceList) {
            if (element.getDevice().equals(service.getDevice())
                    && element.getInstanceName().equals(service.getInstanceName())) {
                add = false; //gia' presente
            }
        }

        if(add) {
            serviceList.add(service);
        }
    }

    //a volte non si sa perche' non riesce ad ottenere il nome durante la fase di discovery, quindi in questo metodo controllo solo il mac address
    public WiFiP2pService getServiceByDevice(WifiP2pDevice device) {
        for (WiFiP2pService element : serviceList) {
            if (element.getDevice().deviceAddress.equals(device.deviceAddress) ) {
                return element;
            }
        }
        return null;
    }
}
