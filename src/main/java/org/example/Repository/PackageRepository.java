package org.example.Repository;

import org.example.DatabaseConnection;
import org.example.Model.Card;
import org.example.Model.MonsterCard;
import org.example.Model.SpellCard;
import org.example.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PackageRepository {

    // Method to create a new package in the database
    public boolean createPackage(List<Card> cards) {
        String insertCardQuery = "INSERT INTO cards (id, name, damage, element_type, card_type) VALUES (CAST(? AS uuid), ?, ?, ?, ?)";
        String insertPackageQuery = "INSERT INTO packages (is_purchased) VALUES (FALSE) RETURNING id";
        String insertPackageCardsQuery = "INSERT INTO package_cards (package_id, card_id) VALUES (?, CAST(? AS uuid))";

        try (Connection connection = DatabaseConnection.connect()) {
            connection.setAutoCommit(false);  // Start a transaction

            // Insert cards into the cards table and get their generated ids
            List<UUID> cardIds = new ArrayList<>();
            String cardtype = "spell";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertCardQuery)) {
                for (Card card : cards) {
                    if(card.isMonster()){
                        cardtype = "monster";
                    }
                    else{
                        cardtype = "spell";
                    }
                    preparedStatement.setObject(1, card.getId());  // Card id
                    preparedStatement.setString(2, card.getName());
                    preparedStatement.setDouble(3, card.getDamage());
                    preparedStatement.setString(4, card.getElementType().toString());
                    preparedStatement.setString(5, cardtype);
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Error while inserting cards: " + e.getMessage());
                return false;
            }

            // Insert a new package into the packages table
            UUID packageId = null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertPackageQuery)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    packageId = UUID.fromString(resultSet.getString("id"));
                }
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Error while inserting package: " + e.getMessage());
                return false;
            }

            // Insert into the package_cards table to relate the package and cards
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertPackageCardsQuery)) {
                for (Card card : cards) {
                    preparedStatement.setObject(1, packageId);  // Set package_id
                    preparedStatement.setObject(2,card.getId());  // Set card_id
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
                connection.commit();  // Commit the transaction
                return true;
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Error while inserting cards into package: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }


    public List<Card> buyPackage(String username) {
        List<Card> cards = new ArrayList<>();
        String selectPackageQuery = """
        SELECT p.id
        FROM packages p
        WHERE p.is_purchased = FALSE
    """;

        // Query to get the cards associated with the selected package
        String fetchCardsQuery = """
    Select c.* from cards c 
    inner join package_cards pc 
    on c.id = pc.card_id where pc.package_id = CAST(? AS uuid)
    """;

        // Query to update the owner of a card
        String updateCardOwnerQuery = """
        UPDATE cards
        SET owner_id = ?
        WHERE id = CAST(? AS uuid)
    """;

        // Query to mark the package as purchased
        String markPackageAsPurchasedQuery = """
        UPDATE packages
        SET is_purchased = TRUE
        WHERE id = CAST(? AS uuid)
    """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement selectPackageStatement = connection.prepareStatement(selectPackageQuery);
             PreparedStatement fetchCardsStatement = connection.prepareStatement(fetchCardsQuery);
             PreparedStatement updateCardOwnerStatement = connection.prepareStatement(updateCardOwnerQuery);
             PreparedStatement markPackageAsPurchasedStatement = connection.prepareStatement(markPackageAsPurchasedQuery)) {

            // Step 1: Select the first package that has not been purchased yet
            ResultSet resultSet = selectPackageStatement.executeQuery();
            if (resultSet.next()) {
                String packageId = resultSet.getString("id");
                System.out.println("id : " + packageId);
                // Step 2: Fetch all cards associated with this package
                fetchCardsStatement.setString(1, packageId);
                ResultSet cardResultSet = fetchCardsStatement.executeQuery();
                System.out.println("end of card query");
                // Add cards to the list
                while (cardResultSet.next()) {
                    Card card = createCardFromResultSet(cardResultSet);
                    if (card != null) {
                        cards.add(card);
                        System.out.println("card_id : " + card.getId());
                    }
                }

                // Step 3: Update the owner_id for each card in the list
                for (Card card : cards) {
                    updateCardOwnerStatement.setString(1, username);  // Set the owner_id to the username
                    updateCardOwnerStatement.setObject(2, card.getId()); // Set the card's id
                    updateCardOwnerStatement.executeUpdate();
                }

                // Step 4: Mark the package as purchased
                markPackageAsPurchasedStatement.setString(1, packageId);
                markPackageAsPurchasedStatement.executeUpdate();

            } else {
                System.err.println("No package available.");
            }
        } catch (SQLException e) {
            System.err.println("Error while buying package: " + e.getMessage());
        }

        return cards;
    }







    // Helper method to create a Card object from a ResultSet
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
