package com.rusty.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGUI extends JFrame implements MessageHandler {
    private JTextField userInputField;
    private JTextArea chatArea;
    private Client client;

    public ClientGUI() {
        // Set the title of the frame
        super("Chat Client");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Connect to server
        connectToServer();
    }

    private void connectToServer() {
        try {
            // Get server address and port from user input
            JPanel panel = new JPanel(new GridLayout(3, 2));

            // Add labels and text fields for address
            panel.add(new JLabel("Server Address:"));
            JTextField addressField = new JTextField();
            addressField.setMargin(new Insets(0, 5, 0, 0)); // Add left padding
            panel.add(addressField);

            // Add labels and text fields for port
            panel.add(new JLabel("Server Port:"));
            JTextField portField = new JTextField();
            portField.setMargin(new Insets(0, 5, 0, 0)); // Add left padding
            panel.add(portField);

            addressField.setText("localhost");
            portField.setText("1");

            // Show the dialog box to the user
            int result = JOptionPane.showConfirmDialog(this, panel, "Enter Server Details", JOptionPane.OK_CANCEL_OPTION);

            // If the user clicked OK, proceed with connection
            if (result == JOptionPane.OK_OPTION) {
                String serverAddress = addressField.getText().trim();
                String portStr = portField.getText().trim();

                // Check if the server address is empty, if it is reprompt in correct format
                while (serverAddress.isEmpty()) {
                    serverAddress = JOptionPane.showInputDialog("Server address cannot be empty. \nEnter the server address:");
                    portStr = JOptionPane.showInputDialog("Server port cannot be empty. \nEnter the server port:");
                }

                // Parse port number
                int serverPort = 0;
                try {
                    serverPort = Integer.parseInt(portStr);
                } catch (NumberFormatException ex) {
                    // Show error message if the port number is invalid
                    JOptionPane.showMessageDialog(this, "Invalid server port.", "Error", JOptionPane.ERROR_MESSAGE);
                    dispose();
                    return;
                }

                // Initialize the client
                client = new Client(serverAddress, serverPort, this);

                // Prompt user for username
                String username = JOptionPane.showInputDialog("Enter your username:");

                // check username isn't empty, if it is reprompt
                while (username == null || username.isEmpty()) {
                    username = JOptionPane.showInputDialog("Username cannot be empty. Enter your username:");
                }

                
                // Set the username
                client.setUsername(username);
                
                // Show the chat window after successful connection
                initializeChatWindow();

            } else {
                // If the user clicked Cancel or closed the dialog, dispose the application
                dispose();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeChatWindow() {
        // Create and set up the GUI components
        userInputField = new JTextField();
        userInputField.addActionListener(new ActionListener() {
            @Override // Send the message when the user presses Enter key
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Create "Send" button
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override // Send the message when the button is clicked
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Input field and Send button
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(userInputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // Chat area 
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Set the location of the frame to the center of the screen
        setLocationRelativeTo(null);

        // Add welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + client.getUsername());
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the text
        welcomeLabel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK)); // Add top border
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(16f).deriveFont(Font.BOLD)); // Increase font size and make it bold
        add(welcomeLabel, BorderLayout.NORTH);

        // Show the frame
        setVisible(true);

        // Disable closing the window using the close button
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    private void sendMessage() {
        String message = userInputField.getText().trim(); // Trim to remove leading and trailing whitespaces

        // Check if the message is empty or null
        if (message.isEmpty()) {
            // Alert the user that the message cannot be empty
            JOptionPane.showMessageDialog(this, "Message cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for special commands
        switch (message) {
            case "/exit":
                // Clear the input field
                userInputField.setText("");
                // Close the socket and dispose the frame
                client.close();
                //dispose(); // Close the chat window
                //System.exit(0); // Exit the application
                break;
            case "/clear":
                // Clear the input field
                userInputField.setText("");
                // Clear the chat area
                chatArea.setText("");
                break;
            case "/help":
                // Clear the input field
                userInputField.setText("");
                // Show help message
                appendMessage("Special commands:");
                appendMessage("/exit - Close the chat window");
                appendMessage("/clear - Clear the chat area");
                appendMessage("/help - Show this help message");
                break;
            default:
                // Send the message to the server
                client.sendMessage(message);

                // Clear the input field
                userInputField.setText("");

                // Display the message in the chat area with "You: " prefix and HH:MM timestamp
                appendMessage("You: " + message);
                break;
        }
    }

    private void appendMessage(String message) {
        chatArea.append(message + "\n");

        // Scroll to the bottom of the chat area
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    @Override
    public void onMessageReceived(String message) {
        // Append the received message to the chat area
        System.out.println("here");
        appendMessage(message);
    }

    @Override
    public void onConnectionError(String errorMessage) {
        // Display an error message to the user
        JOptionPane.showMessageDialog(this, errorMessage, "Connection Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }

    // Add a method to close the client when the window is closed
    @Override
    public void dispose() {
        if (client != null) {
            client.close();
        }
        super.dispose();
    }

    // Method to dispose window for other classes
    public void closeWindow() {
        dispose();
    }

}
