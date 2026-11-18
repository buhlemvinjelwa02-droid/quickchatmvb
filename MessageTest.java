import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

/**
 * JUnit Test Class for Message functionality
 * Tests message validation, hash generation, and sending
 * Author: Student
 * Module: PROG5121
 */
public class MessageTest {
    
    private Message validMessage;
    private Message invalidMessage;
    
    /**
     * Set up test fixtures before each test
     */
    @Before
    public void setUp() {
        // Clear static lists
        Message.clearMessages();
        
        // Valid message
        validMessage = new Message(1, "+27718693002", 
                                  "Hi Mike, can you join us for dinner tonight?");
        
        // Invalid message
        invalidMessage = new Message(2, "08575975889", 
                                    "Hi Keegan, did you receive the payment?");
    }
    
    /**
     * Clean up after each test
     */
    @After
    public void tearDown() {
        Message.clearMessages();
    }
    
    // ==================== Message ID Tests ====================
    
    /**
     * Test 1: Message ID is valid (not more than 10 characters)
     */
    @Test
    public void testCheckMessageIDValid() {
        boolean result = validMessage.checkMessageID();
        assertTrue("Message ID should be valid", result);
    }
    
    /**
     * Test 2: Message ID is generated
     */
    @Test
    public void testMessageIDGenerated() {
        assertNotNull("Message ID should be generated", validMessage.getMessageID());
        assertTrue("Message ID should be numeric", 
                   validMessage.getMessageID().matches("[0-9]+"));
    }
    
    // ==================== Recipient Cell Tests ====================
    
    /**
     * Test 3: Recipient cell is correctly formatted
     * Expected: "Cell phone number successfully captured."
     */
    @Test
    public void testCheckRecipientCellValid() {
        String result = validMessage.checkRecipientCell();
        assertEquals("Should return success message for valid cell", 
                    "Cell phone number successfully captured.", 
                    result);
    }
    
    /**
     * Test 4: Recipient cell is incorrectly formatted
     * Expected: Error message
     */
    @Test
    public void testCheckRecipientCellInvalid() {
        String result = invalidMessage.checkRecipientCell();
        assertTrue("Should contain error message for invalid cell", 
                   result.contains("incorrectly formatted"));
    }
    
    /**
     * Test 5: Recipient cell without international code
     */
    @Test
    public void testCheckRecipientCellNoCode() {
        Message noCode = new Message(1, "0838968976", "Test message");
        String result = noCode.checkRecipientCell();
        assertTrue("Should error for missing international code", 
                   result.contains("international code"));
    }
    
    // ==================== Message Hash Tests ====================
    
    /**
     * Test 6: Message hash is created correctly
     * For message 1 with payload "Hi Mike, can you join us for dinner tonight?"
     * Expected: "00:1:HITONIGHT" (first 2 digits of ID : message number : first and last word)
     */
    @Test
    public void testCreateMessageHashFormat() {
        String hash = validMessage.createMessageHash();
        assertNotNull("Hash should not be null", hash);
        
        // Check format includes colons
        assertTrue("Hash should contain colons", hash.contains(":"));
        
        // Check format matches XX:M:WORDS
        String[] parts = hash.split(":");
        assertEquals("Hash should have 3 parts", 3, parts.length);
        assertEquals("First part should be 2 digits", 2, parts[0].length());
    }
    
    /**
     * Test 7: Message hash contains first and last words in caps
     */
    @Test
    public void testCreateMessageHashContent() {
        String hash = validMessage.createMessageHash();
        
        // Hash should be all uppercase
        assertEquals("Hash should be all uppercase", 
                    hash, hash.toUpperCase());
        
        // Should contain message number
        assertTrue("Hash should contain message number", 
                   hash.contains(":1:"));
    }
    
    /**
     * Test 8: Multiple message hashes test
     */
    @Test
    public void testCreateMultipleMessageHashes() {
        Message msg1 = new Message(1, "+27718693002", 
                                  "Hi Mike, can you join us for dinner tonight?");
        Message msg2 = new Message(2, "+27838884567", 
                                  "Where are you? You are late! I have asked you to be on time.");
        
        String hash1 = msg1.createMessageHash();
        String hash2 = msg2.createMessageHash();
        
        assertNotNull("Hash 1 should be created", hash1);
        assertNotNull("Hash 2 should be created", hash2);
        
        // Hashes should be different
        assertNotEquals("Hashes should be unique", hash1, hash2);
    }
    
    // ==================== Message Length Tests ====================
    
    /**
     * Test 9: Message length is valid (under 250 characters)
     */
    @Test
    public void testCheckMessageLengthValid() {
        String result = validMessage.checkMessageLength();
        assertEquals("Should return ready message", 
                    "Message ready to send.", 
                    result);
    }
    
