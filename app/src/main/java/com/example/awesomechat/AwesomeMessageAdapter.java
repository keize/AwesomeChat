package com.example.awesomechat;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.List;


public class AwesomeMessageAdapter extends ArrayAdapter<AwesomeMessage> {

    private List<AwesomeMessage> messages;
    private Activity activity;

    public AwesomeMessageAdapter(Activity context, int resource,
                                 List<AwesomeMessage> messages) {
        super(context, resource, messages);

        this.messages = messages;
        this.activity = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        LayoutInflater layoutInflater =
                (LayoutInflater)activity.getSystemService(
                        Activity.LAYOUT_INFLATER_SERVICE);

        AwesomeMessage awesomeMessage = getItem(position);
        int layoutResource = 0;
        int viewType = getItemViewType(position);

        if (viewType == 0) {
            layoutResource = R.layout.my_message_item;
        } else {
            layoutResource = R.layout.your_messege_item;
        }

        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = layoutInflater.inflate(
                    layoutResource, parent, false
            );
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        boolean isText = awesomeMessage.getImageUrl() == null;

        if (isText) {
            viewHolder.messegeTextView.setVisibility(View.VISIBLE);
            viewHolder.photoImageView.setVisibility(View.GONE);
            viewHolder.messegeTextView.setText(awesomeMessage.getText());
            viewHolder.dataTextView.setText(awesomeMessage.getMessageDate());
        } else {
            viewHolder.photoImageView.setVisibility(View.VISIBLE);
            viewHolder.messegeTextView.setVisibility(View.GONE);
            viewHolder.messegeTextView.setText(awesomeMessage.getText());
            viewHolder.dataTextView.setText(awesomeMessage.getMessageDate());
            Glide.with(viewHolder.photoImageView.getContext())
                    .load(awesomeMessage.getImageUrl())
                    .into(viewHolder.photoImageView);
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {

        int flag;
        AwesomeMessage awesomeMessage = messages.get(position);
        if (awesomeMessage.isMine()) {
            flag = 0;
        } else {
            flag = 1;
        }

        return flag;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private class ViewHolder {

        private ImageView photoImageView;
        private TextView messegeTextView;
        private TextView dataTextView;

        public ViewHolder(View view) {
            photoImageView = view.findViewById(R.id.photoImageView);
            messegeTextView = view.findViewById(R.id.messegeTextView);
            dataTextView = view.findViewById(R.id.dataTextView);

        }

    }
}
