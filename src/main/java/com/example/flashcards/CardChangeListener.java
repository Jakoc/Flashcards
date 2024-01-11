package com.example.flashcards;

public interface CardChangeListener {
    void onNextCard();
    void showDelayedCard(int index);
}
