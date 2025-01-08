package org.example.Model;

public abstract class Card {
    private String id;
    private String name;
    private double damage;
    private ElementType elementType;

    public Card(String id, String name, double damage, ElementType elementType) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getDamage() {
        return damage;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public abstract boolean isMonster();

    public enum ElementType {
        FIRE,
        WATER,
        NORMAL
    }
}
