package com.example.aitools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<ChatMessage> chatMessages = new ArrayList<>();
    private RecyclerView chatMessageRecyclerView;
    private ChatMessageAdapter chatMessageAdapter;
    Button send;
    private EditText editTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send=findViewById(R.id.chat_send_button);
        editTextMessage=findViewById(R.id.chat_input);
        chatMessageRecyclerView = findViewById(R.id.chat_message_recycler_view);
        chatMessageAdapter = new ChatMessageAdapter(chatMessages);
        chatMessageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatMessageRecyclerView.setAdapter(chatMessageAdapter);
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = editTextMessage.getText().toString().trim();
                    if (!TextUtils.isEmpty(message)) {
                        sendMessage(message);
                    }
                }
            });

        receiveMessage("Hello, how can I help you?",chatMessageAdapter,chatMessages);


    }

    protected void sendMessage(String message) {
        // Create a new ChatMessage object with the message and current date
        ChatMessage newMessage = new ChatMessage(message, new Date(), true);

        // Add the new message to the message list and notify the adapter of the data change
        chatMessages.add(newMessage);
        chatMessageAdapter.notifyDataSetChanged();

        // Scroll the RecyclerView to the bottom to show the latest message
        chatMessageRecyclerView.scrollToPosition(chatMessages.size() - 1);

        // Send the message to the OpenAI API using curl
        String apiKey = "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"; // Replace with your OpenAI API key
        String model = "text-davinci-003";
        String prompt = "The following is a conversation with an AI assistant. The assistant is helpful, creative, clever, and very friendly.\n\nHuman: " + message + "\nAI:";
        String temperature = "0.9";
        String maxTokens = "150";
        String topP = "1";
        String frequencyPenalty = "0.0";
        String presencePenalty = "0.6";
        String[] cmd = {"curl", "-X", "POST", "https://api.openai.com/v1/completions", "-H", "Content-Type: application/json", "-H", "Authorization: Bearer " + apiKey, "-d", "{\"model\": \"" + model + "\", \"prompt\": \"" + prompt + "\", \"temperature\": " + temperature + ", \"max_tokens\": " + maxTokens + ", \"top_p\": " + topP + ", \"frequency_penalty\": " + frequencyPenalty + ", \"presence_penalty\": " + presencePenalty + ", \"stop\": [\" Human:\", \" AI:\"]}"};

        try {
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            reader.close();
            String response = output.toString();

            // Parse the response JSON and extract the generated message
            JSONObject jsonResponse = new JSONObject(response);
            String generatedMessage = jsonResponse.getJSONArray("choices").getJSONObject(0).getString("text");

            // Add the generated message to the message list and notify the adapter of the data change
            ChatMessage generatedChatMessage = new ChatMessage(generatedMessage, new Date(), false);
            chatMessages.add(generatedChatMessage);
            chatMessageAdapter.notifyDataSetChanged();

            // Scroll the RecyclerView to the bottom to show the latest message
            chatMessageRecyclerView.scrollToPosition(chatMessages.size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Handle receiving a message from ChatGPT
    private void receiveMessage(String message, ChatMessageAdapter messageAdapter, List<ChatMessage> messageList) {
        // Create a new ChatMessage object with the message and current date
        ChatMessage newMessage = new ChatMessage(message, new Date(), false);

        // Add the new message to the message list and notify the adapter of the data change
        messageList.add(newMessage);
        messageAdapter.notifyDataSetChanged();

        // Scroll the RecyclerView to the bottom to show the latest message
        chatMessageRecyclerView.scrollToPosition(messageList.size() - 1);
    }





}