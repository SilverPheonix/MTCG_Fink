package org.example.Controller;

import org.example.Service.BattleService;
import org.example.Service.PlayerThread;

import java.io.PrintWriter;

public class BattleController {
    private final BattleService battleService = new BattleService();

    public void enterBattle(String token, PrintWriter out) {
        PlayerThread player = new PlayerThread(token);

        // Führe den Kampf aus und warte auf das Ergebnis
        String battleResult = battleService.handleBattle(player);

        // Sende das Ergebnis an den Client
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/plain");
        out.println();
        out.println(battleResult);
    }
}
