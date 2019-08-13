package fr.arena.monster.monster_arena;

public abstract class Card {

    protected String assetPath;
    protected String id;
    protected int level;
    protected String name;
    protected int type_card;
    protected String cardDetail;
    protected boolean active;
    protected boolean onGround;
    protected boolean inHand;
    int index;



    public Card(String assetPath, String id, int level, String name, int type_card, String cardDetail)
    {
        setAssetPath(assetPath);
        setId(id);
        setLevel(level);
        setName(name);
        setType_card(type_card);
        setCardDetail(cardDetail);
        setActive(true);
        setInHand(false);
        setOnGround(false);
    }

    public String getAssetPath() {
        return assetPath;
    }

    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
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

    public String getCardDetail() {
        return cardDetail;
    }

    public void setCardDetail(String cardDetail) {
        this.cardDetail = cardDetail;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isInHand() {
        return this.inHand;
    }

    public void setInHand(boolean inHand) {
        this.inHand = inHand;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
