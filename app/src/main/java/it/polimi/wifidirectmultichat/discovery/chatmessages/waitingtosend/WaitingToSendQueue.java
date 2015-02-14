package it.polimi.wifidirectmultichat.discovery.chatmessages.waitingtosend;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;


/**
 * Created by Stefano Cappa on 04/02/15.
 */
public class WaitingToSendQueue {

    private List<WaitingToSendListElement> waitingToSend;

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

        if (number - 1 >= 0 && number - 1 <= waitingToSend.size() - 1) {
            waitingToSend.set(number - 1, new WaitingToSendListElement());
        } else {
            waitingToSend.add(number - 1, new WaitingToSendListElement());
        }

//        if(waitingToSend.size() < number) {
//            waitingToSend.add(number, new WaitingToSendListElement());
//        }
        return waitingToSend.get(number - 1).getWaitingToSendList();
    }

    private WaitingToSendQueue() {
        waitingToSend = new ArrayList<>();
    }

}
