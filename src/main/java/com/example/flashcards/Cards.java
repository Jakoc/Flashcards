package com.example.flashcards;

public class Cards {
    private String cardId;
    private String category;
    private String question;
    private String imageName;
    private String answer;
    private String title;
    private int year;
    private String timePeriod;

    public Cards(String cardId, String category, String question, String imageName, String answer, String title, int year, String timePeriod) {
        this.cardId = cardId;
        this.category = category;
        this.question = question;
        this.imageName = imageName;
        this.answer = answer;
        this.title = title;
        this.year = year;
        this.timePeriod = timePeriod;
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
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }
}

