package org.example.Repository;

import org.example.Model.Card;
import org.example.Model.Deck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardRepository {
    private Map<String, List<Card>> userCards = new HashMap<>();
    private Map<String, Deck> userDecks = new HashMap<>();

    public List<Card> getUserCardsByToken(String token) {
        return userCards.get(token);
    }

    public Deck getUserDeckByToken(String token) {
        return userDecks.get(token);
    }

    public boolean configureDeck(String token, String[] cardIds) {
        List<Card> cards = userCards.get(token);

        if (cards == null || cards.isEmpty()) {
            return false;
        }

        Deck deck = new Deck();
        for (String cardId : cardIds) {
            cards.stream()
                    .filter(card -> card.getId().equals(cardId))
                    .findFirst()
                    .ifPresent(deck::addCard);
        }

        if (deck.getCards().size() == 4) {
            userDecks.put(token, deck);
            return true;
        }
        return false;
    }
}
