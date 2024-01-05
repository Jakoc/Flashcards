module com.example.flashcards {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.flashcards to javafx.fxml;
    exports com.example.flashcards;
}