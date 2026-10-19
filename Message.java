import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Random;
import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Message class handles message creation, validation, and storage
 * Author: Student
 * Module: PROG5121
 */
public class Message {
    
    // Instance variables
    private String messageID;
    private int messageNumber;
    private String recipientCell;
    private String messagePayload;
    private String messageHash;
    private boolean messageSent;
    private boolean messageReceived;
    private boolean messageRead;
    
    // Static for tracking message count
    private static int messageCounter = 0;
    private static ArrayList<String> sentMessages = new ArrayList<>();
    private static ArrayList<String> disregardedMessages = new ArrayList<>();
    private static ArrayList<String> storedMessages = new ArrayList<>();
    
    /**
     * Constructor for Message
     */
    public Message(int msgNumber, String recipient, String payload) {
        this.messageNumber = msgNumber;
        this.recipientCell = recipient;
        this.messagePayload = payload;
        this.messageSent = false;
        this.messageReceived = false;
        this.messageRead = false;
        
        // Generate message ID
        generateMessageID();
    }
    
    /**
     * Generates a random 10-digit message ID
     */
    private void generateMessageID() {
        Random random = new Random();
        StringBuilder idBuilder = new StringBuilder();
        
        for (int i = 0; i < 10; i++) {
            idBuilder.append(random.nextInt(10));
        }
        
        this.messageID = idBuilder.toString();
    }
    
    /**
     * Checks if message ID is valid (not more than 10 characters)
     * @return true if valid, false otherwise
     */
    public boolean checkMessageID() {
        if (messageID == null || messageID.isEmpty()) {
            return false;
        }
        
        return messageID.length() <= 10;
    }
    
    /**
     * Validates recipient cell phone number
     * - No more than 10 characters
     * - Contains international code
     * @return String with validation result
     */
    public String checkRecipientCell() {
        if (recipientCell == null || recipientCell.isEmpty()) {
            return "Cell phone number is incorrectly formatted or does not contain an " +
                   "international code. Please correct the number and try again.";
        }
        
        // Check if contains international code (starts with +)
        boolean hasCode = recipientCell.startsWith("+");
        
        // Remove + for length check if present
        String numericPart = recipientCell.startsWith("+") ? 
                            recipientCell.substring(1) : recipientCell;
        
        // Check length (max 10 digits after code)
        boolean lengthValid = numericPart.length() <= 10;
        
        // Verify all characters are digits (after +)
        boolean allDigits = numericPart.matches("[0-9]+");
        
        if (hasCode && lengthValid && allDigits) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an " +
                   "international code. Please correct the number and try again.";
        }
    }
    
    /**
     * Creates message hash in format: XX:M:FIRSTWORD LASTWORD
     * Where XX = first 2 digits of message ID
     *       M = message number
     * @return String message hash
     */
    public String createMessageHash() {
        if (messageID == null || messagePayload == null) {
            return "ERROR";
        }
        
        // Get first 2 characters of message ID
        String idPrefix = messageID.substring(0, Math.min(2, messageID.length()));
        
        // Get first and last words from message
        String[] words = messagePayload.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 0 ? words[words.length - 1] : "";
        
        // Remove punctuation from words
        firstWord = firstWord.replaceAll("[^a-zA-Z0-9]", "");
        lastWord = lastWord.replaceAll("[^a-zA-Z0-9]", "");
        
        // Create hash in format: XX:M:FIRSTLAST (all caps)
        this.messageHash = idPrefix + ":" + messageNumber + ":" + 
                          (firstWord + lastWord).toUpperCase();
        
        return messageHash;
    }
    
    /**
     * Validates message length (max 250 characters)
     * @return String with validation result
     */
    public String checkMessageLength() {
        if (messagePayload == null || messagePayload.isEmpty()) {
            return "Please enter a message.";
        }
        
        if (messagePayload.length() > 250) {
            int excess = messagePayload.length() - 250;
            return "Please enter a message of less than 250 characters. " +
                   "Message exceeds 250 characters by " + excess + ".";
        }
        
        return "Message ready to send.";
    }
    
    /**
     * Handles user choice for sending, storing, or disregarding message
     * @param choice 1 = Send, 2 = Store, 0 = Disregard
     * @return String with appropriate message
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                this.messageSent = true;
                sentMessages.add(messagePayload);
                return "Message successfully sent.";
                
            case 2:
                storedMessages.add(messagePayload);
                return "Message successfully stored.";
                
            case 0:
                disregardedMessages.add(messagePayload);
                return "Press 0 to delete the message.";
                
            default:
                return "Invalid choice.";
        }
    }
    
    /**
     * Prints all messages (for testing purposes)
     * @return String with all sent messages
     */
    public String printMessages() {
        StringBuilder result = new StringBuilder();
        result.append("=== Sent Messages ===\n");
        
        for (String msg : sentMessages) {
            result.append(msg).append("\n");
        }
        
        return result.toString();
    }
    
    /**
     * Returns total number of messages sent
     * @return int total message count
     */
    public int returnTotalMessages() {
        return sentMessages.size();
    }
    
    /**
     * Stores message as JSON
     * Note: Requires GSON library (com.google.gson)
     * @param filename Path to save JSON file
     */
    public void storeMessageAsJSON(String filename) {
        try {
            Gson gson = new Gson();
            FileWriter writer = new FileWriter(filename, true); // append mode
            
            // Create JSON object for this message
            String json = gson.toJson(this);
            writer.write(json + "\n");
            writer.close();
            
        } catch (IOException e) {
            System.out.println("Error writing to JSON file: " + e.getMessage());
        }
    }
    
    /**
     * Gets formatted message display
     * @return String with message details
     */
    public String getMessageDisplay() {
        return "Message ID: " + messageID + "\n" +
               "Message Hash: " + messageHash + "\n" +
               "Recipient: " + recipientCell + "\n" +
               "Message: " + messagePayload;
    }
    
    // Getters and Setters
    public String getMessageID() {
        return messageID;
    }
    
    public String getRecipientCell() {
        return recipientCell;
    }
    
    public String getMessagePayload() {
        return messagePayload;
    }
    
    public String getMessageHash() {
        return messageHash;
    }
    
    public int getMessageNumber() {
        return messageNumber;
    }
    
    public boolean isMessageSent() {
        return messageSent;
    }
    
    public static void clearMessages() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageCounter = 0;
    }
    
    public static ArrayList<String> getSentMessages() {
        return sentMessages;
    }
    
    public static ArrayList<String> getStoredMessages() {
        return storedMessages;
    }
    
    public static ArrayList<String> getDisregardedMessages() {
        return disregardedMessages;
    }
}
