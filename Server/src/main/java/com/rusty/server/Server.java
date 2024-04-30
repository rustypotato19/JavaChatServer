package com.rusty.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int DEFAULT_PORT = 1;
    private static final String CHAT_LOG_FILE = "chat.log";

    List<ClientHandler> clients = new ArrayList<>();
    public ServerSocket serverSocket;

    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        try {
            int port = DEFAULT_PORT;
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            new Thread(new TerminalListener(this)).start();
            new Thread(new ChatLogListener()).start();
            acceptClients();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptClients() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message, ClientHandler sender) {
        System.out.println(message);
        try {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
            FileWriter fileWriter = new FileWriter(CHAT_LOG_FILE, true);
            fileWriter.write(new TimeStamp().currentTime() + " " + message + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClient(ClientHandler client) {
        System.out.println("test");
        clients.remove(client);
        System.out.println(client);
    }

    public List<ClientHandler> getClients() {
        return clients;
    }
}
