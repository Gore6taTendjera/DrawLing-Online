package com.example.drawling.business.game.round;

import com.example.drawling.business.creator.game.GameRoundCreator;
import com.example.drawling.business.interfaces.service.game.GameWordGeneratorService;
import com.example.drawling.domain.dto.game.GameRoundRequestDTO;
import com.example.drawling.domain.enums.game.GameRoundEnum;
import com.example.drawling.domain.enums.game.GameWordCategoryEnum;
import com.example.drawling.domain.model.game.round.GameRound;
import com.example.drawling.domain.model.game.round.GameRoundFast;
import com.example.drawling.domain.model.game.round.GameRoundNormal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class GameRoundCreatorTest {

    @InjectMocks
    private GameRoundCreator gameRoundCreator;

    @Mock
    private GameWordGeneratorService gameWordGeneratorService;

    @Test
    void testCreateGameRounds_ValidInput() {
        GameRoundRequestDTO request1 = new GameRoundRequestDTO(GameRoundEnum.NORMAL, 1, null);
        GameRoundRequestDTO request2 = new GameRoundRequestDTO(GameRoundEnum.FAST, 2, GameWordCategoryEnum.ANIMAL);

        when(gameWordGeneratorService.generateRandomWordFromRandomCategory()).thenReturn("word1");
        when(gameWordGeneratorService.generateRandomWordFromCategory(GameWordCategoryEnum.ANIMAL)).thenReturn("word2");

        List<GameRound> result = gameRoundCreator.createGameRounds(Arrays.asList(request1, request2));

        assertEquals(2, result.size());
        assertInstanceOf(GameRoundNormal.class, result.get(0));
        assertEquals("word1", ((GameRoundNormal) result.get(0)).getWord());
        assertInstanceOf(GameRoundFast.class, result.get(1));
        assertEquals("word2", ((GameRoundFast) result.get(1)).getWord());
    }
    @Test
    void testCreateGameRounds_EmptyRequest() {
        List<GameRoundRequestDTO> emptyRequest = Collections.emptyList();
        assertThrows(IllegalArgumentException.class, () -> gameRoundCreator.createGameRounds(emptyRequest));
    }

    @Test
    void testCreateGameRounds_NullRequest() {
        assertThrows(IllegalArgumentException.class, () -> gameRoundCreator.createGameRounds(null));
    }

    @Test
    void testCreateGameRounds_NullRoundRequest() {
        List<GameRoundRequestDTO> requestList = Arrays.asList(new GameRoundRequestDTO(GameRoundEnum.NORMAL, 1, null), null);
        assertThrows(IllegalArgumentException.class, () -> gameRoundCreator.createGameRounds(requestList));
    }

    @Test
    void testCreateGameRounds_NegativeDuration() {
        GameRoundRequestDTO request = new GameRoundRequestDTO(GameRoundEnum.NORMAL, -1, null);
        List<GameRoundRequestDTO> requestList = Collections.singletonList(request);
        assertThrows(IllegalArgumentException.class, () -> gameRoundCreator.createGameRounds(requestList));
    }

    @Test
    void testCreateGameRounds_NullRoundEnum() {
        GameRoundRequestDTO request = new GameRoundRequestDTO(null, 1, null);
        List<GameRoundRequestDTO> requestList = Collections.singletonList(request);
        assertThrows(IllegalArgumentException.class, () -> gameRoundCreator.createGameRounds(requestList));
    }

}