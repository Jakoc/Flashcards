package com.example.flashcards;

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
    private int repetition;
    private int interval;
    private int index;

    public Cards(String cardId, String category, String question, String imageName, String answer, String titel, int year, String timePeriod) {
        this.cardId = cardId;
        this.category = category;
        this.question = question;
        this.imageName = imageName;
        this.answer = answer;
        this.titel = titel;
        this.year = year;
        this.timePeriod = timePeriod;
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

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public int getRepetition() {
        return repetition;
    }
    public void setRepetition(int repetition) {
        this.repetition = repetition;
    }
    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
    public boolean isMarkedCorrect() {
        return markedCorrect;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

