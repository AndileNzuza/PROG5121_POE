/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */package com.mycompany.poepart2;

import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

public class POEPart2 {

    // Declaration
    private String messageID;
    private int numMessagesSent;
    private String recipient;
    private String messageDescription;
    private String messageHash;

    private static int totalMessagesAcrossApp = 0;

    // Constructor
    public POEPart2(String messageID, int numMessagesSent,
                    String recipient, String messageDescription) {

        this.messageID = messageID;
        this.numMessagesSent = numMessagesSent;
        this.recipient = recipient;
        this.messageDescription = messageDescription;

        // Generate message hash
        this.messageHash = createMessageHash();

        // Increase total messages
        totalMessagesAcrossApp++;
    }

    // Ensures message ID is not more than 10 characters
    public boolean checkMessageID() {

        return this.messageID != null &&
               this.messageID.length() <= 10;
    }

    // Recipient cell number validation
    public String checkRecipientCell() {

        if (this.recipient != null &&
            this.recipient.startsWith("+") &&
            this.recipient.length() <= 10) {

            return "Cellphone number successfullycaptured.";
        }
        else {

            return "Cellphone number is incorrectly formatted ordoes notcontainan internationalcode.Please correct the number and try again.";
        }
    }
    
    //Message character length validation
    public String checkMessageLength() {
        
        if (this.messageDescription== null) {
            return "Message description is empty.";
        }
        
        if (this.messageDescription.length() <= 250) {
            return "Message ready to send.";
        }else {
            int overflow = this.messageDescription.length() - 250;
            return "Messsage exceeds 250 character by " + overflow + "; please reduce the size.";
        }
    }

    // Auto-generate Message Hash
    // Format: FIRST2DIGITS:LENGTH:FIRSTWORDLASTWORD
    public final String createMessageHash() {

        if (this.messageID == null ||
            this.messageDescription == null ||
            this.messageDescription.isEmpty()) {

            return "00:0:EMPTY";
        }

        // First two characters of message ID
        String firstTwo;

        if (this.messageID.length() >= 2) {
            firstTwo = this.messageID.substring(0, 2);
        }
        else {
            firstTwo = this.messageID;
        }

        // Message length
        int msgLength = this.messageDescription.length();

        // Split words
        String[] words = this.messageDescription.trim().split("\\s+");

        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        // Combine first and last word in uppercase
        String combinedWords =
                (firstWord + lastWord).toUpperCase();

        return firstTwo + ":" + msgLength + ":" + combinedWords;
    }

    // Returns output message depending on user choice
    public String sentMessage(int choice) {

        switch (choice) {

            case 0:
                return "Message successfully sent";

            case 1:
                storeMessage();
                return "Message successfully stored";

            case 2:
                return "Message discarded";

            default:
                return "Action Aborted";
        }
    }

    // Display message details
    public String printMessages() {

        return "Message ID: " + this.messageID + "\n" +
               "Message Hash: " + this.messageHash + "\n" +
               "Recipient: " + this.recipient + "\n" +
               "Message: " + this.messageDescription;
    }

    // Return total number of messages
    public int returnTotalMessage() {

        return totalMessagesAcrossApp;
    }

    // Store message in JSON format
    public void storeMessage() {

        String jsonStructure =
                "{\n" +
                "   \"messageID\": \"" + this.messageID + "\",\n" +
                "   \"numMessagesSent\": " + this.numMessagesSent + ",\n" +
                "   \"recipient\": \"" + this.recipient + "\",\n" +
                "   \"messageDescription\": \"" + this.messageDescription + "\",\n" +
                "   \"messageHash\": \"" + this.messageHash + "\"\n" +
                "}";

        try (FileWriter file = new FileWriter("messages.json", true)) {

            file.write(jsonStructure + "\n");

        }
        catch (IOException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error storing JSON payload file: "
                    + e.getMessage()
            );
        }
    }

    // Main Method using JOptionPane
    public static void main(String[] args) {

        // User input
        String messageID = JOptionPane.showInputDialog(
                "Enter Message ID:");

        int numMessagesSent = Integer.parseInt(
                JOptionPane.showInputDialog(
                        "Enter Number Of Messages Sent:")
        );

        String recipient = JOptionPane.showInputDialog(
                "Enter Recipient Cell Number:");

        String messageDescription = JOptionPane.showInputDialog(
                "Enter Message:");

        // Create object
        POEPart2 message1 = new POEPart2(
                messageID,
                numMessagesSent,
                recipient,
                messageDescription
        );

        // Check Message ID
        if (message1.checkMessageID()) {

            JOptionPane.showMessageDialog(
                    null,
                    "Message ID is valid"
            );
        }
        else {

            JOptionPane.showMessageDialog(
                    null,
                    "Message ID is invalid"
            );
        }

        // Check recipient
        JOptionPane.showMessageDialog(
                null,
                message1.checkRecipientCell()
        );

        // Show message hash
        JOptionPane.showMessageDialog(
                null,
                "Generated Message Hash:\n"
                + message1.createMessageHash()
        );

        // Show message details
        JOptionPane.showMessageDialog(
                null,
                message1.printMessages()
        );

        // Menu options
        String menu = """
                Choose an option:
                0 - Send Message
                1 - Store Message
                2 - Discard Message
                """;

        int choice = Integer.parseInt(
                JOptionPane.showInputDialog(menu)
        );

        // Execute choice
        JOptionPane.showMessageDialog(
                null,
                message1.sentMessage(choice)
        );

        // Show total messages
        JOptionPane.showMessageDialog(
                null,
                "Total Messages Sent: "
                + message1.returnTotalMessage()
        );
    }
}
       
    

    

