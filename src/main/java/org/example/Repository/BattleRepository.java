package org.example.Repository;

import org.example.Model.Deck;

import java.util.HashMap;
import java.util.Map;

public class BattleRepository {
    private Map<String, Deck> decks = new HashMap<>();

    public Deck getDeckByToken(String token) {
        return decks.get(token);
    }

    public Deck findOpponentDeck() {
        // Dummy-Logik: Finde einen zufälligen Gegner
        return decks.values().stream().findAny().orElse(null);
    }

    public void addDeck(String token, Deck deck) {
        decks.put(token, deck);
    }
}
