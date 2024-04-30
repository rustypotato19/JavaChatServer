package com.rusty.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    private String username;
    private MessageHandler messageHandler;

    public Client(String serverAddress, int port, MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        try {
            // Connect to the server
            socket = new Socket(serverAddress, port);

            // Set up input and output streams
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // Start a thread to handle incoming messages
            new Thread(this::listenForMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
            messageHandler.onConnectionError("Error connecting to the server.");
        }
    }

    private void listenForMessages() {
        try {
            int count = 0;
            String message;
            while ((message = reader.readLine()) != null) {
                if (count == 0) {
                    count = 1;
                    this.username = message;
                } else {
                messageHandler.onMessageReceived(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            messageHandler.onConnectionError("Connection lost with the server.");
            // Close the client
            close();
            // Close window
            System.exit(0);
        }
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
        writer.println(username);
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

