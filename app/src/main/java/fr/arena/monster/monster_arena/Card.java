package fr.arena.monster.monster_arena;

public class Card {

    protected String assetPath;
    protected int defend;
    protected int attack;
    protected String id;
    protected int level;
    protected String name;
    protected int type_card;


    public Card(String assetPath, int defend, int attack, String id, int level, String name, int type_card)
    {
        setAssetPath(assetPath);
        setDefend(defend);
        setAttack(attack);
        setId(id);
        setLevel(level);
        setName(name);
        setType_card(type_card);
    }

    public String getAssetPath() {
        return assetPath;
    }

    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getType_card() {
        return type_card;
    }

    public void setType_card(int type_card) {
        this.type_card = type_card;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
