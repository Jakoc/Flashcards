package com.example.flashcards;

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

public class FlashCardController {

    private ButtonFunktion buttonFunctions = new ButtonFunktion();
    private DAO dao;

    @FXML
    private HBox svarVBox;

    @FXML
    private Label answerLabel;
    @FXML
    private ImageView flashcardImage;

public void initialize(){
    dao = new DAO();
}


    public void answerButtonPressed(ActionEvent event) {
        //gør knappen usynlig
        Button showAnswerButton = (Button) event.getSource();
        showAnswerButton.setVisible(false);
        answerLabel.setText("SVARET");
        svarVBox.setVisible(true);
        System.out.println("Vis svar knap blev trykket");



        //opretter knapper og event
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
        answerLabel.setText("SVARET");
    }
}