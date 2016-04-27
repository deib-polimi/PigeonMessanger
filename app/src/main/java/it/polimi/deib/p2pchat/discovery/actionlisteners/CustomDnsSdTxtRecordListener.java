package it.polimi.deib.p2pchat.discovery.actionlisteners;
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
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.Map;

import it.polimi.deib.p2pchat.discovery.Configuration;

/**
 * A custom CustomDnsSdTxtRecordListener.
 * <p></p>
 * This class is not really necessary, at the moment.
 * <p></p>
 * Created by Stefano Cappa on 28/02/15.
 */
public class CustomDnsSdTxtRecordListener implements WifiP2pManager.DnsSdTxtRecordListener {

    private static final String TAG = "DnsSdRecordListener";

    @Override
    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        Log.d(TAG, "onDnsSdTxtRecordAvail: " + srcDevice.deviceName + " is " +
                txtRecordMap.get(Configuration.TXTRECORD_PROP_AVAILABLE));
    }
}
