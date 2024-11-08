package org.example.Model;

public class Card {
    private String name;
    private int damage;
    private Type type;

    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum Type {Fire,Water,Normal};

    public Card(String name, int damage, Type type) {
        this.name = name;
        this.damage = damage;
        this.type = type;
    }
}
