package com.example.drawling.business.game;

import com.example.drawling.business.implementation.game.CanvasImageConvertorServiceImpl;
import com.example.drawling.domain.model.game.canvas.CanvasDrawLine;
import com.example.drawling.domain.model.game.canvas.CanvasEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.List;

import static com.example.drawling.constants.MyConstants.CANVAS_X;
import static com.example.drawling.constants.MyConstants.CANVAS_Y;
import static org.junit.jupiter.api.Assertions.*;

class CanvasImageConvertorServiceImplTest {

    private CanvasImageConvertorServiceImpl canvasImageConvertorService;

    @BeforeEach
    void setUp() {
        canvasImageConvertorService = new CanvasImageConvertorServiceImpl();
    }

    @Test
    void testConvertCanvasEventsToJpgImage_NormalEvents() {
        // Arrange
        CanvasDrawLine line1 = new CanvasDrawLine(10, 10, 50, 50, "#FF0000", 2);
        CanvasDrawLine line2 = new CanvasDrawLine(20, 20, 60, 60, "#00FF00", 1);

        List<CanvasEvent> events = List.of(line1, line2);

        // Act
        BufferedImage image = canvasImageConvertorService.convertCanvasEventsToJpgImage(events);

        // Assert
        assertNotNull(image, "Generated image should not be null");
        assertEquals(CANVAS_X, image.getWidth());
        assertEquals(CANVAS_Y, image.getHeight());
    }

    @Test
    void testConvertCanvasEventsToJpgImage_EmptyEvents() {
        // Arrange
        List<CanvasEvent> events = List.of();

        // Act
        BufferedImage image = canvasImageConvertorService.convertCanvasEventsToJpgImage(events);

        // Assert
        assertNotNull(image, "Generated image should not be null");
        assertEquals(CANVAS_X, image.getWidth());
        assertEquals(CANVAS_Y, image.getHeight());
    }

    @Test
    void testConvertCanvasEventsToJpgImage_NullEvents() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> canvasImageConvertorService.convertCanvasEventsToJpgImage(null),
                "NullPointerException expected for null canvas events");
    }

    @Test
    void testConvertCanvasEventsToJpgImage_InvalidColor() {
        // Arrange
        CanvasDrawLine invalidLine = new CanvasDrawLine(10, 10, 50, 50, "INVALID_COLOR", 2);
        List<CanvasEvent> events = List.of(invalidLine);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> canvasImageConvertorService.convertCanvasEventsToJpgImage(events),
                "Expected IllegalArgumentException for invalid color");
    }

    @Test
    void testConvertCanvasEventsToJpgImage_NegativeDimensions() {
        // Arrange
        CanvasDrawLine lineWithNegativeCoords = new CanvasDrawLine(-10, -10, -50, -50, "#0000FF", 1);
        List<CanvasEvent> events = List.of(lineWithNegativeCoords);

        // Act
        BufferedImage image = canvasImageConvertorService.convertCanvasEventsToJpgImage(events);

        // Assert
        assertNotNull(image, "Generated image should not be null even with negative dimensions");
        assertEquals(CANVAS_X, image.getWidth());
        assertEquals(CANVAS_Y, image.getHeight());
    }

    @Test
    void testConvertCanvasEventsToJpgImage_NonDrawLineEvents() {
        // Arrange
        CanvasEvent nonDrawLineEvent = new CanvasEvent() {};
        List<CanvasEvent> events = List.of(nonDrawLineEvent);

        // Act
        BufferedImage image = canvasImageConvertorService.convertCanvasEventsToJpgImage(events);

        // Assert
        assertNotNull(image, "Generated image should not be null");
        assertEquals(CANVAS_X, image.getWidth());
        assertEquals(CANVAS_Y, image.getHeight());
    }
}
