package com.example.flashcards;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.PriorityQueue;
import java.util.Comparator;


public class ButtonFunktion {
    private FlashCardController controller;
    private FlashCardScheduler cardScheduler;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Queue<Cards> cardQueue = new PriorityQueue<>(Comparator.comparing(Cards::getShowTime));

    public ButtonFunktion(FlashCardController controller, FlashCardScheduler cardScheduler) {
        this.controller = controller;
        this.cardScheduler = cardScheduler;
    }

    public void correctButtonPressed(Cards card) {
        //visnings tidspunkt baseret på hvilken knap
        card.setNextShowTimeBasedOnButton("korrekt");
        updateCardInQueue(card);
        cardScheduler.setUserManuallySwitching(true);
        //tilføjer kort til den kø, som skal vises efter delay
        cardScheduler.addCardToQueue(card);
        //planlæger hvornår kortet skal vises igen
        if (!cardQueue.isEmpty()) {
            cardScheduler.scheduleNextCardFromQueue();
        }
        //opdatere passende label
        controller.incrementCorrectCount();
        //skifter til næste kort og viser næste kort
        controller.setCurrentCardIndex(controller.getCurrentCardIndex() + 4);
        controller.showCardAtIndex(controller.getCurrentCardIndex());
        //opdatere tiden
        controller.resetElapsedTime();
        controller.updateTimerLabel();


        //planlægger hvornår label skal opdateres igen når delay er færdig
        long delay = TimeUnit.DAYS.toMillis(4);
        scheduler.schedule(() -> {
            Platform.runLater(() -> {
                controller.decrementCorrectCount();
                controller.updateLabel();
            });
        }, delay, TimeUnit.MILLISECONDS);

        controller.onNextCard(new ActionEvent());

    }

    public void almostCorrectButtonPressed(Cards card) {
        card.setNextShowTimeBasedOnButton("næsten korrekt");
        updateCardInQueue(card);
        cardScheduler.setUserManuallySwitching(true);
        controller.incrementAlmostCorrectCount();
        cardScheduler.addCardToQueue(card);
        if (!cardQueue.isEmpty()) {
            cardScheduler.scheduleNextCardFromQueue();
        }
        controller.setCurrentCardIndex(controller.getCurrentCardIndex() + 3); // Gå til næste kort
        controller.showCardAtIndex(controller.getCurrentCardIndex());
        controller.resetElapsedTime();
        controller.updateTimerLabel();

        long delay = TimeUnit.MINUTES.toMillis(10);
        scheduler.schedule(() -> {
            Platform.runLater(() -> {
                controller.decrementAlmostCorrectCount();
                controller.updateLabel();
            });
        }, delay, TimeUnit.MILLISECONDS);

        controller.onNextCard(new ActionEvent());
    }

    public void partlyCorrectButtonPressed(Cards card) {
        card.setNextShowTimeBasedOnButton("delvist korrekt");
        updateCardInQueue(card);
        cardScheduler.setUserManuallySwitching(true);
        controller.incrementPartlyCorrectCount();
        cardScheduler.addCardToQueue(card);
        if (!cardQueue.isEmpty()) {
            cardScheduler.scheduleNextCardFromQueue();
        }
        controller.setCurrentCardIndex(controller.getCurrentCardIndex() + 2); // Gå til næste kort
        controller.showCardAtIndex(controller.getCurrentCardIndex());
        controller.resetElapsedTime();
        controller.updateTimerLabel();



        long delay = TimeUnit.MINUTES.toMillis(5);
        scheduler.schedule(() -> {
            Platform.runLater(() -> {
                controller.decrementPartlyCorrectCount();
                controller.updateLabel();
            });
        }, delay, TimeUnit.MILLISECONDS);

        controller.onNextCard(new ActionEvent());
    }

    public void notCorrectButtonPressed(Cards card) {
        card.setNextShowTimeBasedOnButton("ikke korrekt");
        updateCardInQueue(card);
        cardScheduler.setUserManuallySwitching(true);
        controller.incrementNotCorrectCount();
        if (!cardQueue.isEmpty()) {
            cardScheduler.scheduleNextCardFromQueue();
        }
        cardScheduler.addCardToQueue(card);
        controller.setCurrentCardIndex(controller.getCurrentCardIndex() + 1); // Gå til næste kort
        controller.showCardAtIndex(controller.getCurrentCardIndex());
        controller.resetElapsedTime();
        controller.updateTimerLabel();


        long delay = TimeUnit.MINUTES.toMillis(1);
        scheduler.schedule(() -> {
            Platform.runLater(() -> {
                controller.decrementNotCorrectCount();
                controller.updateLabel();
            });
        }, delay, TimeUnit.MILLISECONDS);

        controller.onNextCard(new ActionEvent());
    }
    private void updateCardInQueue(Cards card) {
        // Fjern kortet fra køen, hvis det allerede er der
        cardScheduler.getCardQueue().remove(card);

        // Tilføj kortet til køen igen med den opdaterede showTime
        cardScheduler.getCardQueue().add(card);
    }
}