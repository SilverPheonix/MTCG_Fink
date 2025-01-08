package org.example.Model;

import java.util.ArrayList;
import java.util.List;

public class Battle {
    private Deck player1Deck;
    private Deck player2Deck;
    private StringBuilder battleLog;
    private int maxRounds = 100;

    public Battle(Deck player1Deck, Deck player2Deck) {
        this.player1Deck = player1Deck;
        this.player2Deck = player2Deck;
        this.battleLog = new StringBuilder();
    }

    public String startBattle() {
        int round = 1;
        while (!player1Deck.isEmpty() && !player2Deck.isEmpty() && round <= maxRounds) {
            battleLog.append("Round ").append(round).append(":\n");

            Card player1Card = player1Deck.getRandomCard();
            Card player2Card = player2Deck.getRandomCard();

            battleLog.append("Player 1 plays ").append(player1Card.getName()).append(" (").append(player1Card.getDamage()).append(" damage)\n");
            battleLog.append("Player 2 plays ").append(player2Card.getName()).append(" (").append(player2Card.getDamage()).append(" damage)\n");

            double player1Damage = calculateDamage(player1Card, player2Card);
            double player2Damage = calculateDamage(player2Card, player1Card);

            if (player1Damage > player2Damage) {
                battleLog.append("Player 1 wins the round!\n");
                player2Deck.removeCard(player2Card);
                player1Deck.addCard(player2Card);
            } else if (player2Damage > player1Damage) {
                battleLog.append("Player 2 wins the round!\n");
                player1Deck.removeCard(player1Card);
                player2Deck.addCard(player1Card);
            } else {
                battleLog.append("It's a draw!\n");
            }

            battleLog.append("\n");
            round++;
        }

        return determineWinner();
    }

    private double calculateDamage(Card attacker, Card defender) {
        double damage = attacker.getDamage();

        if (!attacker.isMonster() || !defender.isMonster()) {
            // Apply element effectiveness
            damage *= getEffectiveness(attacker.getElementType(), defender.getElementType());
        }

        // Apply special rules
        if (attacker instanceof MonsterCard && defender instanceof MonsterCard) {
            damage = applySpecialRules((MonsterCard) attacker, (MonsterCard) defender, damage);
        }

        return damage;
    }

    private double getEffectiveness(Card.ElementType attacker, Card.ElementType defender) {
        if (attacker == Card.ElementType.WATER && defender == Card.ElementType.FIRE) return 2.0;
        if (attacker == Card.ElementType.FIRE && defender == Card.ElementType.NORMAL) return 2.0;
        if (attacker == Card.ElementType.NORMAL && defender == Card.ElementType.WATER) return 2.0;

        if (attacker == Card.ElementType.FIRE && defender == Card.ElementType.WATER) return 0.5;
        if (attacker == Card.ElementType.WATER && defender == Card.ElementType.NORMAL) return 0.5;
        if (attacker == Card.ElementType.NORMAL && defender == Card.ElementType.FIRE) return 0.5;

        return 1.0; // No effect
    }

    private double applySpecialRules(MonsterCard attacker, MonsterCard defender, double damage) {
        if (attacker.getName().contains("Goblin") && defender.getName().contains("Dragon")) {
            return 0; // Goblins are too afraid of Dragons
        }
        if (attacker.getName().contains("Wizard") && defender.getName().contains("Ork")) {
            return 0; // Wizards can control Orks
        }
        if (attacker.getName().contains("Kraken") && !defender.isMonster()) {
            return Double.MAX_VALUE; // Kraken is immune to spells
        }
        if (attacker.getName().contains("Knight") && defender.getName().contains("WaterSpell")) {
            return 0; // Knights drown instantly from WaterSpells
        }
        if (attacker.getName().contains("FireElf") && defender.getName().contains("Dragon")) {
            return damage; // FireElves can evade Dragons
        }
        return damage;
    }

    private String determineWinner() {
        if (player1Deck.isEmpty() && player2Deck.isEmpty()) {
            battleLog.append("The battle ends in a draw!");
        } else if (player1Deck.isEmpty()) {
            battleLog.append("Player 2 wins the battle!");
        } else if (player2Deck.isEmpty()) {
            battleLog.append("Player 1 wins the battle!");
        }
        return battleLog.toString();
    }
}
