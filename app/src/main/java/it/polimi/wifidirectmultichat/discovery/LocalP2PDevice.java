package it.polimi.wifidirectmultichat.discovery;

import android.net.wifi.p2p.WifiP2pDevice;

import lombok.Getter;
import lombok.Setter;

/**
 * Class that represents the device where this app is running.
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
