package it.polimi.deib.p2pchat.discovery.chatmessages.messagefilter;
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
import java.util.Locale;

import lombok.Getter;

/**
 * This is an extremely simple filter for blacklisted words in messages.
 * To use this filter in a real world, you need to implement more feature.
 * <p/>
 * Created by Stefano Cappa on 01/03/15.
 */
public class MessageFilter {

    //this list contains all the messages in lowercase to filter messages
    @Getter
    private List<String> lowerCaseBlackList;

    private static final MessageFilter instance = new MessageFilter();

    /**
     * Method to get the instance of this class.
     *
     * @return instance of this class.
     */
    public static MessageFilter getInstance() {
        return instance;
    }

    /**
     * Private constructor, because is a singleton class.
     */
    private MessageFilter() {

        lowerCaseBlackList = new ArrayList<>();
        //add here all the words that you want to blacklist
//        lowerCaseBlackList.add("yesfilter");
//        lowerCaseBlackList.add("filter");
//        lowerCaseBlackList.add("test");
    }

    /**
     * Method to know if the message must be filtered.
     *
     * @param message String message to check
     * @return boolean: true if the message is not valid because is filtered, false otherwise.
     * @throws MessageException Exception throwed if the message is null or if it's too short or
     *                          if it's blacklisted.
     */
    public boolean isFiltered(String message) throws MessageException {
        if (message == null) {
            throw new MessageException(MessageException.Reason.NULLMESSAGE);
        }

        if (message.length() <= 1) {
            throw new MessageException(MessageException.Reason.MESSAGETOOSHORT);
        }

        String[] chunckList = message.toLowerCase(Locale.US).split(" ");

        for (int i = 0; i < chunckList.length; i++) {
            if (lowerCaseBlackList.contains(chunckList[i])) {
                throw new MessageException(MessageException.Reason.MESSAGEBLACKLISTED);
            }
        }
        return false;
    }

}
