package com.example.flashcards;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DAO {
    private Connection con;

    public DAO() {
        try {
            con = DriverManager.getConnection("jdbc:sqlserver://10.176.111.34:1433;database=JacobFlashcard;userName=CSe2023t_t_6;password=CSe2023tT6#23;encrypt=true;trustServerCertificate=true");
        } catch (SQLException e) {
            System.err.println("Can't connect to Database: " + e.getErrorCode() + e.getMessage());
        }
        System.out.println("Forbundet til databasen... ");
    } //opret forbindelse til db og catch exceptions hvis det ikke kan lade sig gøre

    public List<Cards> getAllCards() throws SQLException {
        List<Cards> cards = new ArrayList<>();
        String query = "SELECT * FROM card";

        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String cardId = rs.getString("card_id");
                String category = rs.getString("category");
                String question = rs.getString("question");
                String imageName = rs.getString("image_name");
                String answer = rs.getString("answer");
                String title = rs.getString("titel");
                int year = rs.getInt("year");
                String timePeriod = rs.getString("timeperiod");



                Cards card = new Cards(cardId, category, question, imageName, answer, title, year, timePeriod);
                cards.add(card);
            }
        }
        return cards;
    }

    public List<Cards> importCardsfromList(String filePath) {
        List<Cards> importedCards = new ArrayList<>();
        List<String> failedCards = new ArrayList<>();

        try (BufferedReader buf = new BufferedReader(new FileReader(filePath))) {
            String lineJustFetched = null;
            while ((lineJustFetched = buf.readLine()) != null) {
                String[] wordsArray = lineJustFetched.split("\t");

                if (wordsArray.length >= 8) {
                    String cardID = wordsArray[0];
                    String category = wordsArray[1];
                    String imageName = wordsArray[3];
                    imageName = imageName.substring(imageName.indexOf("\"") + 1, imageName.lastIndexOf("\""));
                    String answer = wordsArray[4];
                    String titel = wordsArray[5];
                    String timePeriod = wordsArray[8];
                    String yearStr = wordsArray[7];
                    int year;
                    try {
                        year = Integer.parseInt(yearStr);
                    } catch (NumberFormatException e) {
                        failedCards.add(lineJustFetched);
                        continue;
                    }



                    String question = "Artist?"; // Set din spørgsmålstekst her

                    Cards card = new Cards(cardID, category, question, imageName, answer, titel, year, timePeriod);
                    importedCards.add(card);
                } else {
                    failedCards.add(lineJustFetched);
                }
            }
        } catch (FileNotFoundException ex){
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        System.out.println("Kort med fejl: ");
        for (String failedCard : failedCards){
            System.out.println(failedCard);
        }

        // Indsæt kortene i databasen
        insertCards(importedCards);

        return importedCards;
    }
    public void insertCards(List<Cards> cards) {
        String query = "INSERT INTO card (card_id, category, question, image_name, answer, titel, year, timeperiod) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            for (Cards card : cards) {

                pstmt.setString(1, card.getCardId());
                pstmt.setString(2, card.getCategory());
                pstmt.setString(3, card.getQuestion());
                pstmt.setString(4, card.getImageName());
                pstmt.setString(5, card.getAnswer());
                pstmt.setString(6, card.getTitle());
                pstmt.setInt(7, card.getYear());
                pstmt.setString(8, card.getTimePeriod());

                pstmt.executeUpdate(); // Udfør indsættelse i databasen
            }
        } catch (SQLException e) {
            System.out.println("dubleret kort kommer ikke med");
        }
    }
}
