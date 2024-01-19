package com.example.flashcards;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import java.time.LocalDateTime;
import java.io.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class FlashCardController {

    private ButtonFunktion buttonFunctions;
    private DAO dao;
    List<Cards> allCards = new ArrayList<>();
    private FlashCardScheduler cardScheduler;
    public boolean userManuallySwitching = false;
    private long elapsedTime;

    @FXML
    private HBox svarVBox, svarKnap;
    @FXML
    private Button correctButton, almostCorrectButton, partlyCorrectButton, notCorrectButton;
    @FXML
    private Button showAnswerButton, irrelevantButton,restartButton, addCardButton, finishButton;
    @FXML
    private Label answerLabel, questionLabel, infoLabel;
    @FXML
    private Label allCardsLabel, cardsLeftLabel, correctCardsLabel, almostCorrectLabel, partlyCorrectLabel, notCorrectLabel, timerLabel;
    @FXML
    private ImageView flashcardImage;
    @FXML
    private ToggleButton modeSwitch;
    @FXML
    private AnchorPane appPane;
    private int lastActiveCardIndex = -1;
    private String lastButtonPressed = null;
    private int currentCardIndex;
    private final String stateFilePath = "state.properties";
    private int correctCount;
    private int almostCorrectCount;
    private int partlyCorrectCount;
    private int notCorrectCount;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");

    //hvad der ske ved opstart
    public void initialize() {
        //opretter en ny dao for at få adgang til db
        dao = new DAO();
        try {
            allCards = dao.getAllCards(); // Hent alle kort fra databasen
            importAndInsertCardsFromFile();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //laver en ny scheduler og forbinder med controller og dao
        cardScheduler = new FlashCardScheduler(this, dao);
        setCardScheduler(cardScheduler);
        cardScheduler.setNextCardIndex(0);
        // Start visning af kort
        cardScheduler.startDisplayingCards();
        // planlægger visning af næste kort fra køen
        cardScheduler.scheduleNextCardFromQueue();
        buttonFunctions = new ButtonFunktion(this, cardScheduler);
        //loader appen fra hvor man lukkede det sidst
        loadState();
        //opdatere label og timer
        updateLabel();
        startTimer();
        //viser det kort, der sidst blev vist
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
    //importere og indsæt kortene fra filerne
    public void importAndInsertCardsFromFile() {
        try {
            List<Cards> importedCards = dao.importCardsfromList("C:\\Users\\damer\\Downloads\\Great Works of Art__Artists.txt");

            if (importedCards != null && !importedCards.isEmpty()) {
                for (int i = 0; i < importedCards.size(); i++) {
                    Cards card = importedCards.get(i);
                    card.setIndex(i);
                }
                dao.insertCards(importedCards);
            } else {
                System.out.println("Ingen kort blev importeret fra filen.");
            }
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }

    public void showCardAtIndex(int index) {
        // Tjek om kortene er ikke-tomme, og om det givne indeks er gyldigt
        if (!allCards.isEmpty() && index >= 0 && index < allCards.size()) {
            // Hent det næste kort baseret på indeks
            Cards nextCard = allCards.get(index);

            // Opdater labels med kortets spørgsmål og svar
            questionLabel.setText(nextCard.getQuestion());
            answerLabel.setText(nextCard.getAnswer());

            // billede fil og viser billede til kort
            String imagePath = "file:C:\\Users\\damer\\IdeaProjects\\Flashcards\\src\\main\\resources\\com\\example\\flashcards\\greatartists\\" + nextCard.getImageName();
            Image image = new Image(imagePath);
            flashcardImage.setImage(image);

            //information tekst
            String infoText = "" + nextCard.getTitle() + "\n" +
                    "" + nextCard.getYear() + " - " + nextCard.getTimePeriod();
            infoLabel.setText(infoText);

            // Hvis brugeren manuelt skifter kort, opdater det aktuelle kortindeks og gem tilstanden
            if (userManuallySwitching) {
                currentCardIndex = index;
                System.out.println("Viser kort på position: " + currentCardIndex);
                saveState();
            }

        } else {
            System.out.println("Ingen kort");
        }
        // Opdater det sidst aktive kortindeks
        lastActiveCardIndex = index;
    }
    //sætter kort køen
    public void setCardScheduler(FlashCardScheduler cardScheduler) {
        this.cardScheduler = cardScheduler;
    }
    //sætter det nuværende kort index
    public void setCurrentCardIndex(int index) {
        this.currentCardIndex = index;
    }
    //for det nuværende kort index
    public int getCurrentCardIndex() {
        return currentCardIndex;
    }

    //fjerne vis svar knap og sætter svar text op samt vurderings knapper
    public void answerButtonPressed(ActionEvent event) {
        //gør knappen usynlig
        Button showAnswerButton = (Button) event.getSource();
        showAnswerButton.setVisible(false);
        answerLabel.setVisible(true);
        infoLabel.setVisible(true);
        svarVBox.setVisible(true);
        System.out.println("Vis svar knap blev trykket");

        Cards currentCard = getCurrentCard();

        //opretter knapper deres event, og svar tekst + info
        Button correctButton = new Button("Korrekt");
        correctButton.setOnAction(e -> buttonFunctions.correctButtonPressed(currentCard));


        Button almostCorrectButton = new Button("Næsten korrekt");
        almostCorrectButton.setOnAction(e -> buttonFunctions.almostCorrectButtonPressed(currentCard));

        Button partlyCorrectButton = new Button("Delvist korrekt");
        partlyCorrectButton.setOnAction(e -> buttonFunctions.partlyCorrectButtonPressed(currentCard));

        Button notCorrectButton = new Button("Ikke korrekt");
        notCorrectButton.setOnAction(e -> buttonFunctions.  notCorrectButtonPressed(currentCard));

        svarVBox.getChildren().clear();
        svarVBox.getChildren().addAll(correctButton, almostCorrectButton, partlyCorrectButton, notCorrectButton);
        answerLabel.setText(answerLabel.getText());
        infoLabel.setText(infoLabel.getText());
        saveState();


    }
    //for det nuværende kort, hvis det er et
    public Cards getCurrentCard() {
        if (!allCards.isEmpty() && currentCardIndex >= 0 && currentCardIndex < allCards.size()) {
            return allCards.get(currentCardIndex);
        }
        return null;
    }


    @FXML
    public void onNextCard(ActionEvent event) {
        Platform.runLater(() -> {
            // Fjern label og tilføj ny knap igen
            answerLabel.setVisible(false);
            svarVBox.setVisible(false);
            infoLabel.setVisible(false);
            showAnswerButton.setVisible(true);

            // Hent det næste kort fra køen, hvis det er klar til at blive vist
            int nextCardIndex = calculateNextCardIndexBasedOnUserAction();
            showCardAtIndex(nextCardIndex);
            saveState();
        });
    }

    int calculateNextCardIndexBasedOnUserAction() {
        int nextCardIndex = currentCardIndex; // Start med det nuværende kortindeks
        // Tjek hvilken knap der blev trykket sidst og justér indekset baseret på det
        if (lastButtonPressed != null) {
            switch (lastButtonPressed) {
                case "correct":
                    nextCardIndex += 4;
                    break;
                case "almostCorrect":
                    nextCardIndex += 3;
                    break;
                case "partlyCorrect":
                    nextCardIndex += 2;
                    break;
                case "notCorrect":
                    nextCardIndex += 1;
                    break;
            }
        }

        // Hvis vi er nået forbi sidste kort, start forfra
        if (nextCardIndex >= allCards.size()) {
            nextCardIndex = nextCardIndex % allCards.size();
        }

        return nextCardIndex;
    }
    //tjekker om kort er klar til at blive vist, det er det vist kortets show time er ovre
    boolean isCardReadyToShow(Cards card) {
        return card.getShowTime() != null && card.getShowTime().before(new Timestamp(System.currentTimeMillis()));
    }
    //alert box hvis man er færdig
    void setIsComplete() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Færdig!");
        alert.setHeaderText(null);
        alert.setContentText("Du er færdig med sættet. Nu må du se fjernsyn");
        alert.showAndWait();
    }

    //restart knap sætter alt til det udgangspunkt som ved opstart af app og viser det første kort i index
    public void RestartButtonPressed(ActionEvent event) {
        userWantsToRestart();
        currentCardIndex = 0;
        correctCount = 0;
        almostCorrectCount = 0;
        partlyCorrectCount = 0;
        notCorrectCount = 0;

        showAnswerButton.setVisible(true);
        svarVBox.setVisible(false);
        answerLabel.setVisible(false);
        infoLabel.setVisible(false);
        updateTimerLabel();
        resetElapsedTime();
        updateLabel();
        if (!allCards.isEmpty()) {
            showCardAtIndex(currentCardIndex);
        } else {
            System.out.println("Ingen kort fundet");
        }
    }
    //alert box til at bekræfte at man starte forfra
    private boolean userWantsToRestart() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Afslut eller genstart");
        alert.setHeaderText(null);
        alert.setContentText("Er du sikker på at du vil genstarte?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    //hvis man trykker færdig knap
    public void finishButtonPressed(ActionEvent event) {
        System.out.println("Hold da op du er hurtig færdig");
        setIsComplete();
    }

    private void saveState() {
        //laver en property til at gemme tilstanden af appen
        Properties properties = new Properties();
        //property for det aktuelle kortindex og tællere
        properties.setProperty("currentCardIndex", String.valueOf(currentCardIndex));
        properties.setProperty("correctCount", String.valueOf(correctCount));
        properties.setProperty("almostCorrectCount", String.valueOf(almostCorrectCount));
        properties.setProperty("partlyCorrectCount", String.valueOf(partlyCorrectCount));
        properties.setProperty("notCorrectCount", String.valueOf(notCorrectCount));

        try (OutputStream output = new FileOutputStream(stateFilePath)) {
            // Gem egenskaberne til tilstandsfilen
            properties.store(output, "Flashcard State");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("Gemmer app");
    }
    //metoden til at loade appen fra hvor det blev sluttet
    private void loadState() {
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
    //+1 til korrekt label
    public void incrementCorrectCount() {
        correctCount++;
        updateLabel();
    }
    //-1 til korrekt label
    public void decrementCorrectCount() {
        correctCount--;
        updateLabel();
    }
    //+1 til næsten korrekt label
    public void incrementAlmostCorrectCount() {
        almostCorrectCount++;
        updateLabel();
    }
    //-1 til næsten korrekt label
    public void decrementAlmostCorrectCount() {
        almostCorrectCount--;
        updateLabel();
    }
    //+1 til delvist korrekt label
    public void incrementPartlyCorrectCount() {
        partlyCorrectCount++;
        updateLabel();
    }
    //-1 til delvist korrekt label
    public void decrementPartlyCorrectCount() {
        partlyCorrectCount--;
        updateLabel();
    }
    //+1 til ikke korrekt label
    public void incrementNotCorrectCount() {
        notCorrectCount++;
        updateLabel();
    }
    //-1 til ikke korrekt label
    public void decrementNotCorrectCount() {
        notCorrectCount--;
        updateLabel();
    }
    //metoden til hvad der står i de forskellige labels + udringen til dem
    public void updateLabel() {
        allCardsLabel.setText("Fulde antal af kort: " + allCards.size());
        cardsLeftLabel.setText("Manglende kort: " + (allCards.size() - (correctCount + almostCorrectCount + partlyCorrectCount + notCorrectCount)));
        correctCardsLabel.setText("Korrekte antal kort: " + correctCount);
        almostCorrectLabel.setText("Næsten korrekte antal kort: " + almostCorrectCount);
        partlyCorrectLabel.setText("Delvist ikke korrekte kort: " + partlyCorrectCount);
        notCorrectLabel.setText("Ikke korrekte kort: " + notCorrectCount);
    }
    //hvis man trykker på irrelevant knappen, så fjerner den det kort og opdatere labels så de passer
    public void IrrelevantButtonPressed(ActionEvent event) {
        if (!allCards.isEmpty() && currentCardIndex >= 0 && currentCardIndex < allCards.size()) {
            Cards irrelevantCard = allCards.get(currentCardIndex);
            dao.deleteCard(irrelevantCard.getCardId());
            System.out.println("Irrelevant Card:");
            System.out.println(irrelevantCard.toString());
            allCards.remove(irrelevantCard);

            if (!allCards.isEmpty()) {
                onNextCard(event);
            } else {

                System.out.println("Ingen flere kort tilbage.");

            }
        } else {
            System.out.println("Ingen kort at fjerne.");
        }
        updateLabel();
    }
    //Laver et dialog som man kan skrive i. Her kan man id og showtime giver det selv, resten skal man selv udfylde
    public void addCardButtonPressed(ActionEvent event) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tilføj nyt kort");
        dialog.setHeaderText("Udfyld kortoplysninger:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        TextField questionField = new TextField();
        questionField.setPromptText("Question");

        TextField imageNameField = new TextField();
        imageNameField.setPromptText("Image Name");

        TextField answerField = new TextField();
        answerField.setPromptText("Answer");

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField timePeriodField = new TextField();
        timePeriodField.setPromptText("Time Period");

        TextField yearField = new TextField();
        yearField.setPromptText("Year");

        grid.add(new Label("Category:"), 0, 0);
        grid.add(categoryField, 1, 0);

        grid.add(new Label("Question:"), 0, 1);
        grid.add(questionField, 1, 1);

        grid.add(new Label("Image Name:"), 0, 2);
        grid.add(imageNameField, 1, 2);

        grid.add(new Label("Answer:"), 0, 3);
        grid.add(answerField, 1, 3);

        grid.add(new Label("Title:"), 0, 4);
        grid.add(titleField, 1, 4);

        grid.add(new Label("Time Period:"), 0, 5);
        grid.add(timePeriodField, 1, 5);

        grid.add(new Label("Year:"), 0, 6);
        grid.add(yearField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            // Hent kortoplysninger fra dialogens tekstfelter
            String category = categoryField.getText();
            String question = questionField.getText();
            String imageName = imageNameField.getText();
            String answer = answerField.getText();
            String title = titleField.getText();
            String timePeriod = timePeriodField.getText();
            int year;
            try {
                year = Integer.parseInt(yearField.getText()); // Brug yearField.getText() i stedet for bare 'year'
            } catch (NumberFormatException e) {
                System.err.println("Fejl: Årstal er ikke et gyldigt tal!");
                return;
            }
            //giver kortet et id
            String generatedCardId = UUID.randomUUID().toString();


            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

            Cards newCard = new Cards(generatedCardId, category, question, imageName, answer, title, year, timePeriod, currentTimestamp);

            LocalDateTime nextShowTime = currentTimestamp.toLocalDateTime().plusMinutes(1);
            newCard.setShowTime(Timestamp.valueOf(nextShowTime));
            // Indsæt det nye kort i databasen ved hjælp af DAO
            dao.insertCards(Collections.singletonList(newCard));
        }
    }
    //opdatere timer label
    public void updateTimerLabel() {
        String formattedTime = timeFormat.format(elapsedTime);
        Platform.runLater(() -> {
            timerLabel.setText("Tid gået på kort: " + formattedTime);
        });
    }
    //reset tiden
    public void resetElapsedTime() {
        elapsedTime = 0;
    }
    //starter tiden
    public void startTimer() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); // Opdater tid hvert sekund
                    elapsedTime += 1000;
                    updateTimerLabel(); // Opdater tidsetiketten
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //håndtere når man trykker på tema knappen om den skal skifte til dark mode eller light mode
    @FXML
    private void handleModeSwitch(ActionEvent event) {
        Scene currentScene = modeSwitch.getScene();

        if (modeSwitch.isSelected()) {
            // Skift til dark mode
            if (currentScene != null) {
                currentScene.getStylesheets().clear();
                currentScene.getStylesheets().add(getClass().getResource("/com/example/flashcards/dark-mode.css").toExternalForm());
            }
        } else {
            // Skift til light mode
            if (currentScene != null) {
                currentScene.getStylesheets().clear();
                currentScene.getStylesheets().add(getClass().getResource("/com/example/flashcards/light-mode.css").toExternalForm());
            }
        }
        updateStyles();
        saveState();
    }

    //hvad der opdateres når man skifter tema
    private void updateStyles() {
        if (modeSwitch.isSelected()) {
            // Skift til dark mode
            svarVBox.getStyleClass().setAll("dark-mode-hbox");
            correctButton.getStyleClass().setAll("dark-mode-button");
            almostCorrectButton.getStyleClass().setAll("dark-mode-button");
            partlyCorrectButton.getStyleClass().setAll("dark-mode-button");
            notCorrectButton.getStyleClass().setAll("dark-mode-button");
            irrelevantButton.getStyleClass().setAll("dark-mode-button");
            addCardButton.getStyleClass().setAll("dark-mode-button");
            restartButton.getStyleClass().setAll("dark-mode-button");
            finishButton.getStyleClass().setAll("dark-mode-button");
            showAnswerButton.getStyleClass().setAll("dark-mode-button");
            modeSwitch.getStyleClass().setAll("dark-mode-toggle-button");
            allCardsLabel.getStyleClass().setAll("dark-mode-label");
            cardsLeftLabel.getStyleClass().setAll("dark-mode-label");
            correctCardsLabel.getStyleClass().setAll("dark-mode-label");
            almostCorrectLabel.getStyleClass().setAll("dark-mode-label");
            partlyCorrectLabel.getStyleClass().setAll("dark-mode-label");
            notCorrectLabel.getStyleClass().setAll("dark-mode-label");
            answerLabel.getStyleClass().setAll("dark-mode-label");
            infoLabel.getStyleClass().setAll("dark-mode-label");
            timerLabel.getStyleClass().setAll("dark-mode-label");
            questionLabel.getStyleClass().setAll("dark-mode-label");
        } else {
            // Skift til light mode
            svarVBox.getStyleClass().setAll("light-mode-hbox");
            correctButton.getStyleClass().setAll("light-mode-button");
            almostCorrectButton.getStyleClass().setAll("light-mode-button");
            partlyCorrectButton.getStyleClass().setAll("light-mode-button");
            notCorrectButton.getStyleClass().setAll("light-mode-button");
            irrelevantButton.getStyleClass().setAll("light-mode-button");
            addCardButton.getStyleClass().setAll("light-mode-button");
            restartButton.getStyleClass().setAll("light-mode-button");
            finishButton.getStyleClass().setAll("light-mode-button");
            showAnswerButton.getStyleClass().setAll("light-mode-button");
            modeSwitch.getStyleClass().setAll("light-mode-toggle-button");
            allCardsLabel.getStyleClass().setAll("light-mode-label");
            cardsLeftLabel.getStyleClass().setAll("light-mode-label");
            correctCardsLabel.getStyleClass().setAll("light-mode-label");
            almostCorrectLabel.getStyleClass().setAll("light-mode-label");
            partlyCorrectLabel.getStyleClass().setAll("light-mode-label");
            notCorrectLabel.getStyleClass().setAll("light-mode-label");
            answerLabel.getStyleClass().setAll("light-mode-label");
            infoLabel.getStyleClass().setAll("light-mode-label");
            timerLabel.getStyleClass().setAll("light-mode-label");
            questionLabel.getStyleClass().setAll("light-mode-label");
        }
    }
}