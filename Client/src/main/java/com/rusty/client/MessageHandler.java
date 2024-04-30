package com.rusty.client;

public interface MessageHandler {
    void onMessageReceived(String message);
    void onConnectionError(String errorMessage);
}
