package com.example.drawling.domain.model.game.round;

import com.example.drawling.domain.model.game.canvas.CanvasEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class GameRound {
    private String word;
    private int duration;
    private boolean isActive;
    private long startTime;
    private long endTime;
    // CopyOnWriteArrayList allows concurrent access when reading and writing to the list.
    private CopyOnWriteArrayList<CanvasEvent> canvasEvents;


    protected GameRound(int duration) {
        this.duration = duration;
        this.isActive = false;
    }

    protected GameRound(int duration, String word) {
        this.duration = duration;
        this.word = word;
        this.isActive = false;
        this.canvasEvents = new CopyOnWriteArrayList<>();
    }

}
