package org.example.Service;

import org.example.Model.Battle;
import org.example.Model.Deck;
import org.example.Repository.BattleRepository;

public class BattleService {
    private BattleRepository battleRepository = new BattleRepository();

    public String startBattle(String token) {
        Deck player1Deck = battleRepository.getDeckByToken(token);
        Deck player2Deck = battleRepository.findOpponentDeck();

        if (player1Deck == null || player2Deck == null) {
            return null;
        }

        Battle battle = new Battle(player1Deck, player2Deck);
        return battle.startBattle();
    }
}
