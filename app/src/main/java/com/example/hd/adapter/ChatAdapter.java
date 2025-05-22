package com.example.hd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hd.R;
import com.example.hd.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_USER = 0;
    private final int VIEW_TYPE_BOT = 1;

    private List<ChatMessage> messageList;
    private Context context;

    public ChatAdapter(List<ChatMessage> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).isFromBot() ? VIEW_TYPE_BOT : VIEW_TYPE_USER;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_BOT) {
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_bot, parent, false);
            return new BotViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_user, parent, false);
            return new UserViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        if (holder instanceof BotViewHolder) {
            ((BotViewHolder) holder).textMessage.setText(message.getMessage());
        } else {
            ((UserViewHolder) holder).textMessage.setText(message.getMessage());
        }
    }

    // User ViewHolder
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
        }
    }

    // Bot ViewHolder
    public static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        ImageView imgAvatar;

        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}
