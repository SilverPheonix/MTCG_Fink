package org.example.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class User {
    @JsonProperty("Username")
    private String username;
    @JsonProperty("Password")
    private String password;
    private int coins;


    private int elo;
    private ArrayList<Card> stack;
    private Deck deck;

    public void trade(){

    }
    public void buy_pack(){

    }
    public void define_deck(){

    }
    // Standardkonstruktor
    public User() {
        this.coins = 20;
        this.elo = 100;
        this.stack = new ArrayList<>();
        this.deck = new Deck();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.elo = 100;
        this.coins = 20; // Startkapital
        this.stack = new ArrayList<>();
        this.deck = new Deck();
    }
    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }
    public void addCardToStack(Card card) {
        stack.add(card);
    }

    public void setDeck(Deck deck) {
        this.deck = (Deck) deck;
    }

    public Deck getDeck() {
        return this.deck;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}
