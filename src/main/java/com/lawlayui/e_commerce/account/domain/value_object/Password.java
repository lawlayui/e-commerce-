package com.lawlayui.e_commerce.account.domain.value_object;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.lawlayui.e_commerce.account.domain.exception.InvalidHashedPasswordException;

public final class Password {

    private final String hashedValue;

    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 310000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    private Password(String hashedValue) {
        this.hashedValue = hashedValue;
    }

    public static Password createFromRaw(char[] rawPassword) {
        try {
            byte[] salt = generateSalt();
            PBEKeySpec spec = new PBEKeySpec(rawPassword, salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();

            String encodedSalt = Base64.getEncoder().encodeToString(salt);
            String encodedHash = Base64.getEncoder().encodeToString(hash);
            
            String hashedFormat = ITERATIONS + ":" + encodedSalt + ":" + encodedHash;

            return new Password(hashedFormat);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Failed to hash password", e);
        } finally {
            Arrays.fill(rawPassword, '\0');
        }
    }

    public static Password createFromHashed(String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.contains(":")) {
            throw new InvalidHashedPasswordException();
        }
        return new Password(hashedPassword);
    }

    public boolean verify(char[] rawPassword) {
        if (rawPassword == null) {
            return false;
        }

        try {
            String[] parts = this.hashedValue.split(":");
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] hashToVerify = Base64.getDecoder().decode(parts[2]);

            PBEKeySpec spec = new PBEKeySpec(rawPassword, salt, iterations, hashToVerify.length * 8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] testHash = factory.generateSecret(spec).getEncoded();

            int diff = hashToVerify.length ^ testHash.length;
            for (int i = 0; i < hashToVerify.length && i < testHash.length; i++) {
                diff |= hashToVerify[i] ^ testHash[i];
            }
            return diff == 0;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return false;
        } finally {
            Arrays.fill(rawPassword, '\0');
        }
    }

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    public String getHashedValue() {
        return hashedValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password = (Password) o;
        return hashedValue.equals(password.hashedValue);
    }

    @Override
    public int hashCode() {
        return hashedValue.hashCode();
    }
}