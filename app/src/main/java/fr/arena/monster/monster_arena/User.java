package fr.arena.monster.monster_arena;

import java.util.ArrayList;

public class User {
    private int money;
    private ArrayList deck;
    private ArrayList collection;

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public ArrayList getDeck() {
        return deck;
    }

    public void setDeck(ArrayList deck) {
        this.deck = deck;
    }

    public ArrayList getCollection() {
        return collection;
    }

    public void setCollection(ArrayList collection) {
        this.collection = collection;
    }
}
