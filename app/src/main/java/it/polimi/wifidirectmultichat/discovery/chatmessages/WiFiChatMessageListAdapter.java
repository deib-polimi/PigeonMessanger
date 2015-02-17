package it.polimi.wifidirectmultichat.discovery.chatmessages;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.polimi.wifidirectmultichat.R;

/**
 * Created by Stefano Cappa on 10/02/15.
 */
public class WiFiChatMessageListAdapter extends ArrayAdapter<String> {

    private WiFiChatFragment chatFragment;

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

//    // Create new views (invoked by the layout manager)
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//        // Create a new view.
//        View v = LayoutInflater.from(viewGroup.getContext())
//                .inflate(R.layout.chatmessage_row, viewGroup, false);
//
//        return new ViewHolder(v, chatFragment, viewGroup);
//    }
//
//
//    // Replace the contents of a view (invoked by the layout manager)
//    @Override
//    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
//
//        String message = chatFragment.getItems().get(position);
//        viewHolder.message.setText(message);
//
//        if (message != null && !message.isEmpty()) {
//            viewHolder.message.setTextAppearance(chatFragment.getActivity(), R.style.normalText);
//            if (chatFragment.isGrayScale()) {
//                viewHolder.message.setTextColor(chatFragment.getResources().getColor(R.color.gray));
//            } else {
//                if (message.startsWith("Me: ")) {
//                    viewHolder.message.setTextAppearance(chatFragment.getActivity(),
//                            R.style.normalText);
//                } else {
//                    viewHolder.message.setTextAppearance(chatFragment.getActivity(),
//                            R.style.boldText);
//                }
//            }
//        }
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return chatFragment.getItems().size();
//    }
}