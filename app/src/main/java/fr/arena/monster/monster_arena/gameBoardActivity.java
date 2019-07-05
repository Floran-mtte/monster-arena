package fr.arena.monster.monster_arena;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class gameBoardActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnDragListener, OnTaskCompleted {

    Party   party;
    Player  player1;
    Player  player2;

    ArrayList<Card>         player1Card = new ArrayList<>();
    ArrayList<Card>         player2Card = new ArrayList<>();

    ArrayList<Card> current_player_hand = new ArrayList<>();

    Helper helper = Helper.getInstance();
    String TAG = "gameBoardActivity";
    int currentPlayer;
    String playerTurn;

    ImageView hand_user_1, hand_user_2, hand_user_3, hand_user_4, hand_user_5, cardDetail, user_attack_left, user_attack_right, user_defense, dropZone = null;
    TextView user_left, user_top, user_right, opponent_left, opponent_top, opponent_right, user_mana, opponent_mana, user_life, opponent_life;
    FrameLayout filter;
    String label = null;
    OnTaskCompleted listener;

    private Task<Void> allTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Helper.playTheme(this, "fight");

        setContentView(R.layout.activity_game_board);

        user_life = (TextView) findViewById(R.id.user_life);
        opponent_life = (TextView) findViewById(R.id.opponent_life);

        user_mana = (TextView) findViewById(R.id.user_mana);
        opponent_mana = (TextView) findViewById(R.id.opponent_mana);

        hand_user_1 = (ImageView) findViewById(R.id.hand_user_1);
        hand_user_2 = (ImageView) findViewById(R.id.hand_user_2);
        hand_user_3 = (ImageView) findViewById(R.id.hand_user_3);
        hand_user_4 = (ImageView) findViewById(R.id.hand_user_4);
        hand_user_5 = (ImageView) findViewById(R.id.hand_user_5);

        cardDetail = (ImageView) findViewById(R.id.cardDetail);
        filter = (FrameLayout) findViewById(R.id.filter);

        user_attack_left = (ImageView) findViewById(R.id.left_card_user);
        user_attack_right = (ImageView) findViewById(R.id.right_card_user);
        user_defense = (ImageView) findViewById(R.id.up_card_user);

        user_left = (TextView) findViewById(R.id.left_card_user_attack);
        user_top = (TextView) findViewById(R.id.up_card_user_defense);
        user_right = (TextView) findViewById(R.id.right_card_user_attack);
        opponent_left = (TextView) findViewById(R.id.left_card_opponent_attack);
        opponent_top = (TextView) findViewById(R.id.up_card_opponent_defense);
        opponent_right = (TextView) findViewById(R.id.right_card_opponent_attack);

        user_attack_left.setTag("left");
        user_defense.setTag("top");
        user_attack_right.setTag("right");

        hand_user_1.setOnClickListener(this);
        hand_user_2.setOnClickListener(this);
        hand_user_3.setOnClickListener(this);
        hand_user_4.setOnClickListener(this);
        hand_user_5.setOnClickListener(this);
        filter.setOnClickListener(this);

        hand_user_1.setOnLongClickListener(this);
        hand_user_2.setOnLongClickListener(this);
        hand_user_3.setOnLongClickListener(this);
        hand_user_4.setOnLongClickListener(this);
        hand_user_5.setOnLongClickListener(this);

        user_attack_left.setOnDragListener(this);
        user_attack_right.setOnDragListener(this);
        user_defense.setOnDragListener(this);

        String partyId = getIntent().getStringExtra("partyId");
        String player_1 = getIntent().getStringExtra("player_1");
        String player_2 = getIntent().getStringExtra("player_2");
        playerTurn = getIntent().getStringExtra("current_player");
        initParty(partyId, player_1, player_2);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @SuppressLint("DefaultLocale")
    public void initParty(String partyId, String player_1, String player_2) {
        //getUsersId(partyId);
        party = new Party("", "", partyId, 1, player_1, player_2, 0);
        player1 = new Player(player_1, "john", 2500, 3);
        player2 = new Player(player_2, "daenerys", 2500, 2);

        currentPlayer = currentPlayer();
        switch (currentPlayer) {
            case 1:
                setPlayer1Info();
                user_life.setText(String.format("%s%d", user_life.getText().toString(), player1.getLifepoint()));
                user_mana.setText(String.format("%d/%d", player1.getMana(), player1.getMana()-1));

                opponent_life.setText(String.format("%s%d", opponent_life.getText().toString(), player2.getLifepoint()));
                opponent_mana.setText(String.format("%d/%d", player2.getMana(), player2.getMana()));

                //todo : show of user wich player is
                Toast toast = Toast.makeText(getApplicationContext(), "Player 1", Toast.LENGTH_SHORT);
                toast.show();

                getPlayer1Card(player_1);
                listener = new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(Boolean isFull) {
                        Log.d(TAG, String.valueOf(currentPlayer));
                        if(isFull)
                        {
                            ImageView[] hands = new ImageView[5];
                            ArrayList<Card> player1Hand = new ArrayList<>();

                            hands[0] = hand_user_1;
                            hands[1] = hand_user_2;
                            hands[2] = hand_user_3;
                            hands[3] = hand_user_4;
                            hands[4] = hand_user_5;

                            Collections.shuffle(player1Card);

                            for (int i=0; i < 3; i++) {
                                Card parent = player1Card.get(i);
                                player1Hand.add(parent);
                                if (parent instanceof CardEntity) {
                                    CardEntity card = (CardEntity) parent;
                                    Drawable path = getDrawable(getResources()
                                            .getIdentifier(card.getAssetPath(), "drawable", getPackageName()));
                                    hands[i].setImageDrawable(path);
                                    hands[i].setTag(R.id.atk, card.getAttack());
                                    hands[i].setTag(R.id.def, card.getDefend());
                                    hands[i].setTag(R.id.cost, card.getLevel());
                                    hands[i].setTag(R.id.name, card.getAssetPath());
                                    hands[i].setVisibility(View.VISIBLE);
                                }
                            }
                            //todo add player hand to fb
                            addCardToParty(player1Hand);
                        }
                    }
                };
                break;
            case 2:
                setPlayer2Info();
                user_life.setText(String.format("%s%d", user_life.getText().toString(), player2.getLifepoint()));
                user_mana.setText(String.format("%d/%d", player2.getMana(), player2.getMana()));

                opponent_life.setText(String.format("%s%d", opponent_life.getText().toString(), player1.getLifepoint()));
                opponent_mana.setText(String.format("%d/%d", player1.getMana(), player1.getMana()-1));

                //todo : show of user wich player is
                Toast toast2 = Toast.makeText(getApplicationContext(), "Player 2", Toast.LENGTH_SHORT);
                toast2.show();

                getPlayer2Card(player_2);
                listener = new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(Boolean isFull) {
                        Log.d(TAG, String.valueOf(currentPlayer));
                        if(isFull)
                        {
                            ImageView[] hands = new ImageView[5];
                            ArrayList<Card> player2Hand = new ArrayList<>();

                            hands[0] = hand_user_1;
                            hands[1] = hand_user_2;
                            hands[2] = hand_user_3;
                            hands[3] = hand_user_4;
                            hands[4] = hand_user_5;

                            Collections.shuffle(player2Card);

                            for (int i=0; i < 3; i++) {
                                Card parent = player2Card.get(i);
                                player2Hand.add(parent);
                                if (parent instanceof CardEntity) {
                                    CardEntity card = (CardEntity) parent;
                                    Drawable path = getDrawable(getResources()
                                            .getIdentifier(card.getAssetPath(), "drawable", getPackageName()));
                                    Log.d(TAG, card.getAssetPath());
                                    hands[i].setImageDrawable(path);
                                    hands[i].setTag(R.id.atk, card.getAttack());
                                    hands[i].setTag(R.id.def, card.getDefend());
                                    hands[i].setTag(R.id.cost, card.getLevel());
                                    hands[i].setTag(R.id.name, card.getAssetPath());
                                    hands[i].setVisibility(View.VISIBLE);
                                }
                            }
                            //todo add player hand to fb
                            addCardToParty(player2Hand);
                        }
                    }
                };
                break;
            default:
                break;
        }
    }

    public int currentPlayer() {
        String userId = Helper.getInstance().mAuth.getUid();

        Log.d(TAG, player1.getId());
        Log.d(TAG, player2.getId());

        if(userId.equals(player1.getId()))
        {
            return 1;
        }
        else if(userId.equals(player2.getId()))
        {
            return 2;
        }
        return 0;
    }

    public void setPlayer1Info() {
        Map<String, Object> partyContainer = new HashMap<>();
        Map<String, Object> player1Info = new HashMap<>();
        player1Info.put("lp", 2500);
        player1Info.put("mana", 3);
        partyContainer.put("player1Info", player1Info);
        Helper.getInstance().db.collection("Party").document(party.getId())
                .set(partyContainer, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    public void setPlayer2Info() {
        Map<String, Object> partyContainer = new HashMap<>();
        Map<String, Object> player2Info = new HashMap<>();
        player2Info.put("lp", 2500);
        player2Info.put("mana", 2);
        partyContainer.put("player2Info", player2Info);
        Helper.getInstance().db.collection("Party").document(party.getId())
                .set(partyContainer, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    public void getPlayer1Card(String userId) {
        DocumentReference docRef = helper.db.collection("User_Deck").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        List list = new ArrayList<String>();
                        list = (List) document.getData().get("cards");

                        getDetailsCard(list, player1Card);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void getPlayer2Card(String userId) {
        DocumentReference docRef = helper.db.collection("User_Deck").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        List list = new ArrayList<String>();
                        list = (List) document.getData().get("cards");

                        getDetailsCard(list, player2Card);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void getDetailsCard(List listDoc, ArrayList<Card> playerCard) {
        for (int i = 0; i < listDoc.size(); i++) {
            Log.d(TAG, "Test");
            DocumentReference doc = (DocumentReference) listDoc.get(i);
            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> obj = document.getData();

                            if(obj.get("card_detail").equals("entity"))
                            {
                                CardEntity card = new CardEntity(
                                        obj.get("asset_path").toString(),
                                        Integer.parseInt(obj.get("defend").toString()),
                                        Integer.parseInt(obj.get("attack").toString()),
                                        obj.get("id").toString(),
                                        Integer.parseInt(obj.get("level").toString()),
                                        obj.get("name").toString(),
                                        Integer.parseInt(obj.get("type_card").toString()),
                                        obj.get("card_detail").toString()
                                );

                                playerCard.add(card);

                                //addCardToParty(card);

                                if(listDoc.size() == playerCard.size())
                                {
                                    listener.onTaskCompleted(true);
                                }

                            }


                            Log.d(TAG, obj.get("name").toString());


                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }
    }

    public void addCardToParty(ArrayList<Card> playerCard) {

        ArrayList playerDeck = new ArrayList();
        for (int i = 0; i < playerCard.size(); i++) {
            Card parent = playerCard.get(i);
            if (parent instanceof CardEntity) {
                playerDeck.add((CardEntity) parent);
            }
        }
        Map<String, Object> party_container = new HashMap<>();
        if (currentPlayer == 1) {
            Map<String, Object> player1Info = new HashMap<>();
            player1Info.put("hands", playerDeck);
            party_container.put("player1Info", player1Info);
            addHandToDb(party_container);
        } else {
            Map<String, Object> player2Info = new HashMap<>();
            player2Info.put("hands", playerDeck);
            party_container.put("player2Info", player2Info);
            addHandToDb(party_container);
        }

        /*Map<String, Object> party_card = new HashMap<>();
        party_card.put("id", card.getId());
        party_card.put("id_party", party.getId());
        party_card.put("active", true);
        party_card.put("card_detail", card.getCardDetail());

        if (card instanceof CardEntity) {
            party_card.put("attack",((CardEntity) card).getAttack());
            party_card.put("defend",((CardEntity) card).getDefend());
        }

        helper.db.collection("Party_Card").document()
                .set(party_card)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error writing document", e);
                    }
                });*/
    }

    public void addHandToDb(Map<String, Object> party_container) {
        helper.db.collection("Party").document(party.getId())
                .set(party_container, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error writing document", e);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        Drawable source;
        switch (v.getId()) {
            case R.id.hand_user_1:
                source = hand_user_1.getDrawable();
                cardDetail.setImageDrawable(source);
                cardDetail.setVisibility(View.VISIBLE);
                filter.setVisibility(View.VISIBLE);
                break;
            case R.id.hand_user_2:
                source = hand_user_2.getDrawable();
                cardDetail.setImageDrawable(source);
                cardDetail.setVisibility(View.VISIBLE);
                filter.setVisibility(View.VISIBLE);
                break;
            case R.id.hand_user_3:
                source = hand_user_3.getDrawable();
                cardDetail.setImageDrawable(source);
                cardDetail.setVisibility(View.VISIBLE);
                filter.setVisibility(View.VISIBLE);
                break;
            case R.id.hand_user_4:
                source = hand_user_4.getDrawable();
                cardDetail.setImageDrawable(source);
                cardDetail.setVisibility(View.VISIBLE);
                filter.setVisibility(View.VISIBLE);
                break;
            case R.id.hand_user_5:
                source = hand_user_5.getDrawable();
                cardDetail.setImageDrawable(source);
                cardDetail.setVisibility(View.VISIBLE);
                filter.setVisibility(View.VISIBLE);
                break;
            case R.id.filter:
                filter.setVisibility(View.INVISIBLE);
                cardDetail.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!playerTurn.equals(Helper.getInstance().mAuth.getUid())) {
            Toast toast = Toast.makeText(getApplicationContext(), "is not your turn", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        View.DragShadowBuilder mShadow = new View.DragShadowBuilder(v);
        ClipData.Item item = new ClipData.Item(v.getTag().toString());
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData data = new ClipData(v.getTag().toString(), mimeTypes, item);
        v.startDragAndDrop(data, mShadow, null, 0);
        return false;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                label = event.getClipDescription().getLabel().toString();
                v.invalidate();
                return true;

            case DragEvent.ACTION_DRAG_ENTERED:

                v.invalidate();
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                return true;

            case DragEvent.ACTION_DRAG_EXITED:

                v.invalidate();
                return true;

            case DragEvent.ACTION_DROP:
                dropZone = (ImageView) v;
                v.invalidate();
                boolean isInvokable = true;
                switch (label) {
                    case "hand_user_1":
                        isInvokable = isInvokable(Integer.parseInt(hand_user_1.getTag(R.id.cost).toString()));
                        break;
                    case "hand_user_2":
                        isInvokable = isInvokable(Integer.parseInt(hand_user_2.getTag(R.id.cost).toString()));
                        break;
                    case "hand_user_3":
                        isInvokable = isInvokable(Integer.parseInt(hand_user_3.getTag(R.id.cost).toString()));
                        break;
                    case "hand_user_4":
                        isInvokable = isInvokable(Integer.parseInt(hand_user_4.getTag(R.id.cost).toString()));
                        break;
                    case "hand_user_5":
                        isInvokable = isInvokable(Integer.parseInt(hand_user_4.getTag(R.id.cost).toString()));
                        break;
                }

                if (!isInvokable)
                    return false;

                Drawable source;
                ArrayList<String> stat = new ArrayList<>();
                String pos = (String) dropZone.getTag();
                Log.d(TAG, "onDrag: before switch");
                switch (label) {
                    case "hand_user_1":
                        source = hand_user_1.getDrawable();
                        dropZone.setImageDrawable(source);
                        stat.add(hand_user_1.getTag(R.id.atk).toString());
                        stat.add(hand_user_1.getTag(R.id.def).toString());
                        manaDecrease(Integer.parseInt(hand_user_1.getTag(R.id.cost).toString()));
                        hand_user_1.setVisibility(View.INVISIBLE);
                        Helper.playVoice(this, hand_user_1.getTag(R.id.name).toString());
                        break;
                    case "hand_user_2":
                        source = hand_user_2.getDrawable();
                        dropZone.setImageDrawable(source);
                        stat.add(hand_user_2.getTag(R.id.atk).toString());
                        stat.add(hand_user_2.getTag(R.id.def).toString());
                        manaDecrease(Integer.parseInt(hand_user_2.getTag(R.id.cost).toString()));
                        hand_user_2.setVisibility(View.INVISIBLE);
                        Helper.playVoice(this, hand_user_2.getTag(R.id.name).toString());
                        break;
                    case "hand_user_3":
                        source = hand_user_3.getDrawable();
                        dropZone.setImageDrawable(source);
                        stat.add(hand_user_3.getTag(R.id.atk).toString());
                        stat.add(hand_user_3.getTag(R.id.def).toString());
                        manaDecrease(Integer.parseInt(hand_user_3.getTag(R.id.cost).toString()));
                        hand_user_3.setVisibility(View.INVISIBLE);
                        Helper.playVoice(this, hand_user_3.getTag(R.id.name).toString());
                        break;
                    case "hand_user_4":
                        source = hand_user_4.getDrawable();
                        dropZone.setImageDrawable(source);
                        stat.add(hand_user_4.getTag(R.id.atk).toString());
                        stat.add(hand_user_4.getTag(R.id.def).toString());
                        manaDecrease(Integer.parseInt(hand_user_4.getTag(R.id.cost).toString()));
                        hand_user_4.setVisibility(View.INVISIBLE);
                        Helper.playVoice(this, hand_user_4.getTag(R.id.name).toString());
                        break;
                    case "hand_user_5":
                        source = hand_user_5.getDrawable();
                        dropZone.setImageDrawable(source);
                        stat.add(hand_user_5.getTag(R.id.atk).toString());
                        stat.add(hand_user_5.getTag(R.id.def).toString());
                        manaDecrease(Integer.parseInt(hand_user_5.getTag(R.id.cost).toString()));
                        hand_user_5.setVisibility(View.INVISIBLE);
                        Helper.playVoice(this, hand_user_5.getTag(R.id.name).toString());
                        break;
                }
                setStat(stat, pos);

                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                if (event.getResult()) {
                    if (label == null)
                        return true;
                    Log.d("dragNDropEvent: ", label);


                }
                return true;

            default:
                return false;
        }
    }

    public boolean isInvokable(int cost) {
        Player player;
        if (currentPlayer == 1)
            player = player1;
        else
            player = player2;

        if (cost <= player.getMana())
        {

            return true;
        }
        return false;
    }

    @SuppressLint("DefaultLocale")
    public void manaDecrease(int cost) {
        Player player;
        if (currentPlayer == 1) {
            player = player1;
        } else {
            player = player2;
        }
        Log.d(TAG, "cost: "+cost);
        Log.d(TAG, "player mana: "+player.getMana());
        Log.d(TAG, "player mana - cost: "+ (player.getMana()-cost));
        user_mana.setText(Integer.toString((player.getMana()-cost))+"/"+Integer.toString(player.getMana()));
        int mana = player.getMana()-cost;
        player.setMana(mana);
    }

    @SuppressLint("SetTextI18n")
    public void setStat(ArrayList<String> stat, String pos) {
        switch (pos) {
            case "left":
                user_left.setText("atk : " + stat.get(0));
                user_left.setVisibility(View.VISIBLE);
                break;
            case "top":
                user_top.setText("def : " + stat.get(1));
                user_top.setVisibility(View.VISIBLE);
                break;
            case "right":
                user_right.setText("atk : " + stat.get(0));
                user_right.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onTaskCompleted(Boolean isFull) {

    }
}
