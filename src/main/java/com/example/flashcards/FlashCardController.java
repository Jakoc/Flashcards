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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class FlashCardController implements CardChangeListener {

    private ButtonFunktion buttonFunctions = new ButtonFunktion(this, this);
    private DAO dao;
    private List<Cards> allCards = new ArrayList<>();
    private Random random = new Random();
    @FXML
    private HBox svarVBox, svarKnap;

    @FXML
    private Button showAnswerButton;
    @FXML
    private Label answerLabel, questionLabel, infoLabel;
    @FXML
    private Label allCardsLabel, cardsLeftLabel, correctCardsLabel, almostCorrectLabel, partlyCorrectLabel, notCorrectLabel;
    @FXML
    private ImageView flashcardImage;
    private int currentCardIndex;
    private final String stateFilePath = "state.properties";
    private int correctCount;
    private int almostCorrectCount;
    private int partlyCorrectCount;
    private int notCorrectCount;


    public void initialize() {
        dao = new DAO();
        try {
            allCards = dao.getAllCards(); // Hent alle kort fra databasen
            importAndInsertCardsFromFile();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadState(); // Indlæs den gemte tilstand. Fjern og kør programmet 2 gange for at reset
        updateLabel();
        if (allCards != null && !allCards.isEmpty()) {
            if (currentCardIndex >= 0 && currentCardIndex < allCards.size()) {
                showCardAtIndex(currentCardIndex); // Vis det kort, der var vist sidst
            } else {
                System.out.println("Ugyldigt kortindeks.");
            }
        } else {
            System.out.println("Ingen kort fundet i databasen.");
        }
    }

    public void importAndInsertCardsFromFile() {
        try {
            List<Cards> importedCards = dao.importCardsfromList("C:\\Users\\damer\\Downloads\\Great Works of Art__Artists.txt");

            if (importedCards != null && !importedCards.isEmpty()) {
                dao.insertCards(importedCards); // Indsæt kortene i databasen via DAO
            } else {
                System.out.println("Ingen kort blev importeret fra filen.");
            }
        } catch (RuntimeException ex) {
            ex.printStackTrace(); // Udskriv eventuelle undtagelser, der kan opstå
        }
    }

    private void updateUI(List<Cards> newCards) {
        if (newCards != null && !newCards.isEmpty()) {
            currentCardIndex = 0; // Nulstil indeks til det første kort
            showCardAtIndex(currentCardIndex);
        } else {
            System.out.println("Ingen kort at vise i brugergrænsefladen.");
        }
    }

    private void showCardAtIndex(int index) {
        if (!allCards.isEmpty() && index >= 0 && index < allCards.size()) {
            Cards nextCard = allCards.get(index);

            questionLabel.setText(nextCard.getQuestion());
            answerLabel.setText(nextCard.getAnswer());

            String imagePath = "file:C:\\Users\\damer\\IdeaProjects\\Flashcards\\src\\main\\resources\\com\\example\\flashcards\\greatartists\\" + nextCard.getImageName();
            Image image = new Image(imagePath);
            flashcardImage.setImage(image);

            String infoText = "" + nextCard.getTitle() + "\n" +
                    "" + nextCard.getYear() + " - " + nextCard.getTimePeriod();

            infoLabel.setText(infoText);
            currentCardIndex = index;
            saveState();
        } else {
            System.out.println("Ingen kort");
        }
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

    private void showRandomCard() {
        if (!allCards.isEmpty()) {
            int randomIndex = new Random().nextInt(allCards.size());
            showCardAtIndex(randomIndex);
        } else {
            System.out.println("Ingen kort tilgængelige.");
        }
    }
    @FXML
    public void onNextCard(){

        Platform.runLater(() -> {
            //fjerne label og tilføjer ny knap igen
            answerLabel.setVisible(false);
            svarVBox.setVisible(false);
            infoLabel.setVisible(false);
            showAnswerButton.setVisible(true);

            if (currentCardIndex < allCards.size() - 1) { // Kontroller om der er flere kort tilbage
                currentCardIndex++; // Gå til næste kort
                showCardAtIndex(currentCardIndex); // Vis det næste kort
            } else {
                System.out.println("Ingen flere kort tilbage."); // Hvis der ikke er flere kort tilbage
            }

        });
    }

    private void saveState() {
        Properties properties = new Properties();
        properties.setProperty("currentCardIndex", String.valueOf(currentCardIndex));
        properties.setProperty("correctCount", String.valueOf(correctCount));
        properties.setProperty("almostCorrectCount", String.valueOf(almostCorrectCount));
        properties.setProperty("partlyCorrectCount", String.valueOf(partlyCorrectCount));
        properties.setProperty("notCorrectCount", String.valueOf(notCorrectCount));

        try (OutputStream output = new FileOutputStream(stateFilePath)) {
            properties.store(output, "Flashcard State");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Gemmer app");
    }
    private void loadState(){
        Properties properties = new Properties();

        try (InputStream input = new FileInputStream(stateFilePath)) {
            properties.load(input);
            String index = properties.getProperty("currentCardIndex");
            if (index != null) {
                currentCardIndex = Integer.parseInt(index);
            }

            correctCount = Integer.parseInt(properties.getProperty("correctCount", "0"));
            almostCorrectCount = Integer.parseInt(properties.getProperty("almostCorrectCount", "0"));
            partlyCorrectCount = Integer.parseInt(properties.getProperty("partlyCorrectCount", "0"));
            notCorrectCount = Integer.parseInt(properties.getProperty("notCorrectCount", "0"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Indlæser app");
    }
    public void incrementCorrectCount() {
        correctCount++;
        updateLabel();
    }

    public void incrementAlmostCorrectCount() {
        almostCorrectCount++;
        updateLabel();
    }

    public void incrementPartlyCorrectCount() {
        partlyCorrectCount++;
        updateLabel();
    }

    public void incrementNotCorrectCount() {
        notCorrectCount++;
        updateLabel();
    }
    private void updateLabel(){
        allCardsLabel.setText("Fulde antal af kort: " + allCards.size());
        cardsLeftLabel.setText("Manglende kort: " + (allCards.size() - (correctCount + almostCorrectCount + partlyCorrectCount + notCorrectCount)));
        correctCardsLabel.setText("Korrekte antal kort: " + correctCount);
        almostCorrectLabel.setText("Næsten korrekte antal kort: " + almostCorrectCount);
        partlyCorrectLabel.setText("Delvist ikke korrekte kort: " + partlyCorrectCount);
        notCorrectLabel.setText("Ikke korrekte kort: " + notCorrectCount);
    }
}