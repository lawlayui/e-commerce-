package com.lawlayui.e_commerce.account.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lawlayui.e_commerce.account.domain.exception.InvalidEmailException;
import com.lawlayui.e_commerce.account.domain.exception.InvalidHashedPasswordException;
// import com.lawlayui.e_commerce.account.domain.value_object.Email;
import com.lawlayui.e_commerce.account.domain.value_object.Password;
import com.lawlayui.e_commerce.account.domain.value_object.Role;

class UserTest {
    private String USER_ID = "1";
    private String RAW_EMAIL = "person@gmail.com";
    private String RAW_PASSWORD = "12345678";

    private Role role = Role.CUSTOMER;
    private Password HASHED_PASSWORD = Password.createFromRaw(RAW_PASSWORD.toCharArray());
    // private Email EMAIL = Email.of(RAW_EMAIL);

    private User USER = User.rehydrate(USER_ID, RAW_EMAIL, HASHED_PASSWORD.getHashedValue(), role,
         LocalDateTime.now(), LocalDateTime.now());

    @BeforeEach  
    void setUp() {
        USER = User.rehydrate(USER_ID, RAW_EMAIL, HASHED_PASSWORD.getHashedValue(), role,
        LocalDateTime.now(), LocalDateTime.now());

    }

    // Factory Test
    @Test 
    @DisplayName("User registration testing")
    void register_ShouldReturnNewUser_WhenAllParametersAreTrue() {
        User user = User.register(RAW_EMAIL, RAW_PASSWORD, role);

        assertNotNull(user);
        assertEquals(RAW_EMAIL, user.getEmail().getValue());
        assertEquals(Role.CUSTOMER, role);
    }

    @Test
    @DisplayName("User registration when email is incorrect")
    void register_ShouldThrowInvalidEmailException_WhenEmailInvalid() {
        assertThrows(InvalidEmailException.class,
            () -> User.register("invalidEmail", RAW_PASSWORD, role));
    }

    @Test 
    @DisplayName("Rehydrate testing")
    void rehydrate_ShouldReturnUser_WhenAllParametersAreTrue() {
        User user = User.rehydrate(USER_ID, RAW_EMAIL, HASHED_PASSWORD.getHashedValue(), role,
             LocalDateTime.now(), LocalDateTime.now());

        assertNotNull(user);
        assertEquals(USER_ID, user.getUserId());
        assertEquals(RAW_EMAIL, user.getEmail().getValue());
        assertEquals(role, user.getRole());
    }

    @Test 
    @DisplayName("Rehydrate when email is incorrect")
    void rehydrate_ShouldThrowInvalidEmailException_WhenEmailInvalid() {
        assertThrows(InvalidEmailException.class,
             () -> User.rehydrate(USER_ID, "invaildEmail", HASHED_PASSWORD.getHashedValue(), role,
             LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test 
    @DisplayName("Rehydrate when fromat hashed password invalid") 
    void rehydrate_ShouldThorwInvalidHashedPassword_WhenPasswordIsInvalid() {
        assertThrows(InvalidHashedPasswordException.class, () -> 
        User.rehydrate(USER_ID, RAW_EMAIL, "invalidFormatPasswrod", role,
        LocalDateTime.now(), LocalDateTime.now()));
    }

    //VO 
    @Test 
    @DisplayName("Verify password")
    void verifyPassword_ShouldReturnTrue_WhenPasswordIsTrue() {
        boolean result = USER.getHashedPassword().verify(RAW_PASSWORD.toCharArray());
        assertEquals(true, result);
    }

    @Test 
    @DisplayName("Verify password when password incorrect")
    void verifyPassword_ShouldReturnFalse_WhenPasswordIsFalse() {
        boolean result = USER.getHashedPassword().verify("Incorrect password".toCharArray());
        assertEquals(false, result);
    }

    //Logic
    @Test 
    @DisplayName("ResetPassword testing")
    void resetPassword_ShouldPasswordChanged() {
        String newPassword = "new_password";
        USER.resetPassword(newPassword);
        assertTrue(USER.getHashedPassword().verify(newPassword.toCharArray()));
    }

    @Test 
    @DisplayName("ChangePassword testing")
    void changePassword_ShouldPasswordChanged() {
        String newPassword = "new_password1";
        USER.resetPassword(newPassword);
        assertTrue(USER.getHashedPassword().verify(newPassword.toCharArray()));
    }

    @Test 
    @DisplayName("RegisterAsSeller testing")
    void registerAsSeller_ShouldRoleChanged() {
        USER.registerAsSeller();
        assertEquals(Role.SELLER, USER.getRole());
    }
}
