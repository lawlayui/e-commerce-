package com.lawlayui.e_commerce.account.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lawlayui.e_commerce.account.domain.entity.UserProfile;
import com.lawlayui.e_commerce.account.domain.exception.InvalidAddressException;
import com.lawlayui.e_commerce.account.domain.exception.InvalidPhoneNumberException;

public class UserProfileTest {
    private String PROFILE_ID = "1";
    private String USER_ID = "2";
    private String FULL_NAME = "Lawlayui";
    private String PHONE_NUMBER = "0812345678";
    private String COUNTRY_CODE = "+62";
    private String ADDRESS = "Jl. Merdeka No. 10, Kel. Braga, Kec. Sumur Bandung, Kota Bandung, Jawa Barat, 40111";
    
    private UserProfile userProfile; 

    @BeforeEach 
    void setUp() {
        userProfile = UserProfile.reconstitue(PROFILE_ID, USER_ID, FULL_NAME,PHONE_NUMBER, COUNTRY_CODE, ADDRESS, LocalDateTime.now());
    }
    //Factory Test
    @Test 
    void reconstitue_ShouldReturnNewUserProfile() {
        UserProfile userProfile = UserProfile.reconstitue(PROFILE_ID, USER_ID, FULL_NAME, PHONE_NUMBER, FULL_NAME, ADDRESS, LocalDateTime.now());

        assertNotNull(userProfile);
    }

    @Test 
    void create_ShouldReturnNewUserProfile()  {
        UserProfile userProfile = UserProfile.create(USER_ID, FULL_NAME, PHONE_NUMBER, COUNTRY_CODE, ADDRESS);

        assertNotNull(userProfile);
    }

    //Logic Test
    @Test 
    void rename_ShouldChangeFullName() {
        String newName = "Firure";

        userProfile.rename(newName);

        assertEquals(newName, userProfile.getFullName());
    }

    @Test 
    void updateContactNumber_ShouldChangeContactNumber() {
        String newNumber = "0887654321";
        String newCountyCode = "+1";

        userProfile.updateContactNumber(newCountyCode, newNumber);

        assertEquals(newCountyCode + " " + newNumber, userProfile.getPhoneNumber().getValue());
    }

    @Test 
    void updateContactNumber_ShouldThorwInvalidPhoneNumberException_WhenNumberInvalid() {
        String newNumber = "00654321";
        String newCountyCode = "+1";

        assertThrows(InvalidPhoneNumberException.class, () -> {
            userProfile.updateContactNumber(newCountyCode, newNumber);
        });
    }

    @Test 
    void relocateTo_ShouldChangeAddressAndCityAndPostalCode() {
        String newAddress = "Jl. Merdeka No. 10, Kel. Braga, Kec. Sumur Bandung, Kota Bandung, Jawa Barat, 40221";

        userProfile.relocateTo(newAddress);

        assertEquals(newAddress, userProfile.getAddress().getValue());
    }

    @Test 
    void relocateTo_ShouldThrowInvalidAddressException_WhenAddresInvalidFormat() {
        String newAddress = "Jl. Merdeka No. 45, Kel";

        assertThrows(InvalidAddressException.class, () -> {
            userProfile.relocateTo(newAddress);
        });
    }
}
