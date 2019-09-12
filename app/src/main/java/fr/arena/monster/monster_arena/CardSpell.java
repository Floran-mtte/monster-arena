package fr.arena.monster.monster_arena;

public class CardSpell extends Card {

    private int idEffect;

    public CardSpell(String assetPath, String id, int level, String name, int type_card, String cardDetail, int idEffect) {
        super(assetPath, id, level, name, type_card, cardDetail);
        setIdEffect(idEffect);
    }

    public int getIdEffect() {
        return idEffect;
    }

    private void setIdEffect(int idEffect) {
        this.idEffect = idEffect;
    }
}
