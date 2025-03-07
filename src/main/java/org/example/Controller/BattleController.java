package org.example.Controller;

import org.example.Model.User;
import org.example.Repository.UserRepository;
import org.example.Service.BattleService;

import java.io.PrintWriter;

public class BattleController {
    private final BattleService battleService = new BattleService();
    private final UserRepository userRepository = new UserRepository();

    public void enterBattle(String token, PrintWriter out) {
        User player = userRepository.getUserByUsername(userRepository.getUsernameByToken(token));

        if (player == null) {
            out.println("HTTP/1.1 404 User Not Found");
            out.println("Content-Type: text/plain");
            out.println();
            out.println("Spieler existiert nicht oder ist nicht eingeloggt.");
            out.flush();
            return;
        }

        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/plain");
        out.println();
        out.flush(); // Antwortkopf sofort senden

        // Kampf starten und erste Nachricht an den Spieler senden
        String battleResult = battleService.handleBattle(player);
        out.println(battleResult);
        out.flush();

        // Spieler wartet, bis das Battle-Log verfügbar ist
        synchronized (player) {
            while (player.getBattleResult() == null) {
                try {
                    player.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    out.println("Fehler während des Kampfes.");
                    out.flush();
                    return;
                }
            }
        }

        // Endgültiges Battle-Log senden
        out.println(player.getBattleResult());
        out.flush();
    }
}
