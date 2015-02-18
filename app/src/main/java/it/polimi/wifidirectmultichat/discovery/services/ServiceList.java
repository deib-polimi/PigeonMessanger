package it.polimi.wifidirectmultichat.discovery.services;

import android.net.wifi.p2p.WifiP2pDevice;

import java.util.ArrayList;
import java.util.List;

import it.polimi.wifidirectmultichat.discovery.utilities.UseOnlyPrivateHere;

/**
 * Class that represents a list of {@link it.polimi.wifidirectmultichat.discovery.services.WiFiP2pService }.
 * This list contains all the device found during discovery phase of the wifi direct protocol.
 * This class use Singleton pattern.
 *
 * Created by Stefano Cappa on 04/02/15.
 */
public class ServiceList {

    //ATTENTION DO NOT EXPOSE THIS ATTRIBUTE, BUT CREATE A SECURE METHOD TO MANAGE THIS LISTS!!!
    //SEE THE ANNOTATION, USE ONLY PRIVATE HERE WITHOUT GETTERS OR SETTERS!!!
    @UseOnlyPrivateHere private final List<WiFiP2pService> serviceList;

    private static final ServiceList instance = new ServiceList();

    /**
     * Method to get the instance of this class.
     * @return instance of this class.
     */
    public static ServiceList getInstance() {
        return instance;
    }

    /**
     * Private constructor, because is a singleton class.
     */
    private ServiceList() {
        serviceList = new ArrayList<>();
    }


    /**
     * Method to add a service inside the list in a secure way.
     * The service is added only if isn't already inside the list.
     * @param service {@link WiFiP2pService} to add.
     */
    public void addServiceIfNotPresent(WiFiP2pService service) {
        if(service==null) {
            return;
        }

        boolean add = true;
        for (WiFiP2pService element : serviceList) {
            if (element.getDevice().equals(service.getDevice())
                    && element.getInstanceName().equals(service.getInstanceName())) {
                add = false; //already in the list
            }
        }

        if(add) {
            serviceList.add(service);
        }
    }

    /**
     * Method to get a service from the list, using only the device.
     * This method use only the deviceAddress, not the device name, because sometimes Android doesn't
     * get the name, but only the mac address.
     * @param device {@link WifiP2pDevice} that you want to use to search the service.
     * @return The {@link WiFiP2pService } associated to the device or null, if the device isn't in the list.
     */
    public WiFiP2pService getServiceByDevice(WifiP2pDevice device) {
        if(device==null) {
            return null;
        }

        for (WiFiP2pService element : serviceList) {
            if (element.getDevice().deviceAddress.equals(device.deviceAddress) ) {
                return element;
            }
        }
        return null;
    }

    /**
     * Method to get the size of the list.
     * @return int that represent the size.
     */
    public int getSize() {
        return serviceList.size();
    }


    /**
     * Method to clear the list.
     */
    public void clear() {
        serviceList.clear();
    }

    /**
     * Method to get a element using only the real position in the list (from 0 to n).
     * @param position int that represents the position of the element.
     * @return A {@link WiFiP2pService } element from the list at position.
     * Attention, this method can throw a {@link IndexOutOfBoundsException },
     * if {@code position < 0 || position >= size()}
     */
    public WiFiP2pService getElementByPosition(int position) {
        return serviceList.get(position);
    }
}
