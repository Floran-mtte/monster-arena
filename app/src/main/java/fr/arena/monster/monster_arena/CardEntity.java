package fr.arena.monster.monster_arena;

public class CardEntity extends Card {

    protected int defend;
    protected int attack;


    public CardEntity(String assetPath, int defend, int attack, String id, int level, String name, int type_card, String cardDetail) {
        super(assetPath, id, level, name, type_card, cardDetail);

        setAttack(attack);
        setDefend(defend);
    }

    public CardEntity(String assetPath, int defend, int attack, String id, int level, String name, int type_card, String cardDetail, String ref_familly) {
        super(assetPath, id, level, name, type_card, cardDetail, ref_familly);

        setAttack(attack);
        setDefend(defend);
    }

    public int getDefend() {
        return defend;
    }

    public void setDefend(int defend) {
        this.defend = defend;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }


}
