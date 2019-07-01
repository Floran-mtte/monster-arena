package fr.arena.monster.monster_arena;

public class Party
{
    private String createdAt;
    private String updatedAt;
    private String id;
    private int numberRound;
    private String player1;
    private String player2;
    private int timeGame;

    public Party(String createdAt, String updatedAt, String id, int numberRound, String player1, String player2, int timeGame)
    {
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
        setId(id);
        setNumberRound(numberRound);
        setPlayer1(player1);
        setPlayer2(player2);
        setTimeGame(timeGame);
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumberRound() {
        return numberRound;
    }

    public void setNumberRound(int numberRound) {
        this.numberRound = numberRound;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public int getTimeGame() {
        return timeGame;
    }

    public void setTimeGame(int timeGame) {
        this.timeGame = timeGame;
    }
}
