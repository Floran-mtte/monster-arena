package fr.arena.monster.monster_arena;

import java.util.Map;

public class Player {

    private String id;
    private String playerName;
    private int lifepoint;
    private int mana;
    private Map<String, Object> hand;
    private Map<String, Object> board;

    public Player(String id, String playerName, int lifepoint, int mana)
    {
        setId(id);
        setPlayerName(playerName);
        setLifepoint(lifepoint);
        setMana(mana);
    }

    public Player(String id, int lifepoint, int mana)
    {
        setId(id);
        setPlayerName(playerName);
        setLifepoint(lifepoint);
        setMana(mana);
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getLifepoint() {
        return lifepoint;
    }

    public void setLifepoint(int lifepoint) {
        this.lifepoint = lifepoint;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getHand() {
        return hand;
    }

    public void setHand(Map<String, Object> hand) {
        this.hand = hand;
    }

    public Map<String, Object> getBoard() {
        return board;
    }

    public void setBoard(Map<String, Object> board) {
        this.board = board;
    }
}
