package com.example.flashcards;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ButtonFunktion {
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private CardChangeListener cardChangeListener;
    private FlashCardController controller;


    public ButtonFunktion(CardChangeListener listener, FlashCardController controller) {
        this.cardChangeListener = listener;
        this.controller = controller;
    }
    public void correctButtonPressed(){
        scheduleReview(4 * 24 * 60); //4 Dage
        System.out.println("korrekt knap blev trykket: Kortet visses tidligst om 4 dage igen");
        Platform.runLater(() -> {
            controller.incrementCorrectCount();
            cardChangeListener.onNextCard();
        });
    }

    public void almostCorrectButtonPressed(){
        scheduleReview(10); // 10 min
        System.out.println("Næsten korrekt knap blev trykket: Kortet visses tidligst om 10 min igen");
        Platform.runLater(() -> {
            controller.incrementCorrectCount();
            cardChangeListener.onNextCard();
        });
    }

    public void partlyCorrectButtonPressed(){
        scheduleReview(5); //5 min
        System.out.println("Delvist korrekt knap blev trykket: Kortet visses tidligst om 5 min igen");
        Platform.runLater(() -> {
            controller.incrementCorrectCount();
            cardChangeListener.onNextCard();
        });
    }

    public void notCorrectButtonPressed(){
        scheduleReview(1); // 1 min
        System.out.println("Ikke korrekt knap blev trykket: Kortet visses tidligst om 1 min igen" );
        Platform.runLater(() -> {
            controller.incrementCorrectCount();
            cardChangeListener.onNextCard();
        });
    }



    private void scheduleReview(int minutes) {
        new Thread(() -> {
            try {
                Thread.sleep(minutes * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> cardChangeListener.onNextCard());
        }).start();
    }
}
