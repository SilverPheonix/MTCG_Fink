package org.example.Repository;

import org.example.DatabaseConnection;
import org.example.Model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CardRepository {
    UserRepository userRepository = new UserRepository();

    // Method to fetch user cards by token
    public List<Card> getUserCardsByToken(String token) {
        String username = userRepository.getUsernameByToken(token);
        if (username == null) {
            return new ArrayList<>(); // Return an empty list if the token is invalid
        }

        List<Card> cards = new ArrayList<>();
        String query = """
            SELECT c.id, c.name, c.damage, c.element_type, c.card_type
            FROM cards c
            INNER JOIN users u ON c.owner_id = u.username
            WHERE u.username = ?
        """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Card card = createCardFromResultSet(resultSet);
                if (card != null) {
                    cards.add(card);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error while fetching cards for token: " + token + " - " + e.getMessage());
        }

        return cards;
    }

    // Method to fetch user's deck by token
    public Deck getUserDeckByToken(String token) {
        String username = userRepository.getUsernameByToken(token);
        if (username == null) {
            return new Deck(); // Return an empty deck if the token is invalid
        }

        Deck deck = new Deck();
        String query = """
            SELECT c.id, c.name, c.damage, c.element_type, c.card_type
            FROM decks d
            INNER JOIN cards c ON d.card_id = c.id
            INNER JOIN users u ON c.owner_id = u.username
            WHERE u.username = ?
        """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Card card = createCardFromResultSet(resultSet);
                if (card != null) {
                    deck.addCard(card);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error while fetching deck for token: " + token + " - " + e.getMessage());
        }

        return deck;
    }

    // Helper method to configure a deck
    public boolean configureDeck(String token, String[] cardIds) {
        List<Card> userCards = getUserCardsByToken(token);
        if (userCards.isEmpty()) {
            System.out.println("Deck configuration failed: No cards available for token: " + token);
            return false;
        }
        System.out.println("User cards: " + userCards);


        Deck deck = new Deck();
        for (String cardId : cardIds) {
            userCards.stream()
                    .filter(card -> {
                        boolean matches = card.getId().equals(cardId.trim());
                        return matches;
                    })
                    .findFirst()
                    .ifPresent(deck::addCard);
        }

        if (deck.getCards().size() == 4) {
            if(saveDeckToDatabase(token, deck)){
                System.out.println("Deck configured successfully for token: " + token);
                return true;
            }
            else{
                return false;
            }

        }

        System.out.println("Deck configuration failed: Only " + deck.getCards().size() + " cards were added. Expected 4 cards.");
        return false;
    }

    private boolean saveDeckToDatabase(String token, Deck deck) {
        try (Connection connection = DatabaseConnection.connect()) {
            // Start transaction
            connection.setAutoCommit(false);

            String username = userRepository.getUsernameByToken(token);
            if (username == null) {
                System.out.println("User not found for token: " + token);
                return false;
            }

            // Insert the cards for the deck
            String insertDeckQuery = "INSERT INTO decks (username, card_id) VALUES (?, CAST(? AS uuid))";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertDeckQuery)) {
                for (Card card : deck.getCards()) {
                    preparedStatement.setObject(1, username);
                    preparedStatement.setObject(2, card.getId());
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }

            // Commit the transaction
            connection.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error while saving deck: " + e.getMessage());
        }
        return false;
    }

    // Helper method to create a card from a ResultSet
    private Card createCardFromResultSet(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");
        String name = resultSet.getString("name");
        double damage = resultSet.getDouble("damage");
        String elementType = resultSet.getString("element_type");
        String cardType = resultSet.getString("card_type");

        Card.ElementType type = Card.ElementType.valueOf(elementType);

        return switch (cardType) {
            case "monster" -> new MonsterCard(id, name, damage, type);
            case "spell" -> new SpellCard(id, name, damage, type);
            default -> null; // Invalid card type
        };
    }
}
