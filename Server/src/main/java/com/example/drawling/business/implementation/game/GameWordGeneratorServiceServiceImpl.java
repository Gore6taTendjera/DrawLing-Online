package com.example.drawling.business.implementation.game;

import com.example.drawling.business.interfaces.service.game.GameWordGeneratorService;
import com.example.drawling.domain.enums.game.GameWordCategoryEnum;
import com.example.drawling.exception.GameWordCategoryNotFoundException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class GameWordGeneratorServiceServiceImpl implements GameWordGeneratorService {

    private final SecureRandom random = new SecureRandom();


    // can be saved in the db
    // for now like this
    private final List<String> geographyWords = List.of("Mountain", "River", "Desert", "Ocean");
    private final List<String> itemWords = List.of("Book", "Pen", "Laptop", "Chair");
    private final List<String> animalWords = List.of("Dog", "Cat", "Elephant", "Tiger");
    private final List<String> furnitureWords = List.of("Table", "Sofa", "Bed", "Desk");
    private final List<String> emotionWords = List.of("Happiness", "Sadness", "Anger", "Fear");
    private final List<String> foodWords = List.of("Pizza", "Sushi", "Burger", "Salad");
    private final List<String> drinkWords = List.of("Water", "Coffee", "Tea", "Juice");
    private final List<String> sportWords = List.of("Soccer", "Basketball", "Tennis", "Baseball");
    private final List<String> plantWords = List.of("Tree", "Flower", "Grass", "Bush");

    public String generateRandomWordFromCategory(GameWordCategoryEnum gameWordCategoryEnum) {
        if (gameWordCategoryEnum == null) {
            throw new GameWordCategoryNotFoundException("Game word category enum cannot be null");
        }

        List<String> words;

        words = switch (gameWordCategoryEnum) {
            case GEOGRAPHY -> geographyWords;
            case ITEM -> itemWords;
            case ANIMAL -> animalWords;
            case FURNITURE -> furnitureWords;
            case EMOTION -> emotionWords;
            case FOOD -> foodWords;
            case DRINK -> drinkWords;
            case SPORT -> sportWords;
            case PLANT -> plantWords;
        };

        return words.get(random.nextInt(words.size()));
    }

    public String generateRandomWordFromRandomCategory() {
        GameWordCategoryEnum[] categories = GameWordCategoryEnum.values();
        GameWordCategoryEnum randomCategory = categories[random.nextInt(categories.length)];

        return generateRandomWordFromCategory(randomCategory);
    }
}
