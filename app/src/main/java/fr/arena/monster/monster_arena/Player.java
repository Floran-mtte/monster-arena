package fr.arena.monster.monster_arena;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import java.util.HashMap;
import java.util.Map;

public class Player {

    private String id;
    private String playerName = "john";
    private int lifepoint;
    private int mana;
    private int manaMax;
    private Map<String, Object> hand = null;
    private Map<String, Object> board = null;
    private Map<Integer, Object> discarding = null;

    public Player(String id, String playerName, int lifepoint, int mana, int manaMax) {
        setId(id);
        setPlayerName(playerName);
        setLifepoint(lifepoint);
        setMana(mana);
        setManaMax(manaMax);
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

    public Map<Integer, Object> getDiscarding() {
        return discarding;
    }

    public void setDiscarding(Map<Integer, Object> discarding) {
        this.discarding = discarding;
    }

    public void setPlayerInfo(String partyId, int currentPlayer) {
        Map<String, Object> party = new HashMap<>();

        if (currentPlayer == 1) {
            Map<String, Object> player1Info = new HashMap<>();
            player1Info.put("name", this.getPlayerName());
            player1Info.put("life", this.getLifepoint());
            player1Info.put("mana", this.getMana());
            player1Info.put("manaMax", this.getManaMax());
            player1Info.put("hand", this.getHand());
            player1Info.put("board", this.getBoard());
            player1Info.put("discarding",this.getDiscarding());
            Helper.getInstance().db.collection("Party").document(partyId)
                .update("player1Info", player1Info)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
        } else {
            Map<String, Object> player2Info = new HashMap<>();
            player2Info.put("name", this.getPlayerName());
            player2Info.put("life", this.getLifepoint());
            player2Info.put("mana", this.getMana());
            player2Info.put("manaMax", this.getManaMax());
            player2Info.put("hand", this.getHand());
            player2Info.put("board", this.getBoard());
            player2Info.put("discarding",this.getDiscarding());
            Helper.getInstance().db.collection("Party").document(partyId)
                .update("player2Info", player2Info)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
        }

    }

    public void updatePlayer(Map<String, Object> playerInfo) {
        Log.d("updatePlayer: ", playerInfo.toString());
        for (Map.Entry<String, Object> entry : playerInfo.entrySet()) {
            switch (entry.getKey()) {
                case "life":
                    setLifepoint(Integer.parseInt(entry.getValue().toString()));
                    break;
                case "mana":
                    setMana(Integer.parseInt(entry.getValue().toString()));
                    break;
                case "manaMax":
                    setManaMax(Integer.parseInt(entry.getValue().toString()));
                case "name":
                    setPlayerName(entry.getValue().toString());
                    break;
                case "hand":
                    setHand((Map<String, Object>) entry.getValue());
                    break;
                case "board":
                    if (this.getBoard() != entry.getValue())
                        setBoard((Map<String, Object>) entry.getValue());
                    break;
            }
        }
    }

    public boolean deleteCardFromBoard(String pos, int index) {
        try {
            int size = 0;
            if(this.discarding.size() > 0) {
                size = discarding.size()+1;
            }
            this.discarding.put(size,index);
            this.getBoard().remove("board-"+pos);
            return true;
        }
        catch (Exception e) {
            Log.d("battle", "trying to remove from  player board but failed cause : "+e.getMessage());
            return false;
        }
    }

    public int getManaMax() {
        return manaMax;
    }

    public void setManaMax(int manaMax) {
        this.manaMax = manaMax;
    }
}
