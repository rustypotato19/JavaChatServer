package com.rusty.server;

public class TimeStamp {

    public String currentTime() {
        // Get the current time
        long currentTime = System.currentTimeMillis();

        // Convert the current time to HH:MM format
        String time = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(currentTime));

        // Display the message in the chat area with "You: " prefix and HH:MM timestamp
        //new TimeStamp().currentTime();
        //appendMessage("You: " + message);
        return "[" + time + "]";
    }

}
