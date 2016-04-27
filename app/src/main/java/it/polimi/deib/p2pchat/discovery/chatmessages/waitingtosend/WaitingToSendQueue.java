package it.polimi.deib.p2pchat.discovery.chatmessages.waitingtosend;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents an ArrayList of ArrayLists.
 * Contains the waitingToSend list (ArrayList of
 * {@link it.polimi.deib.p2pchat.discovery.chatmessages.waitingtosend.WaitingToSendListElement}'s ArrayLists) and
 * the logic to obtain elements.
 * <p></p>
 * Created by Stefano Cappa on 04/02/15.
 */
public class WaitingToSendQueue {

    private final List<WaitingToSendListElement> waitingToSend;

    private static final WaitingToSendQueue instance = new WaitingToSendQueue();

    /**
     * Method to get the instance of this class.
     * @return instance of this class.
     */
    public static WaitingToSendQueue getInstance() {
        return instance;
    }

    /**
     * Private constructor, because is a singleton class.
     */
    private WaitingToSendQueue() {
        waitingToSend = new ArrayList<>();
    }


    /**
     * Method to get an element from the list using the tab number.
     * Contains the logic to retrieve the correct
     * {@link it.polimi.deib.p2pchat.discovery.chatmessages.waitingtosend.WaitingToSendListElement} ArrayList.
     * This method returns always an element, because if the element isn't in the list, this method adds
     * a new {@link it.polimi.deib.p2pchat.discovery.chatmessages.waitingtosend.WaitingToSendListElement}
     * at the specified tabNumber-1 position.
     * {@link it.polimi.deib.p2pchat.discovery.chatmessages.waitingtosend.WaitingToSendListElement}.
     * @param tabNumber int that represents the tabNumber used to retrieve the ArrayList.
     * @return List element of the waitingToSend list.
     */
    public List<String> getWaitingToSendItemsList(int tabNumber) {

        //to remap the tabNumber index to the waitingToSend list's index, this method
        //uses only "(tabNumber - 1)".

        //if tabNumber index in between 0 and size-1
        if ((tabNumber - 1) >= 0 && (tabNumber - 1) <= waitingToSend.size() - 1) {

            //if this element is null i set the WaitingToSendListElement() at tabNumber-1
            if(waitingToSend.get((tabNumber - 1)) == null) {
                waitingToSend.set((tabNumber - 1), new WaitingToSendListElement());
            }

            //if is !=null, do nothing, because i have the list ready and probably with elements
            //and i can't lost this elements
        } else {

            //if the tabNumber index is not available, i add a new WaitingToSendListElement ad the end of the waitingToSend List.
            waitingToSend.add((tabNumber - 1), new WaitingToSendListElement());
        }

        return waitingToSend.get((tabNumber - 1)).getWaitingToSendList();
    }
}
