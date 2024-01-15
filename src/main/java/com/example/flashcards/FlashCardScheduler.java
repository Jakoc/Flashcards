package com.example.flashcards;

import javafx.application.Platform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FlashCardScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final FlashCardController controller;
    //private final List<Cards> allCards;
    private Map<Integer, ScheduledFuture<?>> scheduledTasks = new HashMap<>();
    private Integer nextCardIndex = null;
    private boolean userManuallySwitching = false;


    public FlashCardScheduler(FlashCardController controller) {
        this.controller = controller;
        //this.allCards = allCards;
    }
    public void schedule(Runnable task, long delay, TimeUnit timeUnit) {
        scheduler.schedule(task, delay, timeUnit);
    }
    public void scheduleNextCard(double delayInDays, int cardIndex) {
        if (!userManuallySwitching) {
            scheduler.schedule(() -> {
                Platform.runLater(() -> {
                    controller.incrementNotCorrectCount();
                    controller.updateLabel();
                    controller.showCardAtIndex(cardIndex);
                    userManuallySwitching = false; // Nulstil efter kortet er vist
                    controller.resetElapsedTime();
                    controller.updateTimerLabel();
                });
            }, (long) (delayInDays * 24 * 60 * 60 * 1000), TimeUnit.MILLISECONDS);
        }
    }

    public void cancelScheduledTask(int cardIndex) {
        ScheduledFuture<?> scheduledFuture = scheduledTasks.get(cardIndex);
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(true);
        }
        scheduledTasks.remove(cardIndex);
        clearNextCardIndex();
    }

    public boolean hasScheduledTask(int cardIndex) {
        return scheduledTasks.containsKey(cardIndex);
    }
    public Integer getNextCardIndex() {
        return nextCardIndex;
    }

    public void clearNextCardIndex() {
        nextCardIndex = null;
    }
}


