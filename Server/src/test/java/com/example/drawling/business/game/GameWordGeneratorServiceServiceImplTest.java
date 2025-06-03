package com.example.drawling.business.game;

import com.example.drawling.business.implementation.game.GameWordGeneratorServiceServiceImpl;
import com.example.drawling.domain.enums.game.GameWordCategoryEnum;
import com.example.drawling.exception.GameWordCategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GameWordGeneratorServiceServiceImplTest {

    @Mock
    private SecureRandom mockRandom;

    @InjectMocks
    private GameWordGeneratorServiceServiceImpl gameWordGeneratorService;

    @BeforeEach
    void setUp() {
        // mocks
    }

    @Test
    void testGenerateRandomWordFromCategory() {
        // Arrange
        GameWordCategoryEnum category = GameWordCategoryEnum.GEOGRAPHY;

        // Act
        String result = gameWordGeneratorService.generateRandomWordFromCategory(category);

        // Assert
        assertNotNull(result);
    }


    @Test
    void testGenerateRandomWordFromNullCategory() {
        // Act & Assert
        assertThrows(GameWordCategoryNotFoundException.class, () -> {
            gameWordGeneratorService.generateRandomWordFromCategory(null);
        });
    }

    @Test
    void testGenerateRandomWordFromRandomCategory() {
        // Act
        String result = gameWordGeneratorService.generateRandomWordFromRandomCategory();

        // Assert
        assertNotNull(result);
    }



    @Test
    void testGenerateRandomWordFromItemCategory() {
        // Arrange
        GameWordCategoryEnum category = GameWordCategoryEnum.ITEM;

        // Act
        String result = gameWordGeneratorService.generateRandomWordFromCategory(category);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testGenerateRandomWordFromAnimalCategory() {
        GameWordCategoryEnum category = GameWordCategoryEnum.ANIMAL;

        // Act
        String result = gameWordGeneratorService.generateRandomWordFromCategory(category);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testGenerateRandomWordFromFurnitureCategory() {
        // Arrange
        GameWordCategoryEnum category = GameWordCategoryEnum.FURNITURE;

        // Act
        String result = gameWordGeneratorService.generateRandomWordFromCategory(category);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testGenerateRandomWordFromEmotionCategory() {
        GameWordCategoryEnum category = GameWordCategoryEnum.EMOTION;

        // Act
        String result = gameWordGeneratorService.generateRandomWordFromCategory(category);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testGenerateRandomWordFromFoodCategory() {
        // Arrange
        GameWordCategoryEnum category = GameWordCategoryEnum.FOOD;

        // Act
        String result = gameWordGeneratorService.generateRandomWordFromCategory(category);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testGenerateRandomWordFromDrinkCategory() {
        // Arrange
        GameWordCategoryEnum category = GameWordCategoryEnum.DRINK;

        // Act
        String result = gameWordGeneratorService.generateRandomWordFromCategory(category);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testGenerateRandomWordFromSportCategory() {
        GameWordCategoryEnum category = GameWordCategoryEnum.SPORT;

        // Act
        String result = gameWordGeneratorService.generateRandomWordFromCategory(category);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testGenerateRandomWordFromPlantCategory() {
        // Arrange
        GameWordCategoryEnum category = GameWordCategoryEnum.PLANT;

        // Act
        String result = gameWordGeneratorService.generateRandomWordFromCategory(category);

        // Assert
        assertNotNull(result);
    }

}