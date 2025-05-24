package com.example.hd;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hd.adapter.ChatAdapter;
import com.example.hd.backend.BackendService;
import com.example.hd.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EditText editUserMessage;
    private Button btnSend, btnBack;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private BackendService backendService;

    private final String TYPING_INDICATOR = "AI is typing...";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editUserMessage = findViewById(R.id.editUserMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);

        backendService = new BackendService();
        chatMessages = new ArrayList<>();

        chatAdapter = new ChatAdapter(chatMessages, this);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(chatAdapter);

        //return home
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        // sent user message
        btnSend.setOnClickListener(v -> {
            String userMessage = editUserMessage.getText().toString().trim();
            if (userMessage.isEmpty()) {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add User Message
            chatMessages.add(new ChatMessage(userMessage, false));
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            scrollToBottom();

            editUserMessage.setText("");

            // Add a temporary placeholder
            chatMessages.add(new ChatMessage(TYPING_INDICATOR, true));
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            scrollToBottom();

            // Send Request
            backendService.requestAsk(userMessage, this, reply -> {
                // Replace the "Generating..." message
                int index = chatMessages.size() - 1;
                if (chatMessages.get(index).getMessage().equals(TYPING_INDICATOR)) {
                    chatMessages.set(index, new ChatMessage(reply, true));
                    chatAdapter.notifyItemChanged(index);
                } else {
                    chatMessages.add(new ChatMessage(reply, true));
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                }
                scrollToBottom();
            });
        });
    }

    private void scrollToBottom() {
        new Handler().post(() -> recyclerViewChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1));
    }
}
