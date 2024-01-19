package com.example.flashcards;

import javafx.application.Platform;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.PriorityQueue;
import java.util.Comparator;


public class FlashCardScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final FlashCardController controller;
    private Integer nextCardIndex = null;
    private boolean userManuallySwitching = false;
    PriorityQueue<Cards> cardQueue = new PriorityQueue<>(Comparator.comparing(Cards::getShowTime));

    private DAO dao;


    public FlashCardScheduler(FlashCardController controller, DAO dao) {
        this.controller = controller;
        this.dao = dao;
    }

    //planlægger en opgave
    public void schedule(Runnable task, long delay, TimeUnit timeUnit) {
        scheduler.schedule(task, delay, timeUnit);
    }

    //planlægger visning af det næste kort fra køen med en given forsinkelse i dage.
    public void startDisplayingCards() {
        final Runnable checker = new Runnable() {
            public void run() {
                if (!cardQueue.isEmpty() && isCardReadyToShow(cardQueue.peek())) {
                    Cards nextCard = cardQueue.poll();
                    Platform.runLater(() -> {
                        controller.showCardAtIndex(nextCard.getIndex());  // Vis kortet på FX tråd
                    });
                }
            }
        };

        // Kører checker hver 1 sekund for at se, om der er kort, der er klar til at blive vist
        scheduler.scheduleAtFixedRate(checker, 0, 1, TimeUnit.SECONDS);
    }

    //planlægger visning af det næste kort fra køen.
    public void scheduleNextCardFromQueue() {
        if (!cardQueue.isEmpty()) {
            Cards nextCard = cardQueue.peek();

            if (!userManuallySwitching || isCardReadyToShow(nextCard)) {
                long delay = getDelay(nextCard);

                if (delay <= 0) {
                    controller.showCardAtIndex(nextCard.getIndex());
                    cardQueue.poll();
                } else if (!userManuallySwitching) {
                    schedule(this::scheduleNextCardFromQueue, delay, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    //beregner forsinkelsen i millisekunder mellem nuværende tidspunkt og kortets visningstidspunkt.
    private long getDelay(Cards card) {
        long currentTime = System.currentTimeMillis();
        long showTime = card.getShowTime().getTime();
        return Math.max(0, showTime - currentTime);
    }
    private boolean isCardReadyToShow(Cards card) {
        return card.getShowTime().before(new Timestamp(System.currentTimeMillis()));
    }

    public void setNextCardIndex(int cardIndex) {
        this.nextCardIndex = cardIndex;
    }

    //returnere all kortene
    public List<Cards> getAllCards() {
        try {
            return dao.getAllCards();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    // tilføjer kort til køen
    public void addCardToQueue(Cards card) {
        cardQueue.add(card);
    }


    public PriorityQueue<Cards> getCardQueue() {
        return cardQueue;
    }

    // printer kort fra index og show_time værdien
    /*
    public void printCardQueueContents() {
        for (Cards card : cardQueue) {
            System.out.println("Card Index: " + card.getIndex());
            System.out.println("Show Time: " + card.getShowTime());

        }
    }
    */

    public void setUserManuallySwitching(boolean manuallySwitching) {
        this.userManuallySwitching = manuallySwitching;
    }
}