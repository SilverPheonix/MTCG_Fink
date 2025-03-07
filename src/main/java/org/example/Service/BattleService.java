package org.example.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.example.Model.*;
import org.example.Repository.CardRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BattleService {
    private final BlockingQueue<User> waitingUsers = new LinkedBlockingQueue<>();
    private CardRepository cardRepository =new CardRepository();

    public String handleBattle(User player) {
        try {
            User opponent = waitingUsers.poll();

            if (opponent == null) {
                System.out.println(player.getUsername() + " wartet in der Lobby auf einen Gegner...");
                waitingUsers.put(player);
                opponent = waitingUsers.take();  // Warten auf einen Gegner

                // Falls sich der Spieler selbst zurückbekommt, abbrechen
                if (opponent == player) {
                    System.out.println("FEHLER: Spieler hat sich selbst als Gegner bekommen!");
                    waitingUsers.put(player); // Wieder in die Warteschlange setzen
                    synchronized (player) {
                        player.wait();  // Spieler wartet, bis Gegner gefunden wird
                    }
                    return "Warten auf einen Gegner...";
                }
            }

            System.out.println("Kampf startet zwischen " + player.getUsername() + " und " + opponent.getUsername());

            synchronized (opponent) {
                opponent.notify();  // Falls Gegner wartet, aufwecken
            }

            return startBattle(player, opponent);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Ein Fehler ist während des Kampfes aufgetreten.";
        }
    }

    private String startBattle(User player1, User player2) {
        // Kampf-Log erstellen
        StringBuilder battleLog = new StringBuilder();
        battleLog.append("Kampf zwischen " + player1.getUsername() + " und " + player2.getUsername() + " beginnt...\n");

        // Decks beider Spieler holen
        Deck deck1 = cardRepository.getUserDeckByToken(player1.getUsername() + "-mtcgToken");
        Deck deck2 = cardRepository.getUserDeckByToken(player2.getUsername() + "-mtcgToken");
        System.out.println(deck1.toString());
        System.out.println(deck2.toString());

        // Maximale Rundenanzahl auf 100 begrenzen
        for (int round = 1; round <= 100; round++) {
            battleLog.append("Runde " + round + ":\n");

            // Karten zufällig wählen
            Card card1 = deck1.getRandomCard();
            Card card2 = deck2.getRandomCard();

            // Spezialfähigkeiten prüfen und anpassen
            if (card1 instanceof MonsterCard && card2 instanceof MonsterCard) {
                battleLog.append("Beide Spieler haben Monsterkarten gewählt. Es gibt keinen Effekt aufgrund von Elementen.\n");
            } else {
                // Zauberkarten: Elementinteraktion
                if (card1 instanceof SpellCard && card2 instanceof SpellCard) {
                    battleLog.append("Zauberkarten kämpfen gegeneinander, Effektivität wird angewendet.\n");
                    battleLog.append(calculateSpellCardEffect((SpellCard) card1, (SpellCard) card2));
                }

                // Kartentyp und Spezialfähigkeit checken
                battleLog.append(calculateDamageAndLog(card1, card2, battleLog));
            }

            battleLog.append(card1.getName() + " verursacht " + card1.getDamage() + " Schaden.\n");
            battleLog.append(card2.getName() + " verursacht " + card2.getDamage() + " Schaden.\n");

            // Ergebnis der Runde
            if (card1.getDamage() > card2.getDamage()) {
                battleLog.append(player1.getUsername() + " gewinnt diese Runde.\n");
                deck1.addCard(card2); // Gegnerische Karte wird in Deck von Spieler 1 gelegt
                deck2.removeCard(card2);
            } else if (card1.getDamage() < card2.getDamage()) {
                battleLog.append(player2.getUsername() + " gewinnt diese Runde.\n");
                deck2.addCard(card1); // Gegnerische Karte wird in Deck von Spieler 2 gelegt
                deck1.removeCard(card1);
            } else {
                battleLog.append("Unentschieden in dieser Runde.\n");
            }

            // Ende des Spiels
            if (deck1.isEmpty() || deck2.isEmpty()) {
                battleLog.append("Kampf beendet nach " + round + " Runden.\n");
                if(deck1.isEmpty()){
                    battleLog.append("Player " + player2.getUsername() + " wins!");
                    updateElo(player1, player2, false);
                }
                else{
                    battleLog.append("Player " + player1.getUsername() + " wins!");
                    updateElo(player1, player2, true);
                }
                break;
            }
        }
        if (!(deck1.isEmpty() || deck2.isEmpty())){
            battleLog.append("Kampf beendet nach 100 Runden.\n Es ist ein Unentschieden!\n");
        }

        synchronized (player2) {
            player2.setBattleResult(battleLog.toString());
            player2.notify();
        }

        return battleLog.toString();
    }
    // Berechnet die Elementinteraktionen zwischen Zauberkarten
    private String calculateSpellCardEffect(SpellCard spell1, SpellCard spell2) {
        String effectLog = "";
        // Beispiel: Berechnungen und Interaktionen der Zauber-Effekte
        if (spell1.getElementType() == Card.ElementType.WATER && spell2.getElementType() == Card.ElementType.FIRE) {
            effectLog += "Wasser-Zauber ist effektiv gegen Feuer-Zauber, Schaden wird verdoppelt.\n";
        } else if (spell1.getElementType() == Card.ElementType.FIRE && spell2.getElementType() == Card.ElementType.WATER) {
            effectLog += "Feuer-Zauber ist weniger effektiv gegen Wasser-Zauber, Schaden wird halbiert.\n";
        }
        return effectLog;
    }

    // Berechnet den Schaden und aktualisiert das Log
    private String calculateDamageAndLog(Card card1, Card card2, StringBuilder battleLog) {
        String damageLog = "";

        // Beispiel für Spezialfähigkeit: Goblins können keine Drachen angreifen
        if (card1 instanceof MonsterCard && ((MonsterCard) card1).getName().equals("Goblin") &&
                card2 instanceof MonsterCard && ((MonsterCard) card2).getName().equals("Dragon")) {
            damageLog += "Goblin weicht dem Drachen aus und greift nicht an.\n";
        }
        // Weitere Spezialfähigkeiten...
        return damageLog;
    }

    // Berechnet den ELO nach dem Spiel
    public void updateElo(User player1, User player2, boolean player1Wins) {
        int baseElo = 10; // Basis-ELO Änderung
        int player1Elo = player1.getElo();
        int player2Elo = player2.getElo();

        if (player1Wins) {
            player1.setElo(player1Elo + baseElo);
            player2.setElo(player2Elo - baseElo);
        } else {
            player2.setElo(player2Elo + baseElo);
            player1.setElo(player1Elo - baseElo);
        }
    }
}
