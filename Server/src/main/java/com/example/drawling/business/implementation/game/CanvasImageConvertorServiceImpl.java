package com.example.drawling.business.implementation.game;

import com.example.drawling.domain.model.game.canvas.CanvasDrawLine;
import com.example.drawling.domain.model.game.canvas.CanvasEvent;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static com.example.drawling.constants.MyConstants.CANVAS_X;
import static com.example.drawling.constants.MyConstants.CANVAS_Y;

@Service
public class CanvasImageConvertorServiceImpl {

    public BufferedImage convertCanvasEventsToJpgImage(List<CanvasEvent> canvasEvents) {
        int width = CANVAS_X;
        int height = CANVAS_Y;

        if (canvasEvents == null) {
            throw new IllegalArgumentException("Canvas events cannot be null");
        }

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        try {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            for (CanvasEvent event : canvasEvents) {
                if (event instanceof CanvasDrawLine line) {
                    g2d.setColor(Color.decode(line.getColor()));

                    g2d.setStroke(new BasicStroke(line.getLineWidth()));

                    g2d.drawLine((int) line.getStartX(), (int) line.getStartY(), (int) line.getEndX(), (int) line.getEndY());
                }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid color hex in CanvasDrawLine object", e);
        } finally {
            g2d.dispose();
        }

        return bufferedImage;
    }

}
