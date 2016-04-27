package it.polimi.deib.p2pchat.discovery.model;
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
import android.net.wifi.p2p.WifiP2pDevice;

import lombok.Getter;
import lombok.Setter;

/**
 * Class that represents the {@code WifiP2pDevice} associated to a
 * {@link it.polimi.deib.p2pchat.discovery.chatmessages.WiFiChatFragment} and the ip address attribute.
 * This class is useful because can be used to extends the basic {@code WifiP2pDevice}'s
 * functionalities to include the ip address, that is not available in the original Android's API.
 * It's an abstraction of a {@code WifiP2pDevice}.
 * <p></p>
 * Created by Stefano Cappa on 01/03/15.
 */
public class P2pDestinationDevice {

    @Getter private WifiP2pDevice p2pDevice;
    @Getter @Setter private String destinationIpAddress; //it's the ip address


    /**
     * Constructor of the class
     * @param p2pDevice A {@code WifiP2pDevice}
     */
    public P2pDestinationDevice(WifiP2pDevice p2pDevice) {
        this.p2pDevice = p2pDevice;
    }

    /**
     * Another constructor of the class without parameters.
     */
    public P2pDestinationDevice() {}

    @Override
    public String toString() {
        return this.p2pDevice.deviceName + ", " + this.p2pDevice.deviceAddress + ", " + this.p2pDevice.status;
    }
}
