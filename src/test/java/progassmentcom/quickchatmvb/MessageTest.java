package progassmentcom.quickchatmvb;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MessageTest {

    private Message message;


    // - checkMessageID 

    @Test
    void checkMessageID_generatedID_isValid() {
        // messageID is auto-generated as a 10 digit string in the constructor
        assertTrue(message.checkMessageID());
        assertNotNull(message.getMessageID());
        assertEquals(10, message.getMessageID().length());
    }

    //  checkRecipientCell 

    @Test
    void checkRecipientCell_validNumber_returnsSuccessMessage() {
        assertEquals("Cell phone number successfully captured.", message.checkRecipientCell());
    }

    @Test
    void checkRecipientCell_missingPlusSign_returnsErrorMessage() {
        Message msg = new Message(1, "0718693002", "Hello there");
        String result = msg.checkRecipientCell();
        assertTrue(result.contains("incorrectly formatted"));
    }

    @Test
    void checkRecipientCell_tooManyDigits_returnsErrorMessage() {
        // Numeric part after the "+" is longer than 10 digits
        Message msg = new Message(1, "+27718693002", "Hello there");
        String result = msg.checkRecipientCell();
        assertTrue(result.contains("incorrectly formatted"));
    }

    @Test
    void checkRecipientCell_nonDigitCharacters_returnsErrorMessage() {
        Message msg = new Message(1, "+27abc93002", "Hello there");
        String result = msg.checkRecipientCell();
        assertTrue(result.contains("incorrectly formatted"));
    }

    @Test
    void checkRecipientCell_empty_returnsErrorMessage() {
        Message msg = new Message(1, "", "Hello there");
        String result = msg.checkRecipientCell();
        assertTrue(result.contains("incorrectly formatted"));
    }

    //  createMessageHash 

    @Test
    void createMessageHash_followsExpectedPattern() {
        String hash = message.createMessageHash();
        String idPrefix = message.getMessageID().substring(0, 2);
        // Pattern: <first 2 digits of ID>:<message number>:<FIRSTWORD LAST WORD UPPERCASE, no spaces>
        assertEquals(idPrefix + ":1:HITODAY", hash);
    }

    @Test
    void createMessageHash_singleWordMessage_usesWordTwice() {
        Message msg = new Message(2, "+2771869300", "Hello");
        String hash = msg.createMessageHash();
        String idPrefix = msg.getMessageID().substring(0, 2);
        assertEquals(idPrefix + ":2:HELLOHELLO", hash);
    }

    //  checkMessageLength 

    @Test
    void checkMessageLength_underLimit_returnsReadyMessage() {
        assertEquals("Message ready to send.", message.checkMessageLength());
    }

    @Test
    void checkMessageLength_empty_returnsPromptMessage() {
        Message msg = new Message(1, "+2771869300", "");
        assertEquals("Please enter a message.", msg.checkMessageLength());
    }

    @Test
    void checkMessageLength_overLimit_reportsExcessCharacters() {
        String longMessage = "a".repeat(255);
        Message msg = new Message(1, "+2771869300", longMessage);
        String result = msg.checkMessageLength();
        assertTrue(result.contains("exceeds 250 characters by 5"));
    }

    //  sentMessage 

    @Test
    void sentMessage_choiceOne_addsToSentMessages() {
        String result = message.sentMessage(1);
        assertEquals("Message successfully sent.", result);
        assertTrue(message.isMessageSent());
        assertEquals(1, Message.getSentMessages().size());
    }

    @Test
    void sentMessage_choiceTwo_addsToStoredMessages() {
        String result = message.sentMessage(2);
        assertEquals("Message successfully stored.", result);
        assertEquals(1, Message.getStoredMessages().size());
    }

    @Test
    void sentMessage_choiceZero_addsToDisregardedMessages() {
        String result = message.sentMessage(0);
        assertEquals("Press 0 to delete the message.", result);
        assertEquals(1, Message.getDisregardedMessages().size());
    }

    @Test
    void sentMessage_invalidChoice_returnsInvalidMessage() {
        assertEquals("Invalid choice.", message.sentMessage(9));
    }

    // printMessages / returnTotalMessages 
    @Test
    void returnTotalMessages_afterOneSent_returnsOne() {
        message.sentMessage(1);
        assertEquals(1, message.returnTotalMessages());
    }

    @Test
    void printMessages_includesSentMessagePayload() {
        message.sentMessage(1);
        String report = message.printMessages();
        assertTrue(report.contains("Hi Mike, can you join us for lunch today"));
    }

    // getMessageDisplay 

    @Test
    void getMessageDisplay_containsAllFields() {
        message.createMessageHash();
        String display = message.getMessageDisplay();
        assertTrue(display.contains(message.getMessageID()));
        assertTrue(display.contains(message.getMessageHash()));
        assertTrue(display.contains(message.getRecipientCell()));
        assertTrue(display.contains(message.getMessagePayload()));
    }

    //  storeMessageAsJSON 

    @Test
    void storeMessageAsJSON_writesFileContainingMessageData() throws IOException {
        message.createMessageHash();
        File tempFile = File.createTempFile("quickchat", ".json");
        tempFile.deleteOnExit();

        message.storeMessageAsJSON(tempFile.getAbsolutePath());

        String content = Files.readString(tempFile.toPath());
        assertTrue(content.contains(message.getMessageID()));
        assertTrue(content.contains("Hi Mike, can you join us for lunch today"));
    }
}