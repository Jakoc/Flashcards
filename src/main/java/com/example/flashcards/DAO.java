package com.example.flashcards;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        int index = 0;

        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String cardId = rs.getString("card_id");
                String category = rs.getString("category");
                String question = rs.getString("question");
                String imageName = rs.getString("image_name");
                String answer = rs.getString("answer");
                String titel = rs.getString("titel");
                int year = rs.getInt("year");
                String timePeriod = rs.getString("timeperiod");
                Timestamp showTime = rs.getTimestamp("show_time");

                Cards card = new Cards(cardId, category, question, imageName, answer, titel, year, timePeriod, showTime);

                card.setShowTime(showTime);
                card.setIndex(index);

                cards.add(card);
                index++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }

    public List<Cards> importCardsfromList(String filePath) {
        List<Cards> importedCards = new ArrayList<>();
        List<String> failedCards = new ArrayList<>();

        try (BufferedReader buf = new BufferedReader(new FileReader(filePath))) {
            String lineJustFetched = null;
            while ((lineJustFetched = buf.readLine()) != null) {
                String[] wordsArray = lineJustFetched.split(",", -1);

                if (wordsArray.length >= 20) {
                    String cardID = wordsArray[0];
                    String category = wordsArray[1];
                    String imageName = wordsArray[3];
                    imageName = imageName.replaceAll("^\"|\"$", "");
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

                    String question = "Artist?";
                    Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

                    Cards card = new Cards(cardID, category, question, imageName, answer, titel, year, timePeriod, currentTimestamp);
                    card.setShowTime(currentTimestamp);

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
        String query = "INSERT INTO card (card_id, category, question, image_name, answer, titel, year, timeperiod, show_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            for (Cards card : cards) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedTimestamp = dateFormat.format(card.getShowTime());

                pstmt.setString(1, card.getCardId());
                pstmt.setString(2, card.getCategory());
                pstmt.setString(3, card.getQuestion());
                pstmt.setString(4, card.getImageName());
                pstmt.setString(5, card.getAnswer());
                pstmt.setString(6, card.getTitle());
                pstmt.setInt(7, card.getYear());
                pstmt.setString(8, card.getTimePeriod());
                pstmt.setString(9,formattedTimestamp);

                pstmt.executeUpdate(); // Udfør indsættelse i databasen
                System.out.println("Card Index: " + card.getIndex().intValue());
            }

        } catch (SQLException e) {
            System.out.println("dubleret kort kommer ikke med");
            e.printStackTrace();
        }
    }



    // tør ikke at slette denne her
    public void deleteCard(String cardId) {
        try {
            String query = "DELETE FROM card WHERE card_id IN (SELECT card_id FROM irrelevant_cards)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.executeUpdate();


            query = "DELETE FROM irrelevant_cards";
            pstmt = con.prepareStatement(query);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
