package org.example.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

public class User {
    @JsonProperty("Username")
    private String username;
    @JsonProperty("Password")
    private String password;
    @JsonProperty("coins")
    private int coins;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Bio")
    private String bio;
    @JsonProperty("Image")
    private String image;
    @JsonProperty("elo")
    private int elo;
    private ArrayList<Card> stack;
    private Deck deck;
    private String battleResult;
    private final Object lock = new Object(); // Synchronisationsobjekt für den Kampf

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
        this.coins = 20;
        this.stack = new ArrayList<>();
        this.deck = new Deck();
    }

    // Methoden für das Battle-Handling
    public String waitForBattleResult() throws InterruptedException {
        synchronized (lock) {
            while (battleResult == null) {
                lock.wait(); // Warten auf das Ergebnis des Kampfes
            }
            return battleResult;
        }
    }

    public String getImmediateResponse() {
        return "Kampf gestartet. Ergebnisse werden verarbeitet...";
    }

    public void setBattleResult(String battleResult) {
        synchronized (lock) {
            this.battleResult = battleResult;
            lock.notifyAll(); // Signalisiere, dass das Ergebnis verfügbar ist
        }
    }
    public String getBattleResult() {
        return this.battleResult;
    }

    public void updateElo(boolean won) {
        synchronized (lock) {
            if (won) {
                this.elo += 10;
            } else {
                this.elo -= 10;
            }
        }
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter und Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public Deck getDeck() {
        return this.deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public void addCardToStack(Card card) {
        stack.add(card);
    }
}
