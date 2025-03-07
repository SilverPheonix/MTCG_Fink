package org.example.Model;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {

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
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Deck: [");

        // Iterate through the list of cards and append each card's toString()
        for (int i = 0; i < cards.size(); i++) {
            sb.append(cards.get(i).toString());
            if (i < cards.size() - 1) {
                sb.append(", ");  // Add a comma between cards
            }
        }

        sb.append("]");
        return sb.toString();
    }
}
