package org.example.Controller;

import org.example.Service.BattleService;

import java.io.PrintWriter;

public class BattleController {
    private BattleService battleService = new BattleService();

    // POST /battles
    public void startBattle(String token, PrintWriter out) {
        String battleLog = battleService.startBattle(token);

        if (battleLog != null) {
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/plain");
            out.println();
            out.println(battleLog);
        } else {
            out.println("HTTP/1.1 400 Bad Request");
            out.println();
        }
    }
}
