package progassmentcom.quickchatmvb;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Login {

    private String username;
    private String password;
    private String cellPhoneNumber;
    private String userFirstName;
    private String userLastName;

    public Login(String username, String password, String cellPhoneNumber,
                 String userFirstName, String userLastName) {
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
    }

    public boolean checkUserName() {
        if (username == null || username.isEmpty()) {
            return false;
        }
        boolean hasUnderscore = username.contains("_");
        boolean lengthValid = username.length() <= 5;
        return hasUnderscore && lengthValid;
    }

    public boolean checkPasswordComplexity() {
        if (password == null || password.isEmpty()) {
            return false;
        }
        boolean lengthValid = password.length() >= 8;
        boolean hasCapital = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasNumber = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecialChar = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]")
                                        .matcher(password).find();
        return lengthValid && hasCapital && hasNumber && hasSpecialChar;
    }

    public boolean checkCellPhoneNumber() {
        if (cellPhoneNumber == null || cellPhoneNumber.isEmpty()) {
            return false;
        }
        String regex = "^\\+\\d{2}\\d{8,10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cellPhoneNumber);
        return matcher.matches();
    }

    public String registerUser() {
        if (!checkUserName()) {
            return "Username is not correctly formatted; please ensure that your username " +
                   "contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted; please ensure that the password " +
                   "contains at least eight characters, a capital letter, a number, and a " +
                   "special character.";
        }
        if (!checkCellPhoneNumber()) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }
        return "Username successfully captured." +
               "Password successfully captured." +
               "Cell phone number successfully added.";
    }

    public boolean loginUser(String enteredUsername, String enteredPassword) {
        boolean usernameMatch = (this.username != null) && this.username.equals(enteredUsername);
        boolean passwordMatch = (this.password != null) && this.password.equals(enteredPassword);
        return usernameMatch && passwordMatch;
    }

    public String returnLoginStatus(boolean loginSuccess) {
        if (loginSuccess) {
            return "Welcome " + userFirstName + ", " + userLastName +
                   " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }
}