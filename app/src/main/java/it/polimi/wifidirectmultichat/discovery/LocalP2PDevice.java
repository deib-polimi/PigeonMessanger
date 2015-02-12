package it.polimi.wifidirectmultichat.discovery;

import android.net.wifi.p2p.WifiP2pDevice;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * Created by Stefano Cappa on 11/02/15.
 *
*/
public class LocalP2PDevice {

    private static LocalP2PDevice instance = new LocalP2PDevice();

    @Getter @Setter private WifiP2pDevice localDevice;

    /**
     * Metodo che permette di ottenere l'istanza della classe.
     * @return istanza della classe.
     */
    public static LocalP2PDevice getInstance() {
        return instance;
    }

    private LocalP2PDevice(){
        localDevice = new WifiP2pDevice();
    }

}
