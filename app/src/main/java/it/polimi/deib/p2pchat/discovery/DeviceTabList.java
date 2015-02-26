package it.polimi.deib.p2pchat.discovery;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.polimi.deib.p2pchat.discovery.utilities.UseOnlyPrivateHere;

/**
 * Class that represent the tab's list of {@link it.polimi.deib.p2pchat.discovery.chatmessages.WiFiChatFragment}
 * This class contains a logic to "remap" the tab index to the List<{@link android.net.wifi.p2p.WifiP2pDevice}> index.
 * Because the first tab is reserved to {@link it.polimi.deib.p2pchat.discovery.services.WiFiP2pServicesFragment}.
 * Created by Stefano Cappa on 12/02/15.
 */
public class DeviceTabList {

    //ATTENTION DO NOT EXPOSE THIS ATTRIBUTE, BUT CREATE A SECURE METHOD TO MANAGE THIS LISTS!!!
    //SEE THE ANNOTATION, USE ONLY PRIVATE HERE WITHOUT GETTERS OR SETTERS!!!
    @UseOnlyPrivateHere private final List<WifiP2pDevice> deviceList;

    private static final DeviceTabList instance = new DeviceTabList();

    /**
     * Method to get the instance of this class.
     * @return instance of this class.
     */
    public static DeviceTabList getInstance() {
        return instance;
    }

    /**
     * Private constructor, because is a singleton class.
     */
    private DeviceTabList() {
        deviceList = new ArrayList<>();
    }


    /**
     * Method to get a {@link android.net.wifi.p2p.WifiP2pDevice}
     * specifying the position of the element in the list.
     * If the condition (pos>=0 && pos <= deviceList.size() - 1) is false, return null.
     * @param pos integer position
     * @return {@link android.net.wifi.p2p.WifiP2pDevice} element at position pos, inside the deviceList or null.
     */
    public WifiP2pDevice getDevice(int pos) {
        if (pos>=0 && pos <= deviceList.size() - 1) {
            return deviceList.get(pos);
        }
        return null;
    }

    /**
     * Method that sets a {@link android.net.wifi.p2p.WifiP2pDevice} at position pos inside the deviceList.
     * This method replaces the element at position pos, if this deviceList.size() have an element at this position.
     * If not, the device will be added to the deviceList, at position pos.
     * This method never fails! You have the warranty that the element will be added to the list.
     * If you pass a device==null, in the list you will get a null element. Be careful!
     * @param pos integer position
     * @param device {@link android.net.wifi.p2p.WifiP2pDevice} element the you want add to the deviceList.
     */
    public void setDevice(int pos, @NonNull WifiP2pDevice device) {
        if (pos>=0 && pos <= deviceList.size() - 1) {
            deviceList.set(pos, device);
        } else {
            deviceList.add(pos, device);
        }
    }

    /**
     * Method to add a {@link android.net.wifi.p2p.WifiP2pDevice} in the list, only if it isn't already in.
     * If the list contains null elements, this method search the first one and it replaces with the device parameter.
     * If all the element in the list are !=null, it adds the new device ad the end.
     * This method never fails! You have the warranty that the element will be added to the list.
     * If you pass a device==null, in the list you will get a null element. Be careful!
     * @param device {@link android.net.wifi.p2p.WifiP2pDevice} element the you want add to the deviceList.
     */
    public void addDeviceIfRequired(@NonNull WifiP2pDevice device) {
        boolean add = true;
        for (WifiP2pDevice element : deviceList) {
            if (element != null && element.equals(device)) {
                add = false; //already in
            }
        }

        // i must add this element
        if (add) {
            // i search the first null element e i replace this with
            // the device obtained as parameter of this method.
            for (int i = 0; i < deviceList.size(); i++) {
                if (deviceList.get(i) == null) {
                    deviceList.set(i, device);
                    return;
                }
            }

            //if this list hasn't null element i add this device at the end of the list
            deviceList.add(device);
        }
    }

    /**
     * Method that check if a {@link android.net.wifi.p2p.WifiP2pDevice} is in the deviceList.
     * If the device parameter is null, this method returns false!
     * @param device {@link android.net.wifi.p2p.WifiP2pDevice} element.
     * @return true if the element is in the list, false otherwise or if the device parameter is null.
     */
    public boolean containsElement(WifiP2pDevice device) {
        if(device==null) {
            return false;
        }

        for (WifiP2pDevice element : deviceList) {
            if (element != null && element.deviceAddress.equals(device.deviceAddress)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to get the index position of a {@link android.net.wifi.p2p.WifiP2pDevice} from deviceList.
     * If the device is not contained in the list or device == null, it returns -1.
     * Attention: sometimes Android gives a WifiP2pDevice without the deviceName. For this reason i check
     * the element in the list only with the deviceAddress.
     * @param device {@link android.net.wifi.p2p.WifiP2pDevice} element.
     * @return int that represents the index inside the {@link java.util.List} deviceList.
     */
    public int indexOfElement(WifiP2pDevice device) {
        if(device==null) {
            return -1;
        }

        for (int i = 0; i < deviceList.size(); i++) {
            if (deviceList.get(i) != null && deviceList.get(i).deviceAddress.equals(device.deviceAddress)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Method to retrieve the size of {@link #deviceList}
     * @return int that represents the number of elements in {@link #deviceList}
     */
    public int getSize() {
        return deviceList.size();
    }
}
