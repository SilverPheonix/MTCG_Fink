package org.example.Service;

public class PlayerThread {
    private final String username;
    private String battleResult;
    private final Object lock = new Object(); // Synchronisationsobjekt

    public PlayerThread(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

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
}
