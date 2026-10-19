import java.util.Scanner;
import java.util.ArrayList;

/**
 * QuickChat Application - Main console interface
 * Implements Parts 1 (Login) and 2 (Messaging)
 * Author: Student
 * Module: PROG5121
 */
public class QuickChat {
    
    private static Scanner scanner = new Scanner(System.in);
    private static Login currentUser = null;
    private static ArrayList<Message> allMessages = new ArrayList<>();
    private static int totalMessagesSent = 0;
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    Welcome to QuickChat v1.0");
        System.out.println("========================================\n");
        
        // Main application loop
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                // User not logged in - show authentication menu
                running = showAuthenticationMenu();
            } else {
                // User logged in - show main menu
                running = showMainMenu();
            }
        }
        
        System.out.println("\nThank you for using QuickChat. Goodbye!");
        scanner.close();
    }
    
    /**
     * Display authentication menu (login/register)
     */
    private static boolean showAuthenticationMenu() {
        System.out.println("\n=== Authentication Menu ===");
        System.out.println("1. Register New Account");
        System.out.println("2. Login to Existing Account");
        System.out.println("3. Exit");
        System.out.print("Select option (1-3): ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                handleRegistration();
                break;
            case 2:
                handleLogin();
                break;
            case 3:
                return false;
            default:
                System.out.println("Invalid option. Please try again.");
        }
        
        return true;
    }
    
    /**
     * Handle user registration
     */
    private static void handleRegistration() {
        System.out.println("\n=== Create New Account ===");
        
        // Get user details
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine().trim();
        
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine().trim();
        
        System.out.print("Enter username (must contain _ and be max 5 chars): ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Enter password (min 8 chars, capital, number, special char): ");
        String password = scanner.nextLine().trim();
        
        System.out.print("Enter cell phone (format: +27xxxxxxxxxx): ");
        String cellPhone = scanner.nextLine().trim();
        
        // Create login object
        currentUser = new Login(username, password, cellPhone, firstName, lastName);
        
        // Register and display result
        String result = currentUser.registerUser();
        System.out.println("\n" + result);
        
        // Check if registration was successful
        if (!result.contains("incorrectly") && !result.contains("not")) {
            System.out.println("Account created successfully!");
        } else {
            currentUser = null; // Registration failed
        }
    }
    
    /**
     * Handle user login
     */
    private static void handleLogin() {
        if (currentUser == null) {
            System.out.println("\nNo account registered. Please register first.");
            return;
        }
        
        System.out.println("\n=== Login ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        
        // Verify credentials
        boolean loginSuccess = currentUser.loginUser(username, password);
        String message = currentUser.returnLoginStatus(loginSuccess);
        
        System.out.println("\n" + message);
        
        if (!loginSuccess) {
            currentUser = null; // Clear user on failed login
        }
    }
    
    /**
     * Display main menu (after login)
     */
    private static boolean showMainMenu() {
        System.out.println("\n=== Welcome to QuickChat ===");
        System.out.println("1. Send Messages");
        System.out.println("2. Show Recently Sent Messages");
        System.out.println("3. View Message Report");
        System.out.println("4. Logout");
        System.out.print("Select option (1-4): ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                handleSendMessages();
                break;
            case 2:
                System.out.println("Coming Soon.");
                break;
            case 3:
                displayMessageReport();
                break;
            case 4:
                currentUser = null;
                System.out.println("Logged out successfully.");
                return true;
            default:
                System.out.println("Invalid option. Please try again.");
        }
        
        return true;
    }
    
    /**
     * Handle sending messages
     */
    private static void handleSendMessages() {
        System.out.println("\n=== Send Messages ===");
        System.out.print("How many messages do you want to send? ");
        int numMessages = getIntInput();
        
        if (numMessages <= 0) {
            System.out.println("Number must be greater than 0.");
            return;
        }
        
        // Message sending loop
        for (int i = 1; i <= numMessages; i++) {
            System.out.println("\n--- Message " + i + " of " + numMessages + " ---");
            
            // Get recipient
            System.out.print("Enter recipient cell number (+27xxxxxxxxxx): ");
            String recipient = scanner.nextLine().trim();
            
            // Get message
            System.out.print("Enter message (max 250 characters): ");
            String payload = scanner.nextLine().trim();
            
            // Create message
            Message msg = new Message(i, recipient, payload);
            
            // Validate recipient
            String recipientCheck = msg.checkRecipientCell();
            if (!recipientCheck.contains("successfully")) {
                System.out.println(recipientCheck);
                i--; // Retry this message number
                continue;
            }
            
            // Validate message length
            String lengthCheck = msg.checkMessageLength();
            if (!lengthCheck.equals("Message ready to send.")) {
                System.out.println(lengthCheck);
                i--; // Retry this message number
                continue;
            }
            
            // Generate hash
            msg.createMessageHash();
            
            // Display message details
            System.out.println("\n" + msg.getMessageDisplay());
            
            // Ask what to do with message
            System.out.println("\nMessage Options:");
            System.out.println("1. Send Message");
            System.out.println("2. Store Message for Later");
            System.out.println("0. Delete Message");
            System.out.print("Select option: ");
            
            int messageChoice = getIntInput();
            String sendResult = msg.sentMessage(messageChoice);
            System.out.println(sendResult);
            
            if (messageChoice == 1) {
                totalMessagesSent++;
                allMessages.add(msg);
            } else if (messageChoice == 2) {
                allMessages.add(msg);
            }
        }
        
        System.out.println("\n=== Summary ===");
        System.out.println("Total messages processed: " + numMessages);
        System.out.println("Total messages sent: " + totalMessagesSent);
    }
    
    /**
     * Display message report
     */
    private static void displayMessageReport() {
        if (allMessages.isEmpty()) {
            System.out.println("No messages to display.");
            return;
        }
        
        System.out.println("\n=== Message Report ===");
        System.out.println("Total Messages: " + allMessages.size());
        System.out.println("Messages Sent: " + totalMessagesSent);
        
        for (int i = 0; i < allMessages.size(); i++) {
            Message msg = allMessages.get(i);
            System.out.println("\n--- Message " + (i + 1) + " ---");
            System.out.println(msg.getMessageDisplay());
        }
    }
    
    /**
     * Helper method to get integer input from user
     */
    private static int getIntInput() {
        try {
            int value = Integer.parseInt(scanner.nextLine().trim());
            return value;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }
}
