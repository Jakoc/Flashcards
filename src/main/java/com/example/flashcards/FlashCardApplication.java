package com.example.flashcards;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FlashCardApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FlashCardApplication.class.getResource("flashcard.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/flashcards/light-mode.css").toExternalForm());
            stage.setTitle("Flashcard");
            stage.setScene(scene);
            stage.show();
            stage.setResizable(false);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {launch();
    }
}