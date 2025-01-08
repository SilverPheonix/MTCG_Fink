package org.example.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BattleService {
    private final BlockingQueue<PlayerThread> waitingPlayers = new LinkedBlockingQueue<>();

    public synchronized String handleBattle(PlayerThread player) {
        try {
            // Prüfe, ob ein Gegner in der Warteschlange ist
            PlayerThread opponent = waitingPlayers.poll();

            if (opponent == null) {
                System.out.println(player.getUsername() + " wartet in der Lobby auf einen Gegner...");
                waitingPlayers.put(player); // Spieler in die Lobby einreihen
                return player.waitForBattleResult(); // Spieler wartet auf das Ergebnis
            } else {
                System.out.println("Kampf startet zwischen " + player.getUsername() + " und " + opponent.getUsername());
                startBattle(player, opponent); // Kampf starten
                return player.getImmediateResponse(); // Sofortige Rückgabe an Spieler 2
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "Ein Fehler ist während des Kampfes aufgetreten.";
        }
    }

    private void startBattle(PlayerThread player1, PlayerThread player2) {
        // Starte den Kampf in einem separaten Thread
        new Thread(() -> {
            String battleLog = "Kampf zwischen " + player1.getUsername() + " und " + player2.getUsername() + " hat begonnen.\n";

            // Simulierte Battle-Logik
            battleLog += player1.getUsername() + " greift an!\n";
            battleLog += player2.getUsername() + " verteidigt sich!\n";
            battleLog += "Kampf beendet: Gewinner ist " + player1.getUsername() + "!\n";

            System.out.println("Battle log: \n" + battleLog);

            // Ergebnisse an beide Spieler senden
            player1.setBattleResult(battleLog);
            player2.setBattleResult(battleLog);
        }).start();
    }
}