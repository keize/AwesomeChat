package com.example.awesomechat;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;




public class AwesomeMessageAdapter
        extends RecyclerView.Adapter<AwesomeMessageAdapter.AwesomeMessageViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private ArrayList<AwesomeMessage> awesomeMessages;

    public AwesomeMessageAdapter(ArrayList<AwesomeMessage> messages) {
        awesomeMessages = messages;
    }

    @Override
    public AwesomeMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_message_item, parent, false);
            return new AwesomeMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.your_messege_item, parent, false);
            return new AwesomeMessageViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AwesomeMessageAdapter.AwesomeMessageViewHolder holder, int position) {
        AwesomeMessage message = (AwesomeMessage) awesomeMessages.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((AwesomeMessageViewHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((AwesomeMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return awesomeMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        AwesomeMessage message = (AwesomeMessage) awesomeMessages.get(position);

        if (message.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    public static class AwesomeMessageViewHolder extends RecyclerView.ViewHolder {
        public ImageView photoImageView;
        public TextView textTextView;
        public TextView dataTextView;


        public AwesomeMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            photoImageView = itemView.findViewById(R.id.photoImageView);
            textTextView = itemView.findViewById(R.id.messegeTextView);
            dataTextView = itemView.findViewById(R.id.dataTextView);
        }
        void bind(AwesomeMessage message) {
            textTextView.setText(message.getText());
            dataTextView.setText(message.getMessageDate());
        }
    }
}
