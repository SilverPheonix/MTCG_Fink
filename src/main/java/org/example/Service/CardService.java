package org.example.Service;

import org.example.Model.Card;
import org.example.Model.Deck;
import org.example.Repository.CardRepository;

import java.util.List;

public class CardService {
    private CardRepository cardRepository =new CardRepository();

    public String getUserCards(String token) {
        // Validierung des Tokens und Abruf der Benutzerkarten
        List<Card> cards = cardRepository.getUserCardsByToken(token);

        if (cards == null || cards.isEmpty()) {
            return null;
        }

        // Karten in JSON umwandeln
        return cards.toString(); // Beispiel: Umwandlung in JSON
    }

    public String getUserDeck(String token) {
        Deck deck = cardRepository.getUserDeckByToken(token);

        if (deck == null || deck.getCards().isEmpty()) {
            return null;
        }

        return deck.toString(); // Beispiel: Umwandlung in JSON
    }

    public boolean configureDeck(String token, String payload) {
        // Payload parsen und Karten auswählen
        String[] cardIds = parsePayload(payload);

        return cardRepository.configureDeck(token, cardIds);
    }

    private String[] parsePayload(String payload) {
        // Einfaches Parsing, z.B. JSON -> Array von Card-IDs
        return payload.replace("[", "").replace("]", "").replace("\"", "").split(",");
    }
}
