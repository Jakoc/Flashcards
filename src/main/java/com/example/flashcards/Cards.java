package com.example.flashcards;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;


public class Cards {
    private String cardId;
    private String category;
    private String question;
    private String imageName;
    private String answer;
    private String titel;
    private int year;
    private String timePeriod;
    private boolean markedCorrect;
    private int index;
    private Timestamp showTime;

    public Cards(String cardId, String category, String question, String imageName, String answer, String titel, int year, String timePeriod, Timestamp showTime) {
        this.cardId = cardId;
        this.category = category;
        this.question = question;
        this.imageName = imageName;
        this.answer = answer;
        this.titel = titel;
        this.year = year;
        this.timePeriod = timePeriod;
        this.showTime = showTime;
        this.markedCorrect = false;

    }


    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getTitle() {
        return titel;
    }

    public void setTitle(String title) {
        this.titel = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Timestamp getShowTime() {
        return showTime;
    }

    public void setShowTime(Timestamp showTime) {
        this.showTime = showTime;
    }

    public void setNextShowTimeBasedOnButton(String buttonPressed) {
        long currentTime = System.currentTimeMillis();
        switch (buttonPressed) {
            case "korrekt":
                this.showTime = new Timestamp(currentTime + TimeUnit.DAYS.toMillis(4));
                break;
            case "næsten korrekt":
                this.showTime = new Timestamp(currentTime + TimeUnit.MINUTES.toMillis(10));
                break;
            case "delvist korrekt":
                this.showTime = new Timestamp(currentTime + TimeUnit.MINUTES.toMillis(5));
                break;
            case "ikke korrekt":
                this.showTime = new Timestamp(currentTime + TimeUnit.MINUTES.toMillis(1));
                break;
            default:
                // Håndter fejl eller ukendt knap
                break;
        }
    }
}

