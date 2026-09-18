package progassmentcom.quickchatmvb;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    private Login login;


    // ---------- checkUserName ----------

    @Test
    void checkUserName_validUsername_returnsTrue() {
        assertTrue(login.checkUserName());
    }

    @Test
    void checkUserName_noUnderscore_returnsFalse() {
        Login bad = new Login("kate1", "Ch@ncem8", "+27718693002", "Kate", "Peters");
        assertFalse(bad.checkUserName());
    }

    @Test
    void checkUserName_tooLong_returnsFalse() {
        Login bad = new Login("kt_peters", "Ch@ncem8", "+27718693002", "Kate", "Peters");
        assertFalse(bad.checkUserName());
    }

    @Test
    void checkUserName_null_returnsFalse() {
        Login bad = new Login(null, "Ch@ncem8", "+27718693002", "Kate", "Peters");
        assertFalse(bad.checkUserName());
    }

    // ---------- checkPasswordComplexity ----------

    @Test
    void checkPasswordComplexity_validPassword_returnsTrue() {
        assertTrue(login.checkPasswordComplexity());
    }

    @Test
    void checkPasswordComplexity_tooShort_returnsFalse() {
        Login bad = new Login("kt_p", "Ch@1", "+27718693002", "Kate", "Peters");
        assertFalse(bad.checkPasswordComplexity());
    }

    @Test
    void checkPasswordComplexity_noCapital_returnsFalse() {
        Login bad = new Login("kt_p", "ch@ncem8", "+27718693002", "Kate", "Peters");
        assertFalse(bad.checkPasswordComplexity());
    }

    @Test
    void checkPasswordComplexity_noNumber_returnsFalse() {
        Login bad = new Login("kt_p", "Ch@ncemx", "+27718693002", "Kate", "Peters");
        assertFalse(bad.checkPasswordComplexity());
    }

    @Test
    void checkPasswordComplexity_noSpecialChar_returnsFalse() {
        Login bad = new Login("kt_p", "Chancem8", "+27718693002", "Kate", "Peters");
        assertFalse(bad.checkPasswordComplexity());
    }

    // ---------- checkCellPhoneNumber ----------

    @Test
    void checkCellPhoneNumber_valid_returnsTrue() {
        assertTrue(login.checkCellPhoneNumber());
    }

    @Test
    void checkCellPhoneNumber_missingInternationalCode_returnsFalse() {
        Login bad = new Login("kt_p", "Ch@ncem8", "0718693002", "Kate", "Peters");
        assertFalse(bad.checkCellPhoneNumber());
    }

    @Test
    void checkCellPhoneNumber_tooManyDigits_returnsFalse() {
        Login bad = new Login("kt_p", "Ch@ncem8", "+2771869300212345", "Kate", "Peters");
        assertFalse(bad.checkCellPhoneNumber());
    }

    // ---------- registerUser ----------

    @Test
    void registerUser_allValid_returnsSuccessMessage() {
        String result = login.registerUser();
        assertTrue(result.contains("successfully"));
    }

    @Test
    void registerUser_badUsername_returnsUsernameError() {
        Login bad = new Login("kate1", "Ch@ncem8", "+27718693002", "Kate", "Peters");
        String result = bad.registerUser();
        assertTrue(result.contains("Username is not correctly formatted"));
    }

    @Test
    void registerUser_badPassword_returnsPasswordError() {
        Login bad = new Login("kt_p", "chancem8", "+27718693002", "Kate", "Peters");
        String result = bad.registerUser();
        assertTrue(result.contains("Password is not correctly formatted"));
    }

    @Test
    void registerUser_badCellPhone_returnsCellPhoneError() {
        Login bad = new Login("kt_p", "Ch@ncem8", "0718693002", "Kate", "Peters");
        String result = bad.registerUser();
        assertTrue(result.contains("Cell phone number incorrectly formatted"));
    }

    // ---------- loginUser / returnLoginStatus ----------

    @Test
    void loginUser_correctCredentials_returnsTrue() {
        assertTrue(login.loginUser("kt_p", "Ch@ncem8"));
    }

    @Test
    void loginUser_wrongPassword_returnsFalse() {
        assertFalse(login.loginUser("kt_p", "wrongPass1!"));
    }

    @Test
    void loginUser_wrongUsername_returnsFalse() {
        assertFalse(login.loginUser("wrong_", "Ch@ncem8"));
    }

    @Test
    void returnLoginStatus_success_returnsWelcomeMessage() {
        String status = login.returnLoginStatus(true);
        assertEquals("Welcome Kate, Peters it is great to see you again.", status);
    }

    @Test
    void returnLoginStatus_failure_returnsErrorMessage() {
        String status = login.returnLoginStatus(false);
        assertEquals("Username or password incorrect, please try again.", status);
    }
}