package com.example.drawling.business.helper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UniqueIdGeneratorHelperTest {

    @InjectMocks
    private UniqueIdGeneratorHelper uniqueIdGeneratorHelper;


    @Test
    void testGenerateUniqueId() {
        // Arrange
        Set<String> existingIds = new HashSet<>();
        existingIds.add("existing1");
        existingIds.add("existing2");
        uniqueIdGeneratorHelper = new UniqueIdGeneratorHelper() {
            @Override
            public Set<String> getExistingIds() {
                return existingIds;
            }
        };

        // Act
        String uniqueId = uniqueIdGeneratorHelper.generateUniqueId();

        // Assert
        assertNotNull(uniqueId);
        assertFalse(existingIds.contains(uniqueId));
    }

    @Test
    void testGenerateUniqueIdWhenIdExists() {
        // Arrange
        String existingId = "existingId";
        uniqueIdGeneratorHelper.generateUniqueId();
        uniqueIdGeneratorHelper.getExistingIds().add(existingId);

        // Act
        String uniqueId = uniqueIdGeneratorHelper.generateUniqueId();

        // Assert
        assertNotNull(uniqueId);
        assertNotEquals(uniqueId, existingId);
    }
}