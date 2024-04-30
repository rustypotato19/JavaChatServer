package com.rusty.server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;
    private Server server;

    public ClientHandler(Socket clientSocket, Server server) {
        try {
            this.clientSocket = clientSocket;
            this.server = server;
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            username = reader.readLine();
            if (usernameExists(username)) {
                username = generateUniqueUsername(username);
                writer.println(username);
            } else {
                writer.println(username);
            }
            server.broadcastMessage(username + " joined the chat", this);
            String message;
            while ((message = reader.readLine()) != null) {
                if ("/exit".equals(message)) {
                    server.removeClient(this);
                    break;
                }
                server.broadcastMessage(username + ": " + message, this);
            }
            server.broadcastMessage(username + " left the chat", this);
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public void close() {
        try {
            reader.close();
            writer.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean usernameExists(String username) {
        for (ClientHandler client : server.getClients()) {
            if (client != this && client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private String generateUniqueUsername(String username) {
        int random = (int) (Math.random() * 1000);
        String newUsername = username + random;
        while (usernameExists(newUsername)) {
            random = (int) (Math.random() * 1000);
            newUsername = username + random;
        }
        return newUsername;
    }

    public String getUsername() {
        return username;
    }
}
