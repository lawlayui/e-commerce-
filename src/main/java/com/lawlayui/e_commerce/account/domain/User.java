package com.lawlayui.e_commerce.account.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.lawlayui.e_commerce.account.domain.value_object.Email;
import com.lawlayui.e_commerce.account.domain.value_object.Password;
import com.lawlayui.e_commerce.account.domain.value_object.Role;

public class User {
    // Atribute
    private String userId;
    private Email email;
    private Password hashedPassword;
    private Role role; 
    private boolean isActive; 
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

  
    //Logic
    public void resetPassword(String newPassword){
        Password password = Password.createFromRaw(newPassword.toCharArray());
        this.hashedPassword = password;
    }    

    public void changePassword(String newPassword) {
        Password password = Password.createFromRaw(newPassword.toCharArray());
        this.hashedPassword = password;
    }

    public void registerAsSeller() {
        this.role = Role.SELLER;
    }

    public void archiveDueToInactivity() {
      this.isActive = true;
    }

  
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
        return new User(userId, Email.of(email), Password.createFromHashed(password), role, true, createdAt, udpatedAt);
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

  
    // Getter
    public String getUserId() {return userId;}
    public Email getEmail() {return email;}
    public Password getHashedPassword() {return hashedPassword;}
    public Role getRole() {return role;}
    public boolean isActive() {return isActive;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
}
