package com.example.flashcards;

import java.sql.*;
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

                // ... din kode til at erstatte eller håndtere specielle tegn

                Cards card = new Cards(cardId, category, question, imageName, answer, title, year, timePeriod);
                cards.add(card);
            }
        }
        return cards;
    }
}
