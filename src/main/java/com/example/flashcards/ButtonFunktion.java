package com.example.flashcards;

import javafx.animation.KeyFrame;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

import java.sql.Time;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ButtonFunktion {
    private FlashCardController controller;
    private FlashCardScheduler cardScheduler;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Cards lastIncorrectCard;
    private int lastIncorrectIndex;
    private Queue<Integer> cardQueue = new LinkedList<>();

    public ButtonFunktion(CardChangeListener listener,FlashCardController controller) {
        this.controller = controller;
        this.cardScheduler = new FlashCardScheduler(controller);
        controller.setCardScheduler(cardScheduler);
    }
    public void skipToNextCard() {
        int currentCardIndex = controller.getCurrentCardIndex();
        cardQueue.offer(currentCardIndex);
        controller.onNextCard();
        cardScheduler.cancelScheduledTask(currentCardIndex);
        controller.userManuallySwitching = true;
        controller.resetElapsedTime();
        controller.updateTimerLabel();
    }


    public void correctButtonPressed() {
        System.out.println("Korrekt knap blev trykket: Kortet vises tidligst om 4 dage igen");
        controller.incrementCorrectCount();
        cardScheduler.scheduleNextCard(4, controller.getCurrentCardIndex());
    }

    public void almostCorrectButtonPressed() {
        System.out.println("Næsten korrekt knap blev trykket: Kortet vises igen indenfor 10 min igen");
        controller.incrementAlmostCorrectCount();
        cardScheduler.scheduleNextCard(0.16667, controller.getCurrentCardIndex()); //10 min i dage
    }

    public void partlyCorrectButtonPressed() {
        System.out.println("Delvist korrekt knap blev trykket: Kortet vises igen indenfor 5 min igen");
        controller.incrementPartlyCorrectCount();
        cardScheduler.scheduleNextCard(0.08333, controller.getCurrentCardIndex()); //5 minutter i dage
    }

    public void notCorrectButtonPressed(List<Cards> allCards) {
        System.out.println("Ikke korrekt knap blev trykket: Kortet visses igen indenfor 1 min igen");
        controller.incrementNotCorrectCount();

        lastIncorrectCard = controller.getCurrentCard();
        lastIncorrectIndex = controller.getCurrentCardIndex();
        skipToNextCard();

        // Schedule the next card after 1 minute
        cardScheduler.schedule(() -> {
            Platform.runLater(() -> {
                controller.decrementNotCorrectCount();
                controller.updateLabel();
                controller.showCardAtIndex(lastIncorrectIndex);
                controller.userManuallySwitching = false; // Brugeren har nu skiftet kort manuelt
            });
        }, 1, TimeUnit.MINUTES);
    }
}