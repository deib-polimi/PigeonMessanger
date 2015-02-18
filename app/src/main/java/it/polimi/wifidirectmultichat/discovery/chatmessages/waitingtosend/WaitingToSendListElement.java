package it.polimi.wifidirectmultichat.discovery.chatmessages.waitingtosend;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Class that represents an ArrayList's element of the WaitingToSendQueue list.
 * No visibility modifier, this implies that you can access to this class only in this package.
 *
 * Created by Stefano Cappa on 14/02/15.
 */
class WaitingToSendListElement {

    @Getter private final List<String> waitingToSendList;

    /**
     * Constructor of the class.
     */
    public WaitingToSendListElement () {
        waitingToSendList = new ArrayList<>();
    }
}
