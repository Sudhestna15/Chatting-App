package com.example.chattingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.chattingapp.model.UserModel;
import com.example.chattingapp.utils.AndroidUtil;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;

    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser= AndroidUtil.getUserModelFromIntent(getIntent());
        messageInput= (EditText) findViewById(R.id.chat_message_input);
        sendMessageBtn= (ImageButton) findViewById(R.id.message_send_btn);
        backBtn= (ImageButton) findViewById(R.id.back_btn);
        otherUsername= (TextView) findViewById(R.id.other_username);
        recyclerView= (RecyclerView) findViewById(R.id.chat_recycler_view);
        backBtn.setOnClickListener(view -> {
            onBackPressed();
        });
        otherUsername.setText(otherUser.getUsername());

    }

}