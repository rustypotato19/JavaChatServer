package com.rusty.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TerminalListener implements Runnable {
    private Server server;

    public TerminalListener(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input;
            while ((input = reader.readLine()) != null) {
                if ("/shutdown".equalsIgnoreCase(input.trim())) {
                    server.broadcastMessage("[SERVER]: Server shutting down in 5 seconds", null);
                    Thread.sleep(1000);
                    for (int i = 4; i > 0; i--) {
                        server.broadcastMessage("[SERVER]: " + i + "...", null);
                        Thread.sleep(1000);
                    }
                    // close the server socket
                    shutdown();
                } else if (input.trim().startsWith("/")) {
                    System.out.println("Unknown command: " + input);
                } else {
                    server.broadcastMessage("[SERVER]: " + input, null);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void shutdown() {
        //System.out.println("Shutting down the server...");
        try {
            if (server != null && server.serverSocket != null && !server.serverSocket.isClosed()) {
                server.serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
