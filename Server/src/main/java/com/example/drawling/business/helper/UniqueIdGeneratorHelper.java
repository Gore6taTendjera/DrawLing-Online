package com.example.drawling.business.helper;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Component
public class UniqueIdGeneratorHelper {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int STRING_LENGTH = 8;
    private static final SecureRandom random = new SecureRandom();
    private Set<String> existingIds;

    public UniqueIdGeneratorHelper() {
        this.existingIds = new HashSet<>();
    }

    public String generateUniqueId() {
        String uniqueId;
        do {
            uniqueId = generateRandomString();
        } while (existingIds.contains(uniqueId));
        existingIds.add(uniqueId);
        return uniqueId;
    }

    private String generateRandomString() {
        StringBuilder sb = new StringBuilder(STRING_LENGTH);
        for (int i = 0; i < STRING_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    public Set<String> getExistingIds() {
        return existingIds;
    }
}
