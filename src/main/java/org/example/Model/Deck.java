package org.example.Model;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        if (cards.size() >= 4) {
            throw new IllegalArgumentException("A deck can only have 4 cards.");
        }
        cards.add(card);
    }

    public List<Card> getCards() {
        return cards;
    }

    public Card getRandomCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.get((int) (Math.random() * cards.size()));
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}
