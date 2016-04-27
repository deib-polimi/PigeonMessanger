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
 * Class that represents the device where this app is running.
 * <p></p>
 * Created by Stefano Cappa on 11/02/15.
*/
public class LocalP2PDevice {

    //This is the localdevice!!!
    @Getter @Setter private WifiP2pDevice localDevice;

    private static final LocalP2PDevice instance = new LocalP2PDevice();

    /**
     * Method to get the instance of this class.
     * @return instance of this class.
     */
    public static LocalP2PDevice getInstance() {
        return instance;
    }

    /**
     * Private constructor, because is a singleton class.
     */
    private LocalP2PDevice(){
        localDevice = new WifiP2pDevice();
    }

}
