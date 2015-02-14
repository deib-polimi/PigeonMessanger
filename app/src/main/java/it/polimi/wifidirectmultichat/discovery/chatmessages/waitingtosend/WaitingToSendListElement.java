package it.polimi.wifidirectmultichat.discovery.chatmessages.waitingtosend;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Created by Stefano Cappa on 14/02/15.
 */
public class WaitingToSendListElement {

    @Getter private List<String> waitingToSendList;

    public WaitingToSendListElement () {
        waitingToSendList = new ArrayList<>();
    }
}
