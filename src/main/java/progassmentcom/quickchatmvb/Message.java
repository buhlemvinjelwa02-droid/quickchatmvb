package progassmentcom.quickchatmvb;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Message {

    private String messageID;
    private final int messageNumber;
    private final String recipientCell;
    private final String messagePayload;
    private String messageHash;
    private boolean messageSent;

    private static final ArrayList<String> sentMessages = new ArrayList<>();
    private static final ArrayList<String> disregardedMessages = new ArrayList<>();
    private static final ArrayList<String> storedMessages = new ArrayList<>();

    public Message(int msgNumber, String recipient, String payload) {
        this.messageNumber = msgNumber;
        this.recipientCell = recipient;
        this.messagePayload = payload;
        this.messageSent = false;
        generateMessageID();
    }

    private void generateMessageID() {
        Random random = new Random();
        StringBuilder idBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            idBuilder.append(random.nextInt(10));
        }
        this.messageID = idBuilder.toString();
    }

    public boolean checkMessageID() {
        if (messageID == null || messageID.isEmpty()) {
            return false;
        }
        return messageID.length() <= 10;
    }

    public String checkRecipientCell() {
        if (recipientCell == null || recipientCell.isEmpty()) {
            return "Cell phone number is incorrectly formatted or does not contain an " +
                   "international code. Please correct the number and try again.";
        }
        boolean hasCode = recipientCell.startsWith("+");
        String numericPart = recipientCell.startsWith("+") ?
                            recipientCell.substring(1) : recipientCell;
        boolean lengthValid = numericPart.length() <= 10;
        boolean allDigits = numericPart.matches("[0-9]+");
        if (hasCode && lengthValid && allDigits) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an " +
                   "international code. Please correct the number and try again.";
        }
    }

    public String createMessageHash() {
        if (messageID == null || messagePayload == null) {
            return "ERROR";
        }
        String idPrefix = messageID.substring(0, Math.min(2, messageID.length()));
        String[] words = messagePayload.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 0 ? words[words.length - 1] : "";
        firstWord = firstWord.replaceAll("[^a-zA-Z0-9]", "");
        lastWord = lastWord.replaceAll("[^a-zA-Z0-9]", "");
        this.messageHash = idPrefix + ":" + messageNumber + ":" +
                          (firstWord + lastWord).toUpperCase();
        return messageHash;
    }

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

    public String printMessages() {
        StringBuilder result = new StringBuilder();
        result.append("=== Sent Messages ===");
        for (String msg : sentMessages) {
            result.append(msg);
        }
        return result.toString();
    }

    public int returnTotalMessages() {
        return sentMessages.size();
    }

    public void storeMessageAsJSON(String filename) {
        try {
            FileWriter writer = new FileWriter(filename, true);
            String json = "{\"messageID\":\"" + messageID + "\"," +
                          "\"messageNumber\":" + messageNumber + "," +
                          "\"recipientCell\":\"" + recipientCell + "\"," +
                          "\"messagePayload\":\"" + messagePayload + "\"," +
                          "\"messageHash\":\"" + messageHash + "\"," +
                          "\"messageSent\":" + messageSent + "}";
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing to JSON file: " + e.getMessage());
        }
    }

    public String getMessageDisplay() {
        return "Message ID: " + messageID +
               "Message Hash: " + messageHash +
               "Recipient: " + recipientCell +
               "Message: " + messagePayload;
    }

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