package it.polimi.wifidirectmultichat.discovery;

import android.net.wifi.p2p.WifiP2pDevice;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Created by Stefano Cappa on 12/02/15.
 */
public class DeviceTabList {

    @Getter private List<WifiP2pDevice> deviceList;
    private static final int MAXIMUM_SIZE = 6;

    private static DeviceTabList instance = new DeviceTabList();

    /**
     * Metodo che permette di ottenere l'istanza della classe.
     *
     * @return istanza della classe.
     */
    public static DeviceTabList getInstance() {
        return instance;
    }

    private DeviceTabList() {
        deviceList = new ArrayList<>(MAXIMUM_SIZE);
        deviceList.add(new WifiP2pDevice()); //rappresenta il primo tab, che e' riservato alla servicelist che non posso toccare
        deviceList.add(null);
        deviceList.add(null);
        deviceList.add(null);
        deviceList.add(null);
        deviceList.add(null);
    }

    public void addDevice(WifiP2pDevice device) {
        boolean add = true;
        for (WifiP2pDevice element : deviceList) {
            if (element!=null && element.equals(device)) {
                add = false; //gia' presente
            }
        }

        //devo aggiungerlo
        if(add) {
            //prima cerco il primo elemento nullo e lo metto li e temrino il metodo
            for (int i=0; i<deviceList.size(); i++) {
                if(deviceList.get(i)==null) {
                    deviceList.set(i,device);
                    return;
                }
            }

            //altrimenti, lo aggiungo a mano.
            deviceList.add(device);
        }
    }

    public boolean containsElement(WifiP2pDevice device) {
        for (WifiP2pDevice element : deviceList) {
            if (element!=null && element.deviceAddress.equals(device.deviceAddress) && element.deviceName.equals(device.deviceName)) {
                return true;
            }
        }
        return false;
    }

    public int indexOfElement(WifiP2pDevice device) {
        for (int i=0; i<deviceList.size(); i++) {
            if (deviceList.get(i)!=null && deviceList.get(i).deviceAddress.equals(device.deviceAddress) && deviceList.get(i).deviceName.equals(device.deviceName)) {
                return i;
            }
        }
        return -1;
    }
}
