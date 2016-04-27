package it.polimi.deib.p2pchat.discovery.chatmessages;
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
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import it.polimi.deib.p2pchat.R;

/**
 * This class is the ArrayAdapter to represents data inside the {@link it.polimi.deib.p2pchat.discovery.chatmessages.WiFiChatFragment}.
 * No visibility modifier, this implies that you can access to this class only in this package.
 * <p></p>
 * Created by Stefano Cappa on 10/02/15.
 */
class WiFiChatMessageListAdapter extends ArrayAdapter<String> {

    private final WiFiChatFragment chatFragment;

    /**
     * Constructor of the adapter.
     * @param context Context object.
     * @param textViewResourceId TextView id
     * @param chatFragment ChatFragment used to call some methods inside the getView();
     */
    public WiFiChatMessageListAdapter(Context context, int textViewResourceId,
                                      WiFiChatFragment chatFragment) {
        super(context,textViewResourceId,chatFragment.getItems());
        this.chatFragment = chatFragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v==null) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatmessage_row, parent, false);
        }

        String message = chatFragment.getItems().get(position);
        if (message != null && !message.isEmpty()) {
            TextView nameText = (TextView) v
                    .findViewById(R.id.message);
            if (nameText != null) {
                nameText.setText(message);
                nameText.setTextAppearance(chatFragment.getActivity(),R.style.normalText);
                if(chatFragment.isGrayScale()) {
                    nameText.setTextColor(chatFragment.getResources().getColor(R.color.gray));
                } else {
                    if (message.startsWith("Me: ")) {
                        nameText.setTextAppearance(chatFragment.getActivity(),
                                R.style.normalText);
                    } else {
                        nameText.setTextAppearance(chatFragment.getActivity(),
                                R.style.boldText);
                    }
                }
            }
        }
        return v;
    }
}