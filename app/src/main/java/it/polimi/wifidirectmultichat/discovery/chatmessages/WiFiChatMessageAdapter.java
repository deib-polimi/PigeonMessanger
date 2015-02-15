package it.polimi.wifidirectmultichat.discovery.chatmessages;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.polimi.wifidirectmultichat.R;

/**
 * Created by Stefano Cappa on 10/02/15.
 */
public class WiFiChatMessageAdapter extends RecyclerView.Adapter<WiFiChatMessageAdapter.ViewHolder> {

    private WiFiChatFragment chatFragment;

    public WiFiChatMessageAdapter(WiFiChatFragment chatFragment) {
        this.chatFragment = chatFragment;
        setHasStableIds(true);
    }


    /**
     * Classe statica viewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final ViewGroup viewGroup;
        private ImageView image;
        private TextView message;

        private WiFiChatFragment chatFragment;

        public ViewHolder(View view, WiFiChatFragment chatFragment, ViewGroup viewGroup) {
            super(view);

            this.view = view;
            this.viewGroup = viewGroup;

            this.chatFragment = chatFragment;

            message = (TextView) view.findViewById(R.id.message);
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.chatmessage_row, viewGroup, false);

        return new ViewHolder(v, chatFragment, viewGroup);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        String message = chatFragment.getItems().get(position);
        viewHolder.message.setText(message);

        if (message != null && !message.isEmpty()) {
            viewHolder.message.setTextAppearance(chatFragment.getActivity(), R.style.normalText);
            if (chatFragment.isGrayScale()) {
                viewHolder.message.setTextColor(chatFragment.getResources().getColor(R.color.gray));
            } else {
                if (message.startsWith("Me: ")) {
                    viewHolder.message.setTextAppearance(chatFragment.getActivity(),
                            R.style.normalText);
                } else {
                    viewHolder.message.setTextAppearance(chatFragment.getActivity(),
                            R.style.boldText);
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return chatFragment.getItems().size();
    }
}