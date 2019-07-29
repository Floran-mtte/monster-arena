package fr.arena.monster.monster_arena;

import java.util.HashMap;
import java.util.Map;

public class GameBoardPlayer {

    Map<String, CardEntity> board = new HashMap<>();


    public Map<String, CardEntity> getBoard() {
        return board;
    }

    public void setLeftCard(CardEntity card) {
        board.put("left",card);
    }

    public void setUpCard(CardEntity card) {
        board.put("up",card);
    }

    public void setRightCard(CardEntity card) {
        board.put("right",card);
    }

    public CardEntity getLeftCardBoard() {
        if(board.get("left") != null)
        {
            return board.get("left");
        }
        return null;
    }

    public Card getUpCardBoard() {
        if(board.get("up") != null)
        {
            return board.get("up");
        }
        return null;
    }

    public Card getRightCardBoard() {
        if(board.get("right") != null)
        {
            return board.get("right");
        }
        return null;
    }


}
