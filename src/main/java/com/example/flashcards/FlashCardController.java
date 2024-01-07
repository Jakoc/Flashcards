package com.example.flashcards;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class FlashCardController implements CardChangeListener {

    private ButtonFunktion buttonFunctions = new ButtonFunktion(this);
    private DAO dao;
    private List<Cards> allCards;
    private Random random = new Random();
    @FXML
    private HBox svarVBox;

    private Button showAnswerButton;
    @FXML
    private Label answerLabel, questionLabel, infoLabel;
    @FXML
    private ImageView flashcardImage;



    public void initialize(){
        dao = new DAO();
        try {
            allCards = dao.getAllCards(); // Hent alle kort fra databasen
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (allCards != null && !allCards.isEmpty()) {
            showRandomFlashCard(); // Vis et tilfældigt kort, hvis der er kort i listen
        } else {
            System.out.println("Ingen kort fundet i databasen.");
        }
        showAnswerButton = new Button();
    }


    public void answerButtonPressed(ActionEvent event) {
        //gør knappen usynlig
        Button showAnswerButton = (Button) event.getSource();
        showAnswerButton.setVisible(false);
        answerLabel.setVisible(true);
        infoLabel.setVisible(true);
        svarVBox.setVisible(true);
        System.out.println("Vis svar knap blev trykket");



        //opretter knapper deres event, og svar tekst + info
        Button correctButton = new Button("Korrekt");
        correctButton.setOnAction(e -> buttonFunctions.correctButtonPressed());

        Button almostCorrectButton = new Button("Næsten korrekt");
        almostCorrectButton.setOnAction(e -> buttonFunctions.almostCorrectButtonPressed());

        Button partlyCorrectButton = new Button("Delvist korrekt");
        partlyCorrectButton.setOnAction(e -> buttonFunctions.partlyCorrectButtonPressed());

        Button notCorrectButton = new Button("Ikke korrekt");
        notCorrectButton.setOnAction(e -> buttonFunctions.notCorrectButtonPressed());

        svarVBox.getChildren().clear();
        svarVBox.getChildren().addAll(correctButton, almostCorrectButton, partlyCorrectButton, notCorrectButton);
        answerLabel.setText(answerLabel.getText());
        infoLabel.setText(infoLabel.getText());


    }

    @FXML
    public void showRandomFlashCard() {
        if (allCards != null && !allCards.isEmpty()) {
            int randomIndex = random.nextInt(allCards.size()); // Vælg et tilfældigt index
            Cards randomCard = allCards.get(randomIndex);

            questionLabel.setText(randomCard.getQuestion());
            answerLabel.setText(randomCard.getAnswer());

            // sti til billederen + tager et tilfældigt kort
            String imagePath = "file:C:\\Users\\damer\\IdeaProjects\\Flashcards\\src\\main\\resources\\com\\example\\flashcards\\greatartists\\" + randomCard.getImageName();
            Image image = new Image(imagePath);
            flashcardImage.setImage(image);

            //info omkring kortet
            String infoText = "" + randomCard.getTitle() + "\n" +
                    "" + randomCard.getYear() + " - " + randomCard.getTimePeriod();

            infoLabel.setText(infoText);
        }
    }
    public void onNextCard(){

        Platform.runLater(() -> {
            answerLabel.setVisible(false);
            svarVBox.setVisible(false);
            infoLabel.setVisible(false);
            showRandomFlashCard();

            showAnswerButton.setVisible(true);
        });
    }

}