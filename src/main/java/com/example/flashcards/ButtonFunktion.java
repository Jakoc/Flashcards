package com.example.flashcards;

import javafx.animation.KeyFrame;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

import java.sql.Time;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ButtonFunktion {
    private CardChangeListener cardChangeListener;
    private FlashCardController controller;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Cards lastIncorrectCard;
    private int lastIncorrectIndex;


    public ButtonFunktion(CardChangeListener listener, FlashCardController controller) {
        this.cardChangeListener = listener;
        this.controller = controller;
    }

    public void correctButtonPressed() {
        //scheduleReview(4 * 24 * 60); //4 Dage
        System.out.println("korrekt knap blev trykket: Kortet visses tidligst om 4 dage igen");

        controller.incrementCorrectCount();
        cardChangeListener.onNextCard();
        // når 4 dage er gået skal kortet vises igen og incrementCorrectCount skal falde med 1
    }

    public void almostCorrectButtonPressed() {
        //scheduleReview(10); // 10 min
        System.out.println("Næsten korrekt knap blev trykket: Kortet visses tidligst om 10 min igen");

        controller.incrementAlmostCorrectCount();
        cardChangeListener.onNextCard();
        // når 10 minut er gået skal kortet vises igen og incrementAlmostCorrectCount skal falde med 1

    }

    public void partlyCorrectButtonPressed() {
        //scheduleReview(5); //5 min
        System.out.println("Delvist korrekt knap blev trykket: Kortet visses tidligst om 5 min igen");

        controller.incrementPartlyCorrectCount();
        cardChangeListener.onNextCard();
        // når 5 minut er gået skal kortet vises igen og incrementPartlyCorrectCount skal falde med 1

    }

    public void notCorrectButtonPressed() {

        System.out.println("Ikke korrekt knap blev trykket: Skifter til næste kort med det samme");
        controller.incrementNotCorrectCount();


        lastIncorrectIndex = controller.getCurrentCardIndex();

        cardChangeListener.onNextCard();

        scheduleNotCorrect(1, TimeUnit.MINUTES);
    }

    private void scheduleNotCorrect( long delay, TimeUnit timeUnit) {
        scheduler.schedule(() -> {
            Platform.runLater(() -> {
                controller.decrementNotCorrectCount();
                controller.updateLabel();
                System.out.println("Et kort fra Ikke korrekt kan vises igen");

                if (lastIncorrectCard != null) {
                    System.out.println("Viser det sidste ikke korrekte kort igen: " + lastIncorrectCard);
                    // Her kan du bruge lastIncorrectCard til at vise det kort igen
                }

                // Nulstil variablen
                lastIncorrectCard = null;
            });
        }, delay, timeUnit);
    }

}
