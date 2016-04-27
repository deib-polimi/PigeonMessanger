package it.polimi.deib.p2pchat.discovery;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.polimi.deib.p2pchat.discovery.model.P2pDestinationDevice;
import it.polimi.deib.p2pchat.discovery.utilities.UseOnlyPrivateHere;
/*
 * Copyright (C) 2015-2016 Stefano Cappa
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

/**
 * Class that represent the tab's list associated to {@link it.polimi.deib.p2pchat.discovery.chatmessages.WiFiChatFragment}
 * <p></p>
 * Attention: every device in this list is the "destination device" associated with the tabFragments.
 * This means that every message typed in the associated fragment goes to this "destination device".
 * <p></p>
 * This class contains a logic to "remap" the tab index to the List of
 * {@link it.polimi.deib.p2pchat.discovery.model.P2pDestinationDevice} index.
 * Because the first tab is reserved to {@link it.polimi.deib.p2pchat.discovery.services.WiFiP2pServicesFragment}.
 * <p></p>
 * Created by Stefano Cappa on 12/02/15.
 */
public class DestinationDeviceTabList {

    //ATTENTION DO NOT EXPOSE THIS ATTRIBUTE, BUT CREATE A SECURE METHOD TO MANAGE THIS LISTS!!!
    //SEE THE ANNOTATION, USE ONLY PRIVATE HERE WITHOUT GETTERS OR SETTERS!!!
    @UseOnlyPrivateHere private final List<P2pDestinationDevice> deviceList;

    private static final DestinationDeviceTabList instance = new DestinationDeviceTabList();

    /**
     * Method to get the instance of this class.
     * @return instance of this class.
     */
    public static DestinationDeviceTabList getInstance() {
        return instance;
    }

    /**
     * Private constructor, because is a singleton class.
     */
    private DestinationDeviceTabList() {
        deviceList = new ArrayList<>();
    }


    /**
     * Method to get a WifiP2pDevice
     * specifying the position of the element in the list.
     * If the condition {@code pos>=0 && pos <= deviceList.size() - 1 } is false, return null.
     * @param pos integer position
     * @return WifiP2pDevice element at position pos, inside the deviceList or null.
     */
    public WifiP2pDevice getDevice(int pos) {
        if (pos>=0 && pos <= deviceList.size() - 1) {
            return deviceList.get(pos).getP2pDevice();
        }
        return null;
    }

    /**
     * Method that sets a {@link it.polimi.deib.p2pchat.discovery.model.P2pDestinationDevice}
     * at position {@code pos} inside the {@link #deviceList}.
     * This method replaces the element at position {@code pos}, if this {@link #deviceList}
     * has an element at this position.
     * If not, the device will be added to the {@link #deviceList}, at position {@code pos}.
     * This method never fails! You have the warranty that the element will be added to the list.
     * If you pass a device==null, in the list you will set or add a null element. Be careful!
     * @param pos int position
     * @param device {@link it.polimi.deib.p2pchat.discovery.model.P2pDestinationDevice}
     *               element the you want add to the deviceList.
     */
    public void setDevice(int pos, @NonNull P2pDestinationDevice device) {
        if (pos>=0 && pos <= deviceList.size() - 1) {
            deviceList.set(pos, device);
        } else {
            deviceList.add(pos, device);
        }
    }

    /**
     * Method to add a {@link it.polimi.deib.p2pchat.discovery.model.P2pDestinationDevice}
     * in the list, only if it isn't already in.
     * If the list contains null elements, this method search the first one and it replaces with the device parameter.
     * If all the element in the list are {@code !=null}, it adds the new device ad the end.
     * <p></p>
     * This method never fails! You have the warranty that the element will be added to the list.
     * If you pass a {@code device==null}, in the list you will get a null element. Be careful!
     * @param device {@link it.polimi.deib.p2pchat.discovery.model.P2pDestinationDevice}
     * element the you want add to the deviceList.
     */
    public void addDeviceIfRequired(@NonNull P2pDestinationDevice device) {
        boolean add = true;
        for (P2pDestinationDevice element : deviceList) {
            if (element!=null && element.getP2pDevice() != null && element.getP2pDevice().equals(device.getP2pDevice())) {
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
     * Method that check if a {@link it.polimi.deib.p2pchat.discovery.model.P2pDestinationDevice}
     * is in the {@link #deviceList}.
     * If the device parameter is null, this method returns false!
     * @param device WifiP2p{@link it.polimi.deib.p2pchat.discovery.model.P2pDestinationDevice} element.
     * @return true if the element is in the list, false otherwise or if the device parameter is null.
     */
    public boolean containsElement(P2pDestinationDevice device) {
        if(device==null) {
            return false;
        }

        for (P2pDestinationDevice element : deviceList) {
            if (element != null && element.getP2pDevice()!=null && element.getP2pDevice().deviceAddress.equals(device.getP2pDevice().deviceAddress)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to get the index position of a {@link it.polimi.deib.p2pchat.discovery.model.P2pDestinationDevice}
     * from {@link #deviceList}.
     * If the device is not contained in the list or {@code device == null}, it returns -1.
     * <p></p>
     * Attention: sometimes Android gives a WifiP2pDevice without the deviceName. For this reason i check
     * the element in the list only with the deviceAddress.
     * @param device {@link it.polimi.deib.p2pchat.discovery.model.P2pDestinationDevice} element.
     * @return int that represents the index inside the List {@link #deviceList}.
     */
    public int indexOfElement(P2pDestinationDevice device) {
        if(device==null) {
            return -1;
        }

        for (int i = 0; i < deviceList.size(); i++) {
            if (deviceList.get(i) != null && deviceList.get(i).getP2pDevice()!=null &&
                    deviceList.get(i).getP2pDevice().deviceAddress.equals(device.getP2pDevice().deviceAddress)) {
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
