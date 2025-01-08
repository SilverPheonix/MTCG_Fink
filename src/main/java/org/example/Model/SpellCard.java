package org.example.Model;

public class SpellCard extends Card {
    public SpellCard(String id, String name, double damage, ElementType elementType) {
        super(id, name, damage, elementType);
    }

    @Override
    public boolean isMonster() {
        return false;
    }
}

