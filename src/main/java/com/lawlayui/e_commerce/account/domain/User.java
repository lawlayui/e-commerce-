package com.lawlayui.e_commerce.account.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.lawlayui.e_commerce.account.domain.value_object.Email;
import com.lawlayui.e_commerce.account.domain.value_object.Password;
import com.lawlayui.e_commerce.account.domain.value_object.Role;

public class User {
    private String userId;
    private Email email;
    private Password hashedPassword;
    private Role role; 
    private boolean isActive; 
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /*
    1. Register User (register)
    2. User forgets password (resetPassword)
    3. User changes password (changePassword)
    3. User becomes seller (registerAsSeller)
    4. System changes isActive to false (archiveDueToInactivity)
    */

    //Logic
    
    
    // Factory
    public static User register(String email, String password, Role role) {
        return new User(UUID.randomUUID().toString(), 
        Email.of(email), 
        Password.createFromRaw(password.toCharArray()), role, true, 
        LocalDateTime.now(),LocalDateTime.now());
    }
    public static User rehydrate(String userId, String email, String password,
        Role role, LocalDateTime createdAt, LocalDateTime udpatedAt
    ) {
        return new User(userId, Email.of(email), Password.createFromRaw(password.toCharArray()), role, true, createdAt, udpatedAt);
    } 
    private User(String userId, Email email, Password hashedPassword, Role role, boolean isActive,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getUserId() {return userId;}
    public Email getEmail() {return email;}
    public Password getHashedPassword() {return hashedPassword;}
    public Role getRole() {return role;}
    public boolean isActive() {return isActive;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
}
