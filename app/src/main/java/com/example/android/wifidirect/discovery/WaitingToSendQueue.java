package com.example.android.wifidirect.discovery;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;


/**
 * Created by Stefano Cappa on 04/02/15.
 */
public class WaitingToSendQueue {

    private List<String> waitingToSendItemsTab1;
    private List<String> waitingToSendItemsTab2;

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
        if(number==1) {
            return waitingToSendItemsTab1;
        } else {
            return waitingToSendItemsTab2;
        }
    }

    private WaitingToSendQueue() {
        waitingToSendItemsTab1 = new ArrayList<>();
        waitingToSendItemsTab2 = new ArrayList<>();
    }

}
