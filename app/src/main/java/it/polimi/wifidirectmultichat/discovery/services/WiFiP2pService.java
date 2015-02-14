
package it.polimi.wifidirectmultichat.discovery.services;

import android.net.wifi.p2p.WifiP2pDevice;

import lombok.Getter;
import lombok.Setter;

/**
 * A structure to hold service information.
 */
public class WiFiP2pService {
    @Getter @Setter private WifiP2pDevice device;
    @Getter @Setter private String instanceName = null;
    @Getter @Setter private String serviceRegistrationType = null;
}
