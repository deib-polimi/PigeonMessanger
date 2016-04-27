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

import lombok.Getter;

/**
 * Class that represents an ArrayList's element of the WaitingToSendQueue list.
 * No visibility modifier, this implies that you can access to this class only in this package.
 * <p></p>
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
