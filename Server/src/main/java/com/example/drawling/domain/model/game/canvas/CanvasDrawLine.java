package com.example.drawling.domain.model.game.canvas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CanvasDrawLine extends CanvasEvent {
    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private String color;
    private int lineWidth;
}
