package org.example.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Model.Card;
import org.example.Model.MonsterCard;
import org.example.Model.SpellCard;
import org.example.Repository.PackageRepository;
import org.example.Repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PackageService {

    private final PackageRepository packageRepository = new PackageRepository();
    private final UserRepository userRepository = new UserRepository();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public boolean createPackage(String payload, String token) throws IOException {
        // Check if the user is admin
        String username = userRepository.getUsernameByToken(token);
        if (username == null) {
            throw new IllegalArgumentException("Invalid token.");
        }
        if (!username.equals("admin")) {
            throw new SecurityException("Only the admin user can create packages.");
        }

        // Parse the payload and create the package
        List<Card> cards = parsePayloadToCards(payload);
        if (cards.size() != 5) {
            throw new IllegalArgumentException("A package must contain exactly 5 cards.");
        }

        return packageRepository.createPackage(cards);
    }

    public List<Card> buyPackage(String token) {
        // Verify user token
        String username = userRepository.getUsernameByToken(token);
        if (username == null) {
            throw new IllegalArgumentException("Invalid token.");
        }

        // Check if the user has enough coins
        if (!userRepository.hasSufficientCoins(username)) {
            throw new SecurityException("Not enough money to buy a package.");
        }

        // Fetch a package and assign it to the user
        List<Card> purchasedCards = packageRepository.buyPackage(username);

        if (purchasedCards.isEmpty()) {
            throw new IllegalStateException("No packages available for buying.");
        }

        return purchasedCards;
    }



        // Methode, um das Payload in eine Liste von Card-Objekten zu parsen
        private List<Card> parsePayloadToCards(String payload) throws IOException {
            // Jackson wird verwendet, um das JSON direkt in eine Liste von Maps zu parsen
            List<Map<String, Object>> cardDataList = objectMapper.readValue(payload, List.class);

            // Umwandlung der Map-Daten in Card-Objekte
            return cardDataList.stream()
                    .map(cardData -> createCard(cardData))
                    .toList();
        }

        // Hilfsmethode, um ein Card-Objekt basierend auf den extrahierten Daten zu erstellen
        private Card createCard(Map<String, Object> cardData) {
            String id = (String) cardData.get("Id");
            String name = (String) cardData.get("Name");
            double damage = (double) cardData.get("Damage");

            // Der Name wird genutzt, um den Card-Typ und den Elementtyp zu bestimmen
            Card.ElementType elementType = determineElementType(name);
            String cardType = determineCardType(name);

            // Erstellen des Card-Objekts basierend auf dem Typ
            if (cardType.equals("spell")) {
                return new SpellCard(id, name, damage, elementType);
            } else if (cardType.equals("monster")) {
                return new MonsterCard(id, name, damage, elementType);
            } else {
                return null; // Falls der Typ nicht erkannt wird
            }
        }

        // Hilfsmethode, um den Elementtyp basierend auf dem Namen zu bestimmen
        private Card.ElementType determineElementType(String name) {
            if (name.toLowerCase().contains("water")) {
                return Card.ElementType.WATER; // Beispiel: "Wasser" für ElementType.WATER
            } else if (name.toLowerCase().contains("fire")) {
                return Card.ElementType.FIRE; // Beispiel: "Feuer" für ElementType.FIRE
            }
            return Card.ElementType.NORMAL; // Falls kein Elementtyp gefunden wird
        }

        // Hilfsmethode, um den Kartentyp basierend auf dem Namen zu bestimmen
        private String determineCardType(String name) {
            // Beispiel: Bestimme den Kartentyp basierend auf der Namenskonvention
            if (name.toLowerCase().contains("spell")) {
                return "spell"; // Karten, die "spell" im Namen haben, werden als "spell" behandelt
            } else {
                return "monster"; // Alles andere wird als "monster" behandelt
            }
        }
    }
