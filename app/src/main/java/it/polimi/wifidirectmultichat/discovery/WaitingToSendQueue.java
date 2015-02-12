package it.polimi.wifidirectmultichat.discovery;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Stefano Cappa on 04/02/15.
 */
public class WaitingToSendQueue {

    private List<String> waitingToSendItemsTab1;
    private List<String> waitingToSendItemsTab2;
    private List<String> waitingToSendItemsTab3;
    private List<String> waitingToSendItemsTab4;
    private List<String> waitingToSendItemsTab5;

    private static WaitingToSendQueue instance = new WaitingToSendQueue();

    /**
     * Metodo che permette di ottenere l'istanza della classe.
     *
     * @return istanza della classe.
     */
    public static WaitingToSendQueue getInstance() {
        return instance;
    }

    public List<String> waitingToSendItemsList (int number) {
        switch(number) {
            case 2:
                return waitingToSendItemsTab2;
            case 3:
                return waitingToSendItemsTab3;
            case 4:
                return waitingToSendItemsTab4;
            case 5:
                return waitingToSendItemsTab5;
            default:  //1 e negli altri casi, per sicurezza da sempre la 1
                return waitingToSendItemsTab1;
        }
//        if(number==1) {
//            return waitingToSendItemsTab1;
//        } else {
//            return waitingToSendItemsTab2;
//        }
    }

    private WaitingToSendQueue() {
        waitingToSendItemsTab1 = new ArrayList<>();
        waitingToSendItemsTab2 = new ArrayList<>();
        waitingToSendItemsTab3 = new ArrayList<>();
        waitingToSendItemsTab4 = new ArrayList<>();
        waitingToSendItemsTab5 = new ArrayList<>();
    }

}