    /**
     * Test 10: Message length exceeds 250 characters
     */
    @Test
    public void testCheckMessageLengthInvalid() {
        String longMessage = "A".repeat(260);
        Message tooLong = new Message(1, "+27718693002", longMessage);
        String result = tooLong.checkMessageLength();
        
        assertTrue("Should indicate message too long", 
                   result.contains("exceeds"));
        assertTrue("Should show excess characters", 
                   result.contains("10"));
    }
    
    /**
     * Test 11: Message exactly 250 characters
     */
    @Test
    public void testCheckMessageLengthBoundary() {
        String boundaryMessage = "A".repeat(250);
        Message boundary = new Message(1, "+27718693002", boundaryMessage);
        String result = boundary.checkMessageLength();
        assertEquals("Should be valid at exactly 250 chars", 
                    "Message ready to send.", 
                    result);
    }
    
    /**
     * Test 12: Empty message
     */
    @Test
    public void testCheckMessageLengthEmpty() {
        Message empty = new Message(1, "+27718693002", "");
        String result = empty.checkMessageLength();
        assertTrue("Should indicate empty message", 
                   result.contains("enter a message"));
    }
    
    // ==================== Send Message Tests ====================
    
    /**
     * Test 13: Send message choice (1 = Send)
     */
    @Test
    public void testSentMessageChoiceSend() {
        String result = validMessage.sentMessage(1);
        assertEquals("Should return send success message", 
                    "Message successfully sent.", 
                    result);
        assertTrue("Message should be marked as sent", 
                   validMessage.isMessageSent());
    }
    
    /**
     * Test 14: Store message choice (2 = Store)
     */
    @Test
    public void testSentMessageChoiceStore() {
        String result = validMessage.sentMessage(2);
        assertEquals("Should return store success message", 
                    "Message successfully stored.", 
                    result);
    }
    
    /**
     * Test 15: Disregard message choice (0 = Disregard)
     */
    @Test
    public void testSentMessageChoiceDisregard() {
        String result = validMessage.sentMessage(0);
        assertEquals("Should return disregard message", 
                    "Press 0 to delete the message.", 
                    result);
    }
    
    /**
     * Test 16: Invalid choice
     */
    @Test
    public void testSentMessageChoiceInvalid() {
        String result = validMessage.sentMessage(99);
        assertEquals("Should return invalid choice message", 
                    "Invalid choice.", 
                    result);
    }
    
    // ==================== Message Counter Tests ====================
    
    /**
     * Test 17: Total messages sent count
     */
    @Test
    public void testReturnTotalMessages() {
        Message msg1 = new Message(1, "+27718693002", "Message 1");
        Message msg2 = new Message(2, "+27838884567", "Message 2");
        
        msg1.sentMessage(1); // Send
        msg2.sentMessage(1); // Send
        
        assertEquals("Should count 2 sent messages", 2, msg1.returnTotalMessages());
    }
    
    /**
     * Test 18: Message counter increments correctly
     */
    @Test
    public void testMessageCounterIncrements() {
        for (int i = 1; i <= 5; i++) {
            Message msg = new Message(i, "+27718693002", "Test message " + i);
            msg.sentMessage(1);
        }
        
        Message lastMsg = new Message(1, "+27718693002", "Test");
        assertEquals("Should have 5 messages", 5, lastMsg.returnTotalMessages());
    }
    
    // ==================== Message Display Tests ====================
    
    /**
     * Test 19: Message display format
     */
    @Test
    public void testGetMessageDisplay() {
        validMessage.createMessageHash();
        String display = validMessage.getMessageDisplay();
        
        assertTrue("Display should contain Message ID", 
                   display.contains("Message ID:"));
        assertTrue("Display should contain Message Hash", 
                   display.contains("Message Hash:"));
        assertTrue("Display should contain Recipient", 
                   display.contains("Recipient:"));
        assertTrue("Display should contain Message", 
                   display.contains("Message:"));
    }
    
    // ==================== Static List Tests ====================
    
    /**
     * Test 20: Sent messages list
     */
    @Test
    public void testSentMessagesList() {
        Message msg = new Message(1, "+27718693002", "Test message");
        msg.sentMessage(1);
        
        assertEquals("Sent messages list should have 1 item", 
                    1, Message.getSentMessages().size());
    }
    
    /**
     * Test 21: Stored messages list
     */
    @Test
    public void testStoredMessagesList() {
        Message msg = new Message(1, "+27718693002", "Test message");
        msg.sentMessage(2);
        
        assertEquals("Stored messages list should have 1 item", 
                    1, Message.getStoredMessages().size());
    }
    
    /**
     * Test 22: Disregarded messages list
     */
    @Test
    public void testDisregardedMessagesList() {
        Message msg = new Message(1, "+27718693002", "Test message");
        msg.sentMessage(0);
        
        assertEquals("Disregarded messages list should have 1 item", 
                    1, Message.getDisregardedMessages().size());
    }
}
