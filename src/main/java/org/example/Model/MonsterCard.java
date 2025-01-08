package org.example.Model;

public class MonsterCard extends Card {
    public MonsterCard(String id, String name, double damage, ElementType elementType) {
        super(id, name, damage, elementType);
    }

    @Override
    public boolean isMonster() {
        return true;
    }
}
