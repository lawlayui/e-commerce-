package com.lawlayui.e_commerce.account.infrastructure.persistent.user_profile;

import java.time.LocalDateTime;

import com.lawlayui.e_commerce.account.infrastructure.persistent.user.UserEntityJpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="user_profiles")
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
@Setter 
@Getter 
public class UserProfileEntityJpa {
    @Id 
    private String profileId;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name="user_id", referencedColumnName = "userId")
    private  UserEntityJpa user;
    private String fullName;
    private String phoneNumber;
    private String address; 
    private LocalDateTime updatedAt;
}
