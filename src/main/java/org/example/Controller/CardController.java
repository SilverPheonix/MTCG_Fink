package org.example.Controller;

import org.example.Service.CardService;

import java.io.PrintWriter;

public class CardController {
    private CardService cardService = new CardService();

    // GET /cards
    public void getCards(String token, PrintWriter out) {
        String cards = cardService.getUserCards(token);

        if (cards != null && !cards.isEmpty()) {
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: application/json");
            out.println();
            out.println(cards);
        } else {
            out.println("HTTP/1.1 204 No Content");
            out.println();
        }
    }

    // GET /deck
    public void getDeck(String token, PrintWriter out) {
        String deck = cardService.getUserDeck(token);

        if (deck != null && !deck.isEmpty()) {
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: application/json");
            out.println();
            out.println(deck);
        } else {
            out.println("HTTP/1.1 204 No Content");
            out.println();
        }
    }

    // PUT /deck
    public void configureDeck(String token, String payload, PrintWriter out) {
        boolean success = cardService.configureDeck(token, payload);

        if (success) {
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: application/json");
            out.println();
            out.println("{\"message\": \"Deck configured successfully\"}");
        } else {
            out.println("HTTP/1.1 400 Bad Request");
            out.println();
        }
    }
}
