package com.example.flashcards;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ButtonFunktion {
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public void correctButtonPressed(){
        scheduleReview(4 * 24 * 60, TimeUnit.MINUTES); //4 Dage
        System.out.println("korrekt knap blev trykket");
    }

    public void almostCorrectButtonPressed(){
        scheduleReview(5, TimeUnit.MINUTES); // 5 min
        System.out.println("Næsten korrekt knap blev trykket");
    }

    public void partlyCorrectButtonPressed(){
        scheduleReview(10, TimeUnit.MINUTES); //10 min
        System.out.println("Delvist korrekt knap blev trykket");

    }

    public void notCorrectButtonPressed(){
        scheduleReview(1, TimeUnit.MINUTES); // 1 min
        System.out.println("Ikke korrekt knap blev trykket");
    }


    private void scheduleReview(int delay, TimeUnit timeUnit){
        scheduler.schedule(() -> {
            nextCard();
        }, delay, timeUnit);
    }

    public void nextCard(){
        //metode til at skifte til næste kort
    }
}
