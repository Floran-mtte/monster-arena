package fr.arena.monster.monster_arena;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class gameBoardActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnDragListener, OnTaskCompleted {


    Party   party;
    Player  player1;
    Player  player2;

    ArrayList<Card>         player1Card = new ArrayList<>();
    ArrayList<Card>         player2Card = new ArrayList<>();

    ArrayList<Card>         discardCardPlayer1 = new ArrayList<>();
    ArrayList<Card>         discardCardPlayer2 = new ArrayList<>();

    Map<String, CardEntity> player1_board = new HashMap<>();
    Map<String, CardEntity> player2_board = new HashMap<>();

    Map<String, Object> current_player_hand;

    Helper helper = Helper.getInstance();
    String TAG = "gameBoardActivity";
    int currentPlayer, counter = 3, manaPLayer1, manaPLayer2;
    int swipeIndex = 0;
    CardEntity clickedCard;
    String clickedPos;
    String playerTurn;

    boolean attackMode = false;
    boolean spellModeAttack = false;
    boolean spellModeDefend = false;
    boolean controlMode = false;

    CountDownTimer clock;

    ImageView hand_user_1, hand_user_2, hand_user_3, hand_user_4, hand_user_5, cardDetail, user_attack_left, user_attack_right, user_defense, opponent_attack_left, opponent_attack_right, opponent_defense, hand_opponent_1, hand_opponent_2, hand_opponent_3, hand_opponent_4, hand_opponent_5, dropZone = null, end_tour_button, user_avatar, opponent_avatar, discard_user, discard_opponent, spell_zone, card_choosed;
    TextView user_left, user_top, user_right, opponent_left, opponent_top, opponent_right, user_mana, opponent_mana, user_life, opponent_life, timer, prize, swipe_text;
    FrameLayout filter;
    ConstraintLayout victory_layout, defeat_layout, reborn_modal;
    String label = null;
    OnTaskCompleted listener;
    ListenerRegistration registration;
    Button back_victory, back_defeat, revive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Helper.playTheme(this, "fight");

        setContentView(R.layout.activity_game_board);

        victory_layout = (ConstraintLayout) findViewById(R.id.victory_layout);
        defeat_layout = (ConstraintLayout) findViewById(R.id.defeat_layout);
        reborn_modal = (ConstraintLayout) findViewById(R.id.reborn_modal);
        prize = (TextView) findViewById(R.id.prize);

        card_choosed = (ImageView) findViewById(R.id.cardChoose);

        user_avatar = (ImageView) findViewById(R.id.user_avatar);
        user_life = (TextView) findViewById(R.id.user_life);
        user_mana = (TextView) findViewById(R.id.user_mana);

        hand_user_1 = (ImageView) findViewById(R.id.hand_user_1);
        hand_user_2 = (ImageView) findViewById(R.id.hand_user_2);
        hand_user_3 = (ImageView) findViewById(R.id.hand_user_3);
        hand_user_4 = (ImageView) findViewById(R.id.hand_user_4);
        hand_user_5 = (ImageView) findViewById(R.id.hand_user_5);
        discard_user = (ImageView) findViewById(R.id.discard_user);
        discard_opponent = (ImageView) findViewById(R.id.discard_oppenent);

        user_attack_left = (ImageView) findViewById(R.id.left_card_user);
        user_attack_right = (ImageView) findViewById(R.id.right_card_user);
        user_defense = (ImageView) findViewById(R.id.up_card_user);

        user_attack_left.setTag("left");
        user_defense.setTag("top");
        user_attack_right.setTag("right");

        user_left = (TextView) findViewById(R.id.left_card_user_attack);
        user_top = (TextView) findViewById(R.id.up_card_user_defense);
        user_right = (TextView) findViewById(R.id.right_card_user_attack);

        opponent_avatar = (ImageView) findViewById(R.id.opponent_avatar);
        opponent_life = (TextView) findViewById(R.id.opponent_life);
        opponent_mana = (TextView) findViewById(R.id.opponent_mana);

        timer = (TextView) findViewById(R.id.timer);
        swipe_text = (TextView) findViewById(R.id.swipe_text);

        hand_opponent_1 = (ImageView) findViewById(R.id.hand_opponent_1);
        hand_opponent_2 = (ImageView) findViewById(R.id.hand_opponent_2);
        hand_opponent_3 = (ImageView) findViewById(R.id.hand_opponent_3);
        hand_opponent_4 = (ImageView) findViewById(R.id.hand_opponent_4);
        hand_opponent_5 = (ImageView) findViewById(R.id.hand_opponent_5);

        opponent_attack_left = (ImageView) findViewById(R.id.left_card_opponent);
        opponent_attack_right = (ImageView) findViewById(R.id.right_card_opponent);
        opponent_defense = (ImageView) findViewById(R.id.up_card_opponent);
        end_tour_button = (ImageView) findViewById(R.id.end_tour_button);

        opponent_left = (TextView) findViewById(R.id.left_card_opponent_attack);
        opponent_top = (TextView) findViewById(R.id.up_card_opponent_defense);
        opponent_right = (TextView) findViewById(R.id.right_card_opponent_attack);

        cardDetail = (ImageView) findViewById(R.id.cardDetail);
        spell_zone = (ImageView) findViewById(R.id.spell_zone);
        filter = (FrameLayout) findViewById(R.id.filter);

        back_victory = (Button) findViewById(R.id.back_home_victory);
        back_defeat = (Button) findViewById(R.id.back_home_defeat);
        revive = (Button) findViewById(R.id.revive_btn);

        hand_user_1.setOnClickListener(this);
        hand_user_2.setOnClickListener(this);
        hand_user_3.setOnClickListener(this);
        hand_user_4.setOnClickListener(this);
        hand_user_5.setOnClickListener(this);
        filter.setOnClickListener(this);
        revive.setOnClickListener(this);

        hand_user_1.setOnLongClickListener(this);
        hand_user_2.setOnLongClickListener(this);
        hand_user_3.setOnLongClickListener(this);
        hand_user_4.setOnLongClickListener(this);
        hand_user_5.setOnLongClickListener(this);

        user_attack_left.setOnDragListener(this);
        user_attack_right.setOnDragListener(this);
        user_defense.setOnDragListener(this);
        spell_zone.setOnDragListener(this);

        user_attack_left.setOnClickListener(this);
        user_attack_right.setOnClickListener(this);
        user_defense.setOnClickListener(this);

        opponent_avatar.setOnClickListener(this);
        opponent_left.setOnClickListener(this);
        opponent_right.setOnClickListener(this);
        opponent_top.setOnClickListener(this);

        opponent_attack_left.setOnClickListener(this);
        opponent_attack_right.setOnClickListener(this);
        opponent_defense.setOnClickListener(this);

        end_tour_button.setOnClickListener(this);

        back_victory.setOnClickListener(this);
        back_defeat.setOnClickListener(this);

        card_choosed.setOnTouchListener(new OnSwipeTouchListener(gameBoardActivity.this) {
            public void onSwipeTop() {
               return;
            }
            public void onSwipeRight() {
                if(swipeIndex > 0) {
                    swipeIndex--;
                    Drawable path = getDrawable(getResources().getIdentifier(discardCardPlayer1.get(swipeIndex).getAssetPath(), "drawable", getPackageName()));
                    card_choosed.setImageDrawable(path);
                    swipe_text.setText(swipeIndex+1+"/"+discardCardPlayer1.size());
                    card_choosed.setTag(R.id.name,discardCardPlayer1.get(swipeIndex).assetPath);
                    card_choosed.setTag(R.id.index,swipeIndex);
                }
            }
            public void onSwipeLeft() {
                if(swipeIndex < discardCardPlayer1.size()) {
                    swipeIndex++;
                    Drawable path = getDrawable(getResources().getIdentifier(discardCardPlayer1.get(swipeIndex).getAssetPath(), "drawable", getPackageName()));
                    card_choosed.setImageDrawable(path);
                    swipe_text.setText(swipeIndex+1+"/"+discardCardPlayer1.size());
                    card_choosed.setTag(R.id.name,discardCardPlayer1.get(swipeIndex).assetPath);
                    card_choosed.setTag(R.id.index,swipeIndex);
                    Log.d("spell_card_reborn","index swipe"+swipeIndex);
                    Log.d("spell_card_reborn","asset_path"+discardCardPlayer1.get(swipeIndex).assetPath);

                }
            }
            public void onSwipeBottom() {
                return;
            }

        });

        String partyId = getIntent().getStringExtra("partyId");
        String player_1 = getIntent().getStringExtra("player_1");
        String player_2 = getIntent().getStringExtra("player_2");
        playerTurn = getIntent().getStringExtra("current_player");
        initParty(partyId, player_1, player_2);

        Log.d(TAG,"id = "+playerTurn);
        Log.d(TAG,"id user ="+helper.mAuth.getUid());

        if(party.getNumberRound() == 1 && currentPlayer == 1)
        {
            startTimer();
        }
        watchOtherMove();
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
        player1 = new Player(player_1, "john", 4000, 3,2);
        player2 = new Player(player_2, "daenerys", 4000, 2,2);
        manaPLayer1 = player1.getMana() -1;
        manaPLayer2 = player2.getMana();


        currentPlayer = currentPlayer();
        switch (currentPlayer) {
            case 1:
                //setPlayer1Info();
                player1.setPlayerInfo(party.getId(), 1);
                user_life.setText(String.format("%s%d", user_life.getText().toString(), player1.getLifepoint()));
                user_mana.setText(String.format("%d/%d", player1.getMana(), player1.getMana()-1));

                opponent_life.setText(String.format("%s%d", opponent_life.getText().toString(), player2.getLifepoint()));
                opponent_mana.setText(String.format("%d/%d", player2.getMana(), player2.getMana()));

                //todo : show of user wich player is

                getPlayer1Card(player_1);
                listener = new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(Boolean isFull) {
                        Log.d(TAG, String.valueOf(currentPlayer));
                        if(isFull)
                        {
                            ImageView[] hands = new ImageView[5];
                            Map<String, Object> player1Hand = new HashMap<>();

                            hands[0] = hand_user_1;
                            hands[1] = hand_user_2;
                            hands[2] = hand_user_3;
                            hands[3] = hand_user_4;
                            hands[4] = hand_user_5;

                            Collections.shuffle(player1Card);

                            for (int i=0; i < 3; i++) {
                                Card parent = player1Card.get(i);
                               player1Card.get(i).setInHand(true);
                                player1Hand.put("hand"+(i+1), parent);
                                if (parent instanceof CardEntity) {
                                    CardEntity card = (CardEntity) parent;
                                    Drawable path = getDrawable(getResources()
                                            .getIdentifier(card.getAssetPath(), "drawable", getPackageName()));
                                    hands[i].setImageDrawable(path);
                                    hands[i].setTag(R.id.id, card.getId());
                                    hands[i].setTag(R.id.typeCard, 1);
                                    hands[i].setTag(R.id.atk, card.getAttack());
                                    hands[i].setTag(R.id.def, card.getDefend());
                                    hands[i].setTag(R.id.cost, card.getLevel());
                                    hands[i].setTag(R.id.name, card.getAssetPath());
                                    hands[i].setTag(R.id.index, Integer.toString(i));
                                    hands[i].setVisibility(View.VISIBLE);
                                }
                                else if(parent instanceof CardSpell) {
                                    CardSpell card = (CardSpell) parent;
                                    Drawable path = getDrawable(getResources()
                                            .getIdentifier(card.getAssetPath(), "drawable", getPackageName()));
                                    hands[i].setImageDrawable(path);
                                    hands[i].setTag(R.id.id, card.getId());
                                    hands[i].setTag(R.id.typeCard, 2);
                                    hands[i].setTag(R.id.cost, card.getLevel());
                                    hands[i].setTag(R.id.name, card.getAssetPath());
                                    hands[i].setTag(R.id.effect, card.getIdEffect());
                                    hands[i].setTag(R.id.index, Integer.toString(i));
                                    hands[i].setVisibility(View.VISIBLE);
                                }
                            }
                            player1.setHand(player1Hand);
                            player1.setPlayerInfo(party.getId(),1);
                        }
                    }
                };
                break;
            case 2:
                player2.setPlayerInfo(party.getId(), 2);

                user_life.setText(String.format("%s%d", user_life.getText().toString(), player2.getLifepoint()));
                user_mana.setText(String.format("%d/%d", player2.getMana(), player2.getMana()));

                opponent_life.setText(String.format("%s%d", opponent_life.getText().toString(), player1.getLifepoint()));
                opponent_mana.setText(String.format("%d/%d", player1.getMana(), player1.getMana()-1));

                //todo : showoff playerTurn

                getPlayer2Card(player_2);
                listener = new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(Boolean isFull) {
                        Log.d(TAG, String.valueOf(currentPlayer));
                        if(isFull)
                        {
                            ImageView[] hands = new ImageView[5];
                            Map<String, Object> player2Hand = new HashMap<>();

                            hands[0] = hand_user_1;
                            hands[1] = hand_user_2;
                            hands[2] = hand_user_3;
                            hands[3] = hand_user_4;
                            hands[4] = hand_user_5;

                            Collections.shuffle(player2Card);

                            for (int i=0; i < 3; i++) {
                                Card parent = player2Card.get(i);
                                player2Card.get(i).setInHand(true);
                                player2Hand.put("hand"+(i+1), parent);
                                if (parent instanceof CardEntity) {
                                    CardEntity card = (CardEntity) parent;
                                    Drawable path = getDrawable(getResources()
                                            .getIdentifier(card.getAssetPath(), "drawable", getPackageName()));
                                    Log.d(TAG, card.getAssetPath());
                                    hands[i].setImageDrawable(path);
                                    hands[i].setTag(R.id.id, card.getId());
                                    hands[i].setTag(R.id.typeCard, 1);
                                    hands[i].setTag(R.id.atk, card.getAttack());
                                    hands[i].setTag(R.id.def, card.getDefend());
                                    hands[i].setTag(R.id.cost, card.getLevel());
                                    hands[i].setTag(R.id.name, card.getAssetPath());
                                    hands[i].setTag(R.id.index, Integer.toString(i));
                                    hands[i].setVisibility(View.VISIBLE);
                                }
                                else if(parent instanceof CardSpell) {
                                    CardSpell card = (CardSpell) parent;
                                    Drawable path = getDrawable(getResources()
                                            .getIdentifier(card.getAssetPath(), "drawable", getPackageName()));
                                    hands[i].setImageDrawable(path);
                                    hands[i].setTag(R.id.id, card.getId());
                                    hands[i].setTag(R.id.typeCard, 2);
                                    hands[i].setTag(R.id.cost, card.getLevel());
                                    hands[i].setTag(R.id.name, card.getAssetPath());
                                    hands[i].setTag(R.id.effect, card.getIdEffect());
                                    hands[i].setTag(R.id.index, Integer.toString(i));
                                    hands[i].setVisibility(View.VISIBLE);
                                }
                            }
                            player2.setHand(player2Hand);
                            player2.setPlayerInfo(party.getId(),2);
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

                                if(listDoc.size() == playerCard.size())
                                {
                                    listener.onTaskCompleted(true);
                                }

                            }
                            else if(obj.get("card_detail").equals("spell")) {
                                CardSpell card = new CardSpell(
                                        obj.get("asset_path").toString(),
                                        obj.get("id").toString(),
                                        Integer.parseInt(obj.get("cost").toString()),
                                        obj.get("name").toString(),
                                        Integer.parseInt(obj.get("type_card").toString()),
                                        obj.get("card_detail").toString(),
                                        Integer.parseInt(obj.get("id_effect").toString())
                                );

                                playerCard.add(card);

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

    public void addCardToParty(Map<String, Object> playerCard) {

        /*ArrayList playerDeck = new ArrayList();
        for (Map.Entry<String, Object> parent : playerCard.entrySet()) {
            if (parent.getValue() instanceof CardEntity) {
                playerDeck.add((CardEntity) parent.getValue());
            }
        }*/
        Map<String, Object> party_container = new HashMap<>();
        if (currentPlayer == 1) {
            Map<String, Object> player1Info = new HashMap<>();
            player1Info.put("hand", current_player_hand);
            party_container.put("player1Info", player1Info);
            addHandToDb(party_container);
        } else {
            Map<String, Object> player2Info = new HashMap<>();
            player2Info.put("hand", current_player_hand);
            party_container.put("player2Info", player2Info);
            addHandToDb(party_container);
        }
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
        ImageView card_container;
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
            case R.id.revive_btn:
               reborn_modal.setVisibility(View.INVISIBLE);
               swipe_text.setText("");

               ImageView dropPos = null;
               if(user_left.getVisibility() == View.INVISIBLE) {
                   dropPos = user_attack_left;
               }
               else if(user_right.getVisibility() == View.INVISIBLE) {
                   dropPos = user_attack_right;
               }
               else if(user_top.getVisibility() == View.INVISIBLE) {
                   dropPos = user_defense;
               }

               if(dropPos != null) {
                   CardEntity card_selected = null;
                   int index = 0;
                   if(currentPlayer == 1) {
                       Card discardCard = discardCardPlayer1.get(Integer.parseInt(card_choosed.getTag(R.id.index).toString()));
                       index = discardCard.getIndex();
                       player1Card.get(index).setActive(true);
                       player1Card.get(index).setOnGround(true);
                       player1Card.get(index).setInHand(false);
                       card_selected = (CardEntity) discardCardPlayer1.get(Integer.parseInt(card_choosed.getTag(R.id.index).toString()));
                   }
                   else if(currentPlayer == 2) {
                       Card discardCard = discardCardPlayer2.get(Integer.parseInt(card_choosed.getTag(R.id.index).toString()));
                       index = discardCard.getIndex();
                       player2Card.get(index).setActive(true);
                       player2Card.get(index).setOnGround(true);
                       player2Card.get(index).setInHand(false);
                       card_selected = (CardEntity) discardCardPlayer2.get(Integer.parseInt(card_choosed.getTag(R.id.index).toString()));
                   }

                   String assetPath = card_choosed.getTag(R.id.name).toString();
                   Drawable path = getDrawable(getResources().getIdentifier(assetPath, "drawable", getPackageName()));
                   dropPos.setImageDrawable(path);
                   ArrayList<String> stat = new ArrayList<>();
                   String attack = Integer.toString(card_selected.getAttack());
                   String defend = Integer.toString(card_selected.getDefend());
                   stat.add(attack);
                   stat.add(defend);
                   Helper.playVoice(this, card_choosed.getTag(R.id.name).toString());
                   setStat(stat, dropPos);
                   sendPlayerBoard(dropPos, index, "null");
               }


               break;
            case R.id.left_card_opponent:
                if(playerTurn.equals(helper.mAuth.getUid()))
                {
                    if(controlMode) {
                        controlMode = false;

                        ImageView dropPosUser = null;
                        if(user_left.getVisibility() == View.INVISIBLE) {
                            dropPosUser = user_attack_left;
                        }
                        else if(user_right.getVisibility() == View.INVISIBLE) {
                            dropPosUser = user_attack_right;
                        }
                        else if(user_top.getVisibility() == View.INVISIBLE) {
                            dropPosUser = user_defense;
                        }

                        if(dropPosUser != null) {
                            if(currentPlayer == 1) {

                                Map<String, Object> board_opponent = player2.getBoard();
                                Map<String, Object> board_left = (Map<String,Object>) board_opponent.get("board-left");
                                Map<String, Object> opponent_card = (Map<String,Object>) board_left.get("card");
                                Log.d("spell_card_control","opp card :"+opponent_card.toString());
                               CardEntity new_card = new CardEntity(
                                        opponent_card.get("assetPath").toString(),
                                        Integer.parseInt(opponent_card.get("defend").toString()),
                                        Integer.parseInt(opponent_card.get("attack").toString()),
                                        opponent_card.get("id").toString(),
                                        Integer.parseInt(opponent_card.get("level").toString()),
                                        opponent_card.get("name").toString(),
                                        Integer.parseInt(opponent_card.get("type_card").toString()),
                                        opponent_card.get("cardDetail").toString());
                                player1Card.add(new_card);

                                player2.deleteCardFromBoard("left");
                                player2.setPlayerInfo(party.getId(), 2);

                                String assetPath = new_card.getAssetPath();
                                Drawable path = getDrawable(getResources().getIdentifier(assetPath, "drawable", getPackageName()));
                                dropPosUser.setImageDrawable(path);
                                ArrayList<String> stat = new ArrayList<>();
                                String attack = Integer.toString(new_card.getAttack());
                                String defend = Integer.toString(new_card.getDefend());
                                stat.add(attack);
                                stat.add(defend);
                                Log.d("spell_card_control",new_card.getName());
                                Helper.playVoice(this, new_card.getAssetPath());
                                setStat(stat, dropPosUser);
                                sendPlayerBoard(dropPosUser, player1Card.size()-1, "null");

                            }
                            else if(currentPlayer == 2) {

                            }
                        }

                    }
                    else {
                        if(clickedCard != null) {
                            if(attackMode) {
                                if(opponent_top.getVisibility() == View.INVISIBLE) {
                                    card_container = getClickedCard(clickedPos);
                                    if(!(Boolean) card_container.getTag(R.id.fight)) {
                                        card_container.setTag(R.id.fight, true);
                                        fight("left");
                                    }
                                    else {
                                        Log.d("fought","already fight");
                                    }
                                }
                                else {
                                    Log.d("fight","defensive card must be attacked first");
                                }
                            }
                        }
                    }
                }
                break;
            case R.id.right_card_opponent:
                if(playerTurn.equals(helper.mAuth.getUid())) {
                    if(clickedCard != null){
                        if(attackMode) {
                            if(opponent_top.getVisibility() == View.INVISIBLE) {
                                card_container = getClickedCard(clickedPos);
                                if(!(Boolean) card_container.getTag(R.id.fight)) {
                                    card_container.setTag(R.id.fight, true);
                                    fight("right");
                                }
                                else {
                                    Log.d("fought", "already fight");
                                }
                            }
                            else {
                                Log.d("fight","defensive card must be attacked first");
                            }
                        }
                    }
                }
                break;
            case R.id.up_card_opponent:
                if(playerTurn.equals(helper.mAuth.getUid())) {
                    if(attackMode) {
                        if(clickedCard != null) {
                            card_container = getClickedCard(clickedPos);
                            if(!(Boolean) card_container.getTag(R.id.fight)) {
                                card_container.setTag(R.id.fight, true);
                                fight("top");
                            }
                            else {
                                Log.d("fought", "already fight");
                            }
                        }
                    }
                }
                break;
            case R.id.opponent_avatar:
                Log.d("avatar","click sur avatar");
                if(playerTurn.equals(helper.mAuth.getUid())) {
                    if(attackMode) {
                        if(clickedCard != null) {
                            card_container = getClickedCard(clickedPos);
                            if(!(Boolean) card_container.getTag(R.id.fight)) {
                                if (
                                        opponent_right.getVisibility() == View.INVISIBLE &&
                                                opponent_left.getVisibility() == View.INVISIBLE &&
                                                opponent_top.getVisibility() == View.INVISIBLE) {
                                    card_container.setTag(R.id.fight, true);
                                    attackPlayer();
                                }
                            }
                            else {
                                Log.d("fought", "already fight");
                            }
                        }
                    }

                }
                break;
            case R.id.left_card_user:
                if(playerTurn.equals(helper.mAuth.getUid())) {
                    setClickedCard("left");
                    clickedPos = "left";
                    if(spellModeAttack) {
                        spellModeAttack = false;
                        if(currentPlayer == 1) {
                           CardEntity card = (CardEntity) player1Card.get(clickedCard.getIndex());
                           card.setAttack(card.getAttack()+300);
                           player1Card.set(clickedCard.getIndex(),card);
                           Map<String, Object> player1Board = player1.getBoard();
                           Map<String, Object> cardInfo = new HashMap<>();
                           cardInfo = (Map<String, Object>) player1Board.get("board-left");
                           cardInfo.replace("card",card);
                           player1Board.replace("board-left",cardInfo);
                           player1.setBoard(player1Board);
                           player1.setPlayerInfo(party.getId(), 1);
                           user_left.setText("ATK :"+card.getAttack());
                        }
                        else if(currentPlayer == 2) {
                            CardEntity card = (CardEntity) player2Card.get(clickedCard.getIndex());
                            card.setAttack(card.getAttack()+300);
                            player2Card.set(clickedCard.getIndex(),card);
                            Map<String, Object> player2Board = player2.getBoard();
                            Map<String, Object> cardInfo = new HashMap<>();
                            cardInfo = (Map<String, Object>) player2Board.get("board-left");
                            cardInfo.replace("card",card);
                            player2Board.replace("board-left",cardInfo);
                            player2.setBoard(player2Board);
                            player2.setPlayerInfo(party.getId(), 2);
                            user_left.setText("ATK :"+card.getAttack());
                        }
                    }
                    else {
                        attackMode = true;
                    }
                }
                break;
            case R.id.right_card_user:
                if(playerTurn.equals(helper.mAuth.getUid())) {
                    setClickedCard("right");
                    clickedPos = "right";
                    if(spellModeAttack) {
                        spellModeAttack = false;
                        if(currentPlayer == 1) {
                            CardEntity card = (CardEntity) player1Card.get(clickedCard.getIndex());
                            card.setAttack(card.getAttack()+300);
                            player1Card.set(clickedCard.getIndex(),card);
                            Map<String, Object> player1Board = player1.getBoard();
                            Map<String, Object> cardInfo = new HashMap<>();
                            cardInfo = (Map<String, Object>) player1Board.get("board-right");
                            cardInfo.replace("card",card);
                            player1Board.replace("board-right",cardInfo);
                            player1.setBoard(player1Board);
                            player1.setPlayerInfo(party.getId(), 1);
                            user_right.setText("ATK :"+card.getAttack());
                        }
                        else if(currentPlayer == 2) {
                            CardEntity card = (CardEntity) player2Card.get(clickedCard.getIndex());
                            card.setAttack(card.getAttack()+300);
                            player2Card.set(clickedCard.getIndex(),card);
                            Map<String, Object> player2Board = player2.getBoard();
                            Map<String, Object> cardInfo = new HashMap<>();
                            cardInfo = (Map<String, Object>) player2Board.get("board-right");
                            cardInfo.replace("card",card);
                            player2Board.replace("board-right",cardInfo);
                            player2.setBoard(player2Board);
                            player2.setPlayerInfo(party.getId(), 2);
                            user_right.setText("ATK :"+card.getAttack());
                        }
                    }
                    else {
                        attackMode = true;
                    }
                }
                break;
            case R.id.up_card_user:
                if(playerTurn.equals(helper.mAuth.getUid())) {
                    setClickedCard("top");
                    if(spellModeDefend) {
                        spellModeDefend = false;
                        if(currentPlayer == 1) {
                            CardEntity card = (CardEntity) player1Card.get(clickedCard.getIndex());
                            card.setDefend(card.getDefend()+300);
                            player1Card.set(clickedCard.getIndex(),card);
                            Map<String, Object> player1Board = player1.getBoard();
                            Map<String, Object> cardInfo = new HashMap<>();
                            cardInfo = (Map<String, Object>) player1Board.get("board-top");
                            cardInfo.replace("card",card);
                            player1Board.replace("board-top",cardInfo);
                            player1.setBoard(player1Board);
                            player1.setPlayerInfo(party.getId(), 1);
                            user_top.setText("DEF :"+card.getDefend());
                        }
                        else if(currentPlayer == 2) {
                            CardEntity card = (CardEntity) player2Card.get(clickedCard.getIndex());
                            card.setAttack(card.getDefend()+300);
                            player2Card.set(clickedCard.getIndex(),card);
                            Map<String, Object> player2Board = player2.getBoard();
                            Map<String, Object> cardInfo = new HashMap<>();
                            cardInfo = (Map<String, Object>) player2Board.get("board-top");
                            cardInfo.replace("card",card);
                            player2Board.replace("board-top",cardInfo);
                            player2.setBoard(player2Board);
                            player2.setPlayerInfo(party.getId(), 2);
                            user_top.setText("DEF :"+card.getDefend());
                        }
                    }
                }
                break;
            case R.id.end_tour_button:
                if(playerTurn.equals(helper.mAuth.getUid()))
                {
                    updatePlayerTurn();
                }
                break;
            case R.id.back_home_victory:
                goToHome();
                break;
            case R.id.back_home_defeat:
                goToHome();
                break;
        }
    }

    public ImageView getClickedCard(String pos) {
        switch (pos) {
            case "left":
                return user_attack_left;
            case "right":
                return user_attack_right;
        }
        return null;
    }
    public void goToHome() {
        finish();
        Intent intent = new Intent(this, homePageActivity.class);
        startActivity(intent);
    }

    public void setClickedCard(String pos) {
        attackMode = true;
        Log.d("battle", "dans le click user");
        if(currentPlayer == 1) {

            Map<String, Object> board = player1.getBoard();

            Map<String, Object> row;

            for (Map.Entry<String, Object> entry : board.entrySet()) {
                row = (Map<String, Object>) entry.getValue();
                if(row.get("pos").toString().equals(pos)) {
                    Map<String, Object> card = ( Map<String, Object>) row.get("card");
                    clickedCard = new CardEntity(
                            card.get("assetPath").toString(),
                            Integer.parseInt(card.get("defend").toString()),
                            Integer.parseInt(card.get("attack").toString()),
                            card.get("id").toString(),
                            Integer.parseInt(card.get("level").toString()),
                            card.get("name").toString(),
                            1,
                            ""
                    );

                    Log.d("index_debug","index"+row.get("index").toString());
                    clickedCard.setIndex(Integer.parseInt(row.get("index").toString()));
                }
            }

        }
        else if(currentPlayer == 2) {

            Map<String, Object> board = player2.getBoard();

            Map<String, Object> row;

            for (Map.Entry<String, Object> entry : board.entrySet()) {
                row = (Map<String, Object>) entry.getValue();
                if(row.get("pos").toString().equals(pos)) {
                    Map<String, Object> card = ( Map<String, Object>) row.get("card");
                    clickedCard = new CardEntity(
                            card.get("assetPath").toString(),
                            Integer.parseInt(card.get("defend").toString()),
                            Integer.parseInt(card.get("attack").toString()),
                            card.get("id").toString(),
                            Integer.parseInt(card.get("level").toString()),
                            card.get("name").toString(),
                            1,
                            ""
                    );
                    Log.d("index_debug","index"+row.get("index").toString());
                    clickedCard.setIndex(Integer.parseInt(row.get("index").toString()));
                }
            }

        }
    }

    public void attackPlayer() {
        if(currentPlayer == 1) {
            int atk = clickedCard.getAttack();
            player2.setLifepoint(player2.getLifepoint() - atk);
            opponent_life.setText("LP : "+player2.getLifepoint());
            player2.setPlayerInfo(party.getId(), 2);
        }
        else if(currentPlayer == 2) {
            int atk = clickedCard.getAttack();
            player1.setLifepoint(player1.getLifepoint() - atk);
            opponent_life.setText("LP : "+player1.getLifepoint());
            player1.setPlayerInfo(party.getId(), 1);
        }
    }
    public void fight(String pos) {
        Log.d("battle","dans le click opponent");
        if(attackMode) {
            attackMode = false;

            Map<String, Object> board = new ConcurrentHashMap<>();
            if(currentPlayer == 1) {
                board = player2.getBoard();
            }
            else {
                board = player1.getBoard();
            }

            Map<String, Object> row;
            Iterator<Map.Entry<String, Object>> itr = board.entrySet().iterator();
            while(itr.hasNext()){

                String key = itr.next().getKey();
                Object value = board.get(key);

                row = (Map<String, Object>) value;
                if(row.get("pos").toString().equals(pos)) {
                    Map<String, Object> card = ( Map<String, Object>) row.get("card");
                    int difference = clickedCard.getAttack() - Integer.parseInt(card.get("attack").toString());
                    if(pos.equals("top")) {
                        difference = clickedCard.getAttack() - Integer.parseInt(card.get("defend").toString());
                    }

                    Log.d("battle","diff : "+difference);
                    if(difference > 0) {
                        Log.d("battle","position : "+pos);
                        winFight(pos, difference, itr);
                        break;
                    }
                    else if(difference < 0) {
                        Log.d("battle","position : "+pos);
                        loseFight(clickedPos, difference);
                        break;
                    }
                    else {
                        Log.d("battle","position : "+pos);
                        equalsFight(pos, itr);
                        break;
                    }
                }
            }
        }
    }

    public void winFight(String pos, int difference, Iterator itr) {


        Log.d("battle","dans le diff > 0");
        itr.remove();
        Drawable path = getDrawable(getResources().getIdentifier("card_section", "drawable", getPackageName()));
        int index = 0;
        switch (pos) {
            case "left":
                index = Integer.parseInt(opponent_attack_left.getTag(R.id.index).toString());
                opponent_attack_left.setImageDrawable(path);
                opponent_attack_left.setTag(null);
                opponent_attack_left.setTag("left");
                opponent_left.setText("");
                opponent_left.setVisibility(View.INVISIBLE);
                break;
            case "right":
                index = Integer.parseInt(opponent_attack_right.getTag(R.id.index).toString());
                opponent_attack_right.setImageDrawable(path);
                opponent_attack_right.setTag(null);
                opponent_attack_right.setTag("right");
                opponent_right.setText("");
                opponent_right.setVisibility(View.INVISIBLE);
                break;
            case "top":
                index = Integer.parseInt(opponent_defense.getTag(R.id.index).toString());
                opponent_defense.setImageDrawable(path);
                opponent_defense.setTag(null);
                opponent_defense.setTag("top");
                opponent_top.setText("");
                opponent_top.setVisibility(View.INVISIBLE);
                break;
        }

        if(currentPlayer == 1) {
            Drawable verso_card = getDrawable(getResources().getIdentifier("verso", "drawable", getPackageName()));
            discard_opponent.setImageDrawable(verso_card);

            player2.deleteCardFromBoard(pos);
            player2.addToDiscard(index);
            player2.setLifepoint(player2.getLifepoint() - difference);
            opponent_life.setText(player2.getLifepoint()+"");
            player2.setPlayerInfo(party.getId(), 2);
            setAdvBoard(player2.getBoard());
        }
        else {
            if(player2.getDiscarding() == null || player2.getDiscarding().size() == 0) {
                Drawable verso_card = getDrawable(getResources().getIdentifier("verso", "drawable", getPackageName()));
                discard_opponent.setImageDrawable(verso_card);
            }

            player1.deleteCardFromBoard(pos);
            player1.addToDiscard(index);
            player1.setLifepoint(player1.getLifepoint() - difference);
            opponent_life.setText(player1.getLifepoint()+"");
            player1.setPlayerInfo(party.getId(), 1);
            setAdvBoard(player1.getBoard());
        }
    }

    public void loseFight(String pos, int difference) {
        Log.d("battle","dans le diff < 0");

        Drawable path = getDrawable(getResources().getIdentifier("card_section", "drawable", getPackageName()));
        switch (clickedPos) {
            case "left":
                user_attack_left.setImageDrawable(path);
                user_attack_left.setTag(null);
                user_attack_left.setTag(R.id.fight, false);
                user_attack_left.setTag("left");
                user_left.setText("");
                user_left.setVisibility(View.INVISIBLE);
                break;
            case "right":
                user_attack_right.setImageDrawable(path);
                user_attack_right.setTag(null);
                user_attack_right.setTag(R.id.fight, false);
                user_attack_right.setTag("right");
                user_right.setText("");
                user_right.setVisibility(View.INVISIBLE);
                break;
            case "top":
                user_defense.setImageDrawable(path);
                user_defense.setTag(null);
                user_defense.setTag("top");
                user_top.setText("");
                user_top.setVisibility(View.INVISIBLE);
                break;
        }

        if(currentPlayer == 1) {
            Drawable verso_card = getDrawable(getResources().getIdentifier("verso", "drawable", getPackageName()));
            discard_user.setImageDrawable(verso_card);

            int offset = clickedCard.getIndex();
            player1Card.get(offset).setActive(false);
            player1Card.get(offset).setInHand(false);
            player1Card.get(offset).setOnGround(false);

            clickedCard.setOnGround(false);
            clickedCard.setInHand(false);
            player1.deleteCardFromBoard(clickedPos);
            player1.setLifepoint(player1.getLifepoint() + difference);
            user_life.setText(player1.getLifepoint()+"");
            player1.setPlayerInfo(party.getId(), 1);
        }
        else {

            Drawable verso_card = getDrawable(getResources().getIdentifier("verso", "drawable", getPackageName()));
            discard_user.setImageDrawable(verso_card);

            int offset = clickedCard.getIndex();
            player2Card.get(offset).setActive(false);
            player2Card.get(offset).setInHand(false);
            player2Card.get(offset).setOnGround(false);
            player2.deleteCardFromBoard(clickedPos);
            player2.setLifepoint(player2.getLifepoint() + difference);
            user_life.setText(player2.getLifepoint()+"");
            player2.setPlayerInfo(party.getId(), 2);
        }
    }

    public void equalsFight(String pos, Iterator itr) {
        Log.d("battle","dans le diff = 0");
        itr.remove();

        Drawable verso_card = getDrawable(getResources().getIdentifier("verso", "drawable", getPackageName()));
        discard_user.setImageDrawable(verso_card);
        discard_opponent.setImageDrawable(verso_card);


        Drawable path = getDrawable(getResources().getIdentifier("card_section", "drawable", getPackageName()));
        int index = 0;
        switch (pos) {
            case "left":
                index = Integer.parseInt(opponent_attack_left.getTag(R.id.index).toString());
                opponent_attack_left.setImageDrawable(path);
                opponent_attack_left.setTag(null);
                opponent_attack_left.setTag("left");
                opponent_left.setText("");
                opponent_left.setVisibility(View.INVISIBLE);
                break;
            case "right":
                index = Integer.parseInt(opponent_attack_right.getTag(R.id.index).toString());
                opponent_attack_right.setImageDrawable(path);
                opponent_attack_right.setTag(null);
                opponent_attack_right.setTag("right");
                opponent_right.setText("");
                opponent_right.setVisibility(View.INVISIBLE);
                break;
            case "top":
                index = Integer.parseInt(opponent_defense.getTag(R.id.index).toString());
                opponent_defense.setImageDrawable(path);
                opponent_defense.setTag(null);
                opponent_defense.setTag("top");
                opponent_top.setText("");
                opponent_top.setVisibility(View.INVISIBLE);
                break;
        }

        switch (clickedPos) {
            case "left":
                user_attack_left.setImageDrawable(path);
                user_attack_left.setTag(null);
                user_attack_left.setTag(R.id.fight, false);
                user_attack_left.setTag("left");
                user_left.setText("");
                user_left.setVisibility(View.INVISIBLE);
                break;
            case "right":
                user_attack_right.setImageDrawable(path);
                user_attack_right.setTag(null);
                user_attack_right.setTag(R.id.fight, false);
                user_attack_right.setTag("right");
                user_right.setText("");
                user_right.setVisibility(View.INVISIBLE);
                break;

            case "top":
                user_defense.setImageDrawable(path);
                user_defense.setTag(null);
                user_defense.setTag("top");
                user_top.setText("");
                user_top.setVisibility(View.INVISIBLE);
                break;
        }



        if(currentPlayer == 1) {
            int offset = clickedCard.getIndex();
            player1Card.get(offset).setActive(false);
            player1Card.get(offset).setInHand(false);
            player1Card.get(offset).setOnGround(false);
            player1.deleteCardFromBoard(clickedPos);
            player2.deleteCardFromBoard(pos);
            player2.addToDiscard(index);
            setAdvBoard(player2.getBoard());
        }
        else {
            int offset = clickedCard.getIndex();
            player2Card.get(offset).setActive(false);
            player2Card.get(offset).setInHand(false);
            player2Card.get(offset).setOnGround(false);
            player2.deleteCardFromBoard(clickedPos);
            player1.deleteCardFromBoard(pos);
            player1.addToDiscard(index);
            setAdvBoard(player1.getBoard());
        }

        player1.setPlayerInfo(party.getId(), 1);
        player2.setPlayerInfo(party.getId(), 2);

    }

    public void updatePlayerTurn()
    {
        clickedPos = "";
        clickedCard = null;
        user_attack_left.setTag(R.id.fight, false);
        user_attack_right.setTag(R.id.fight, false);
        clock.cancel();
        counter = 30;
        timer.setText(String.valueOf(counter));
        Log.d(TAG,"current player = "+currentPlayer);
        if(currentPlayer == 1)
        {
            playerTurn = player2.getId();
            Log.d(TAG,"player 2 info"+player2.getId());
        }
        else if (currentPlayer == 2)
        {
            playerTurn = player1.getId();
            Log.d(TAG,"player 1 info"+player1.getId());
        }


        party.setNumberRound(party.getNumberRound() + 1);

        Map<String, Object> playerInfo = new HashMap<>();
        Map<String, Object> turn = new HashMap<>();
        turn.put("current_player", playerTurn);
        turn.put("number_round", party.getNumberRound());

        if(currentPlayer == 1)
        {
            manaPLayer1++;
            player1.setMana(manaPLayer1);
            playerInfo.put("manaMax",player1.getMana());
            playerInfo.put("mana",player1.getMana());
            turn.put("player1Info",playerInfo);
            user_mana.setText(String.format("%d/%d", player1.getMana(), manaPLayer1));
        }
        else if(currentPlayer == 2)
        {
            manaPLayer2++;
            player2.setMana(manaPLayer2);
            playerInfo.put("manaMax",player2.getMana());
            playerInfo.put("mana",player2.getMana());
            turn.put("player2Info",playerInfo);
            Log.d(TAG,"Mana player 2 => "+player2.getMana());
            Log.d(TAG,"Mana max player 2 => "+manaPLayer2);
            user_mana.setText(String.format("%d/%d", player2.getMana(), manaPLayer2));
        }


        Helper.getInstance().db.collection("Party").document(party.getId())
                .set(turn, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>(){
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }
    @Override
    public boolean onLongClick(View v) {
        if (!playerTurn.equals(Helper.getInstance().mAuth.getUid())) {
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
                TextView dropPos = getDropPos(dropZone);
                int typeCard = 0;
                int effect = 0;
                if(dropZone.getId() != R.id.spell_zone) {
                    if(dropPos != null && dropPos.getVisibility() == View.INVISIBLE) {
                        switch (label) {
                            case "hand_user_1":
                                isInvokable = isInvokable(Integer.parseInt(hand_user_1.getTag(R.id.cost).toString()));
                                typeCard = Integer.parseInt(hand_user_1.getTag(R.id.typeCard).toString());
                                if(typeCard == 2) {
                                    effect = Integer.parseInt(hand_user_1.getTag(R.id.effect).toString());
                                }
                                break;
                            case "hand_user_2":
                                isInvokable = isInvokable(Integer.parseInt(hand_user_2.getTag(R.id.cost).toString()));
                                typeCard = Integer.parseInt(hand_user_2.getTag(R.id.typeCard).toString());
                                if(typeCard == 2) {
                                    effect = Integer.parseInt(hand_user_2.getTag(R.id.effect).toString());
                                }
                                break;
                            case "hand_user_3":
                                isInvokable = isInvokable(Integer.parseInt(hand_user_3.getTag(R.id.cost).toString()));
                                typeCard = Integer.parseInt(hand_user_3.getTag(R.id.typeCard).toString());
                                if(typeCard == 2) {
                                    effect = Integer.parseInt(hand_user_3.getTag(R.id.effect).toString());
                                }
                                break;
                            case "hand_user_4":
                                isInvokable = isInvokable(Integer.parseInt(hand_user_4.getTag(R.id.cost).toString()));
                                typeCard = Integer.parseInt(hand_user_4.getTag(R.id.typeCard).toString());
                                if(typeCard == 2) {
                                    effect = Integer.parseInt(hand_user_4.getTag(R.id.effect).toString());
                                }
                                break;
                            case "hand_user_5":
                                isInvokable = isInvokable(Integer.parseInt(hand_user_4.getTag(R.id.cost).toString()));
                                typeCard = Integer.parseInt(hand_user_5.getTag(R.id.typeCard).toString());
                                if(typeCard == 2) {
                                    effect = Integer.parseInt(hand_user_5.getTag(R.id.effect).toString());
                                }
                                break;
                        }
                    }
                    else {
                        Log.d("pick_card","passe dans le else View.VISIBLE");
                        isInvokable = false;
                    }
                }
                else {
                    switch (label) {
                        case "hand_user_1":
                            isInvokable = isInvokable(Integer.parseInt(hand_user_1.getTag(R.id.cost).toString()));
                            typeCard = Integer.parseInt(hand_user_1.getTag(R.id.typeCard).toString());
                            if(typeCard == 2) {
                                effect = Integer.parseInt(hand_user_1.getTag(R.id.effect).toString());
                            }
                            break;
                        case "hand_user_2":
                            isInvokable = isInvokable(Integer.parseInt(hand_user_2.getTag(R.id.cost).toString()));
                            typeCard = Integer.parseInt(hand_user_2.getTag(R.id.typeCard).toString());
                            if(typeCard == 2) {
                                effect = Integer.parseInt(hand_user_2.getTag(R.id.effect).toString());
                            }
                            break;
                        case "hand_user_3":
                            isInvokable = isInvokable(Integer.parseInt(hand_user_3.getTag(R.id.cost).toString()));
                            typeCard = Integer.parseInt(hand_user_3.getTag(R.id.typeCard).toString());
                            if(typeCard == 2) {
                                effect = Integer.parseInt(hand_user_3.getTag(R.id.effect).toString());
                            }
                            break;
                        case "hand_user_4":
                            isInvokable = isInvokable(Integer.parseInt(hand_user_4.getTag(R.id.cost).toString()));
                            typeCard = Integer.parseInt(hand_user_4.getTag(R.id.typeCard).toString());
                            if(typeCard == 2) {
                                effect = Integer.parseInt(hand_user_4.getTag(R.id.effect).toString());
                            }
                            break;
                        case "hand_user_5":
                            isInvokable = isInvokable(Integer.parseInt(hand_user_4.getTag(R.id.cost).toString()));
                            typeCard = Integer.parseInt(hand_user_5.getTag(R.id.typeCard).toString());
                            if(typeCard == 2) {
                                effect = Integer.parseInt(hand_user_5.getTag(R.id.effect).toString());
                            }
                            break;
                    }
                }

                if (!isInvokable)
                    return false;


                Drawable source;
                ArrayList<String> stat = new ArrayList<>();

                if (typeCard == 1) {
                    if(dropZone.getId() != R.id.spell_zone) {
                        switch (label) {
                            case "hand_user_1":
                                source = hand_user_1.getDrawable();
                                dropZone.setImageDrawable(source);
                                stat.add(hand_user_1.getTag(R.id.atk).toString());
                                stat.add(hand_user_1.getTag(R.id.def).toString());
                                manaDecrease(Integer.parseInt(hand_user_1.getTag(R.id.cost).toString()));
                                hand_user_1.setVisibility(View.INVISIBLE);
                                Helper.playVoice(this, hand_user_1.getTag(R.id.name).toString());
                                sendPlayerBoard(dropZone, Integer.parseInt(hand_user_1.getTag(R.id.index).toString()), "hand1");
                                break;
                            case "hand_user_2":
                                source = hand_user_2.getDrawable();
                                dropZone.setImageDrawable(source);
                                stat.add(hand_user_2.getTag(R.id.atk).toString());
                                stat.add(hand_user_2.getTag(R.id.def).toString());
                                manaDecrease(Integer.parseInt(hand_user_2.getTag(R.id.cost).toString()));
                                hand_user_2.setVisibility(View.INVISIBLE);
                                Helper.playVoice(this, hand_user_2.getTag(R.id.name).toString());
                                sendPlayerBoard(dropZone, Integer.parseInt(hand_user_2.getTag(R.id.index).toString()), "hand2");
                                break;
                            case "hand_user_3":
                                source = hand_user_3.getDrawable();
                                dropZone.setImageDrawable(source);
                                stat.add(hand_user_3.getTag(R.id.atk).toString());
                                stat.add(hand_user_3.getTag(R.id.def).toString());
                                manaDecrease(Integer.parseInt(hand_user_3.getTag(R.id.cost).toString()));
                                hand_user_3.setVisibility(View.INVISIBLE);
                                Helper.playVoice(this, hand_user_3.getTag(R.id.name).toString());
                                sendPlayerBoard(dropZone, Integer.parseInt(hand_user_3.getTag(R.id.index).toString()), "hand3");
                                break;
                            case "hand_user_4":
                                source = hand_user_4.getDrawable();
                                dropZone.setImageDrawable(source);
                                stat.add(hand_user_4.getTag(R.id.atk).toString());
                                stat.add(hand_user_4.getTag(R.id.def).toString());
                                manaDecrease(Integer.parseInt(hand_user_4.getTag(R.id.cost).toString()));
                                hand_user_4.setVisibility(View.INVISIBLE);
                                Helper.playVoice(this, hand_user_4.getTag(R.id.name).toString());
                                Log.d("pick_card", hand_user_4.getTag(R.id.id).toString());
                                sendPlayerBoard(dropZone, Integer.parseInt(hand_user_4.getTag(R.id.index).toString()), "hand4");
                                break;
                            case "hand_user_5":
                                source = hand_user_5.getDrawable();
                                dropZone.setImageDrawable(source);
                                stat.add(hand_user_5.getTag(R.id.atk).toString());
                                stat.add(hand_user_5.getTag(R.id.def).toString());
                                manaDecrease(Integer.parseInt(hand_user_5.getTag(R.id.cost).toString()));
                                hand_user_5.setVisibility(View.INVISIBLE);
                                Helper.playVoice(this, hand_user_5.getTag(R.id.name).toString());
                                sendPlayerBoard(dropZone, Integer.parseInt(hand_user_5.getTag(R.id.index).toString()), "hand5");
                                break;
                        }
                        setStat(stat, dropZone);
                    }
                }
                else {
                    if(dropZone.getId() == R.id.spell_zone) {
                        String pos = (String) dropZone.getTag();
                        Log.d("spell_card", "card of type spell");
                        if (effect != 5) {
                            switch (label) {
                                case "hand_user_1":
                                    manaDecrease(Integer.parseInt(hand_user_1.getTag(R.id.cost).toString()));
                                    hand_user_1.setVisibility(View.INVISIBLE);
                                    if(currentPlayer == 1) {
                                        int index = Integer.parseInt(hand_user_1.getTag(R.id.index).toString());
                                        player1Card.get(index).setActive(false);
                                    }
                                    else if(currentPlayer == 2) {
                                        int index = Integer.parseInt(hand_user_1.getTag(R.id.index).toString());
                                        player2Card.get(index).setActive(false);
                                    }
                                    break;
                                case "hand_user_2":
                                    manaDecrease(Integer.parseInt(hand_user_2.getTag(R.id.cost).toString()));
                                    hand_user_2.setVisibility(View.INVISIBLE);
                                    if(currentPlayer == 1) {
                                        int index = Integer.parseInt(hand_user_2.getTag(R.id.index).toString());
                                        player1Card.get(index).setActive(false);
                                    }
                                    else if(currentPlayer == 2) {
                                        int index = Integer.parseInt(hand_user_2.getTag(R.id.index).toString());
                                        player2Card.get(index).setActive(false);
                                    }
                                    break;
                                case "hand_user_3":
                                    manaDecrease(Integer.parseInt(hand_user_3.getTag(R.id.cost).toString()));
                                    hand_user_3.setVisibility(View.INVISIBLE);

                                    if(currentPlayer == 1) {
                                        int index = Integer.parseInt(hand_user_3.getTag(R.id.index).toString());
                                        player1Card.get(index).setActive(false);
                                    }
                                    else if(currentPlayer == 2) {
                                        int index = Integer.parseInt(hand_user_3.getTag(R.id.index).toString());
                                        player2Card.get(index).setActive(false);
                                    }
                                    break;
                                case "hand_user_4":
                                    manaDecrease(Integer.parseInt(hand_user_4.getTag(R.id.cost).toString()));
                                    hand_user_4.setVisibility(View.INVISIBLE);
                                    if(currentPlayer == 1) {
                                        int index = Integer.parseInt(hand_user_4.getTag(R.id.index).toString());
                                        player1Card.get(index).setActive(false);
                                    }
                                    else if(currentPlayer == 2) {
                                        int index = Integer.parseInt(hand_user_4.getTag(R.id.index).toString());
                                        player2Card.get(index).setActive(false);
                                    }
                                    break;
                                case "hand_user_5":
                                    manaDecrease(Integer.parseInt(hand_user_5.getTag(R.id.cost).toString()));
                                    hand_user_5.setVisibility(View.INVISIBLE);
                                    if(currentPlayer == 1) {
                                        int index = Integer.parseInt(hand_user_5.getTag(R.id.index).toString());
                                        player1Card.get(index).setActive(false);
                                    }
                                    else if(currentPlayer == 2) {
                                        int index = Integer.parseInt(hand_user_5.getTag(R.id.index).toString());
                                        player2Card.get(index).setActive(false);
                                    }
                                    break;
                            }
                    }

                        switch (effect) {
                            case 1:
                                healthPlayer();
                                break;
                            case 2:
                                boostMana();
                                break;
                            case 3:
                                spellModeAttack = true;
                                break;
                            case 4:
                                spellModeDefend = true;
                                break;
                            case 5:
                                rebornMonster(label);
                                break;
                            case 6:
                                takeControl(label);
                                break;
                        }
                    }

                }

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

    public void takeControl(String label) {
        ImageView dropPos = null;
        if(user_left.getVisibility() == View.INVISIBLE) {
            dropPos = user_attack_left;
        }
        else if(user_right.getVisibility() == View.INVISIBLE) {
            dropPos = user_attack_right;
        }
        else if(user_top.getVisibility() == View.INVISIBLE) {
            dropPos = user_defense;
        }

        if(dropPos != null) {

            controlMode = true;
            switch (label) {
                case "hand_user_1":
                    manaDecrease(Integer.parseInt(hand_user_1.getTag(R.id.cost).toString()));
                    hand_user_1.setVisibility(View.INVISIBLE);
                    if(currentPlayer == 1) {
                        int index = Integer.parseInt(hand_user_1.getTag(R.id.index).toString());
                        player1Card.get(index).setActive(false);
                    }
                    else if(currentPlayer == 2) {
                        int index = Integer.parseInt(hand_user_1.getTag(R.id.index).toString());
                        player2Card.get(index).setActive(false);
                    }
                    break;
                case "hand_user_2":
                    manaDecrease(Integer.parseInt(hand_user_2.getTag(R.id.cost).toString()));
                    hand_user_2.setVisibility(View.INVISIBLE);
                    if(currentPlayer == 1) {
                        int index = Integer.parseInt(hand_user_2.getTag(R.id.index).toString());
                        player1Card.get(index).setActive(false);
                    }
                    else if(currentPlayer == 2) {
                        int index = Integer.parseInt(hand_user_2.getTag(R.id.index).toString());
                        player2Card.get(index).setActive(false);
                    }
                    break;
                case "hand_user_3":
                    manaDecrease(Integer.parseInt(hand_user_3.getTag(R.id.cost).toString()));
                    hand_user_3.setVisibility(View.INVISIBLE);
                    if(currentPlayer == 1) {
                        int index = Integer.parseInt(hand_user_3.getTag(R.id.index).toString());
                        player1Card.get(index).setActive(false);
                    }
                    else if(currentPlayer == 2) {
                        int index = Integer.parseInt(hand_user_3.getTag(R.id.index).toString());
                        player2Card.get(index).setActive(false);
                    }
                    break;
                case "hand_user_4":
                    manaDecrease(Integer.parseInt(hand_user_4.getTag(R.id.cost).toString()));
                    hand_user_4.setVisibility(View.INVISIBLE);
                    if(currentPlayer == 1) {
                        int index = Integer.parseInt(hand_user_4.getTag(R.id.index).toString());
                        player1Card.get(index).setActive(false);
                    }
                    else if(currentPlayer == 2) {
                        int index = Integer.parseInt(hand_user_4.getTag(R.id.index).toString());
                        player2Card.get(index).setActive(false);
                    }
                    break;
                case "hand_user_5":
                    manaDecrease(Integer.parseInt(hand_user_5.getTag(R.id.cost).toString()));
                    hand_user_5.setVisibility(View.INVISIBLE);
                    if(currentPlayer == 1) {
                        int index = Integer.parseInt(hand_user_5.getTag(R.id.index).toString());
                        player1Card.get(index).setActive(false);
                    }
                    else if(currentPlayer == 2) {
                        int index = Integer.parseInt(hand_user_5.getTag(R.id.index).toString());
                        player2Card.get(index).setActive(false);
                    }
                    break;
            }
        }
    }

    public void rebornMonster(String label) {
        swipeIndex = 0;
        if(currentPlayer == 1) {

            discardCardPlayer1 = new ArrayList<>();
            for(int i = 0; i < player1Card.size();i++) {
                if(!player1Card.get(i).isActive() && player1Card.get(i).getType_card() == 1) {
                    player1Card.get(i).setIndex(i);
                    discardCardPlayer1.add(player1Card.get(i));
                }
            }

            if(discardCardPlayer1.size() > 0) {
                swipe_text.setText("1/"+discardCardPlayer1.size());
                Drawable path = getDrawable(getResources().getIdentifier(discardCardPlayer1.get(0).getAssetPath(), "drawable", getPackageName()));
                card_choosed.setImageDrawable(path);
                card_choosed.setTag(R.id.name,discardCardPlayer1.get(0).assetPath);
                card_choosed.setTag(R.id.index,0);
                reborn_modal.setVisibility(View.VISIBLE);

                switch (label) {
                    case "hand_user_1":
                        manaDecrease(Integer.parseInt(hand_user_1.getTag(R.id.cost).toString()));
                        hand_user_1.setVisibility(View.INVISIBLE);
                        if(currentPlayer == 1) {
                            int index = Integer.parseInt(hand_user_1.getTag(R.id.index).toString());
                            player1Card.get(index).setActive(false);
                        }
                        else if(currentPlayer == 2) {
                            int index = Integer.parseInt(hand_user_1.getTag(R.id.index).toString());
                            player2Card.get(index).setActive(false);
                        }
                        break;
                    case "hand_user_2":
                        manaDecrease(Integer.parseInt(hand_user_2.getTag(R.id.cost).toString()));
                        hand_user_2.setVisibility(View.INVISIBLE);
                        if(currentPlayer == 1) {
                            int index = Integer.parseInt(hand_user_2.getTag(R.id.index).toString());
                            player1Card.get(index).setActive(false);
                        }
                        else if(currentPlayer == 2) {
                            int index = Integer.parseInt(hand_user_2.getTag(R.id.index).toString());
                            player2Card.get(index).setActive(false);
                        }
                        break;
                    case "hand_user_3":
                        manaDecrease(Integer.parseInt(hand_user_3.getTag(R.id.cost).toString()));
                        hand_user_3.setVisibility(View.INVISIBLE);
                        if(currentPlayer == 1) {
                            int index = Integer.parseInt(hand_user_3.getTag(R.id.index).toString());
                            player1Card.get(index).setActive(false);
                        }
                        else if(currentPlayer == 2) {
                            int index = Integer.parseInt(hand_user_3.getTag(R.id.index).toString());
                            player2Card.get(index).setActive(false);
                        }
                        break;
                    case "hand_user_4":
                        manaDecrease(Integer.parseInt(hand_user_4.getTag(R.id.cost).toString()));
                        hand_user_4.setVisibility(View.INVISIBLE);
                        if(currentPlayer == 1) {
                            int index = Integer.parseInt(hand_user_4.getTag(R.id.index).toString());
                            player1Card.get(index).setActive(false);
                        }
                        else if(currentPlayer == 2) {
                            int index = Integer.parseInt(hand_user_4.getTag(R.id.index).toString());
                            player2Card.get(index).setActive(false);
                        }
                        break;
                    case "hand_user_5":
                        manaDecrease(Integer.parseInt(hand_user_5.getTag(R.id.cost).toString()));
                        hand_user_5.setVisibility(View.INVISIBLE);
                        if(currentPlayer == 1) {
                            int index = Integer.parseInt(hand_user_5.getTag(R.id.index).toString());
                            player1Card.get(index).setActive(false);
                        }
                        else if(currentPlayer == 2) {
                            int index = Integer.parseInt(hand_user_5.getTag(R.id.index).toString());
                            player2Card.get(index).setActive(false);
                        }
                        break;
                }
            }


        }
        else if (currentPlayer == 2) {
            discardCardPlayer2 = new ArrayList<>();
            for(int i = 0; i < player2Card.size();i++) {
                if(!player2Card.get(i).isActive() && player2Card.get(i).getType_card() == 1) {
                    player2Card.get(i).setIndex(i);
                    discardCardPlayer2.add(player2Card.get(i));
                }
            }

            if(discardCardPlayer2.size() > 0) {
                swipe_text.setText("1/"+discardCardPlayer2.size());
                Drawable path = getDrawable(getResources().getIdentifier(discardCardPlayer2.get(0).getAssetPath(), "drawable", getPackageName()));
                card_choosed.setImageDrawable(path);
                card_choosed.setTag(R.id.name,discardCardPlayer2.get(0).assetPath);
                card_choosed.setTag(R.id.index,0);
                reborn_modal.setVisibility(View.VISIBLE);

                switch (label) {
                    case "hand_user_1":
                        manaDecrease(Integer.parseInt(hand_user_1.getTag(R.id.cost).toString()));
                        hand_user_1.setVisibility(View.INVISIBLE);
                        if(currentPlayer == 1) {
                            int index = Integer.parseInt(hand_user_1.getTag(R.id.index).toString());
                            player1Card.get(index).setActive(false);
                        }
                        else if(currentPlayer == 2) {
                            int index = Integer.parseInt(hand_user_1.getTag(R.id.index).toString());
                            player2Card.get(index).setActive(false);
                        }
                        break;
                    case "hand_user_2":
                        manaDecrease(Integer.parseInt(hand_user_2.getTag(R.id.cost).toString()));
                        hand_user_2.setVisibility(View.INVISIBLE);
                        if(currentPlayer == 1) {
                            int index = Integer.parseInt(hand_user_2.getTag(R.id.index).toString());
                            player1Card.get(index).setActive(false);
                        }
                        else if(currentPlayer == 2) {
                            int index = Integer.parseInt(hand_user_2.getTag(R.id.index).toString());
                            player2Card.get(index).setActive(false);
                        }
                        break;
                    case "hand_user_3":
                        manaDecrease(Integer.parseInt(hand_user_3.getTag(R.id.cost).toString()));
                        hand_user_3.setVisibility(View.INVISIBLE);
                        if(currentPlayer == 1) {
                            int index = Integer.parseInt(hand_user_3.getTag(R.id.index).toString());
                            player1Card.get(index).setActive(false);
                        }
                        else if(currentPlayer == 2) {
                            int index = Integer.parseInt(hand_user_3.getTag(R.id.index).toString());
                            player2Card.get(index).setActive(false);
                        }
                        break;
                    case "hand_user_4":
                        manaDecrease(Integer.parseInt(hand_user_4.getTag(R.id.cost).toString()));
                        hand_user_4.setVisibility(View.INVISIBLE);
                        if(currentPlayer == 1) {
                            int index = Integer.parseInt(hand_user_4.getTag(R.id.index).toString());
                            player1Card.get(index).setActive(false);
                        }
                        else if(currentPlayer == 2) {
                            int index = Integer.parseInt(hand_user_4.getTag(R.id.index).toString());
                            player2Card.get(index).setActive(false);
                        }
                        break;
                    case "hand_user_5":
                        manaDecrease(Integer.parseInt(hand_user_5.getTag(R.id.cost).toString()));
                        hand_user_5.setVisibility(View.INVISIBLE);
                        if(currentPlayer == 1) {
                            int index = Integer.parseInt(hand_user_5.getTag(R.id.index).toString());
                            player1Card.get(index).setActive(false);
                        }
                        else if(currentPlayer == 2) {
                            int index = Integer.parseInt(hand_user_5.getTag(R.id.index).toString());
                            player2Card.get(index).setActive(false);
                        }
                        break;
                }
            }

        }

    }

    public void healthPlayer() {
        if(currentPlayer == 1) {
            player1.setLifepoint(player1.getLifepoint() + 300);
            player1.setPlayerInfo(party.getId(), 1);
        }
        else if(currentPlayer == 2) {
            player2.setLifepoint(player2.getLifepoint() + 300);
            player1.setPlayerInfo(party.getId(), 2);
        }
    }

    public void boostMana() {
        if(currentPlayer == 1) {
            player1.setMana(player1.getMana()+1);
            user_mana.setText(player1.getMana()+"/"+player1.getManaMax());
            player1.setPlayerInfo(party.getId(),1);

        }
        else if(currentPlayer == 2) {
            player2.setMana(player2.getMana()+1);
            user_mana.setText(player2.getMana()+"/"+player2.getManaMax());
            player2.setPlayerInfo(party.getId(),2);
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
    public void setStat(ArrayList<String> stat, ImageView dropZone) {

        switch (dropZone.getId()) {
            case R.id.left_card_user:
                user_left.setText("ATK : " + stat.get(0));
                user_left.setVisibility(View.VISIBLE);
                break;
            case R.id.right_card_user:
                user_right.setText("ATK : " + stat.get(0));
                user_right.setVisibility(View.VISIBLE);
                break;
            case R.id.up_card_user:
                user_top.setText("DEF : " + stat.get(1));
                user_top.setVisibility(View.VISIBLE);
                break;
        }
    }

    public TextView getDropPos(ImageView dropZone) {

        switch (dropZone.getId()) {
            case R.id.left_card_user: return user_left;
            case R.id.right_card_user: return user_right;
            case R.id.up_card_user: return user_top;
            default:
        }
        return null;
    }

    public void sendPlayerBoard(ImageView dropZone, int index, String handIndex) {
        Log.d("pick_card","index : "+index);
        String pos = null;
        switch (dropZone.getId()) {
            case R.id.left_card_user:
                pos = "left";
                break;
            case R.id.right_card_user:
                pos = "right";
                break;
            case R.id.up_card_user:
                pos = "top";
                break;
        }

        Map<String, Object> cardInfo = new HashMap<>();
        if (currentPlayer == 1) {
            Card parent = player1Card.get(index);
            Map<String, Object> board = new HashMap<>();
            if(player1.getBoard() != null) {
                if(player1.getBoard().size() != 0) {
                    board = player1.getBoard();
                }
            }

            if (parent instanceof CardEntity) {
                cardInfo.put("card",(CardEntity) parent);
            }
            else if(parent instanceof  CardSpell) {
                cardInfo.put("card",(CardSpell) parent);
            }
            cardInfo.put("pos", pos);
            cardInfo.put("index", index);

            board.put("board-"+pos ,cardInfo);
            player1.setBoard(board);
            if(handIndex.equals("null")) {
                Map<String, Object> hand = player1.getHand();
                hand.remove(handIndex);
                player1.setHand(hand);
            }

            player1.setPlayerInfo(party.getId(), 1);
        } else {
            Card parent = player2Card.get(index);
            Map<String, Object> board = new HashMap<>();
            if(player2.getBoard() != null) {
                if(player2.getBoard().size() != 0) {
                    board = player2.getBoard();
                }
            }
            if (parent instanceof CardEntity) {
                cardInfo.put("card",(CardEntity) parent);
            }
            else if(parent instanceof  CardSpell) {
                cardInfo.put("card",(CardSpell) parent);
            }

            cardInfo.put("pos", pos);
            cardInfo.put("index", index);

            try {
                Log.d("battle","board test"+board.toString());
            }
            catch (Exception e) {
                Log.d("battle", e.getMessage());
            }

            board.put("board-"+pos, cardInfo);

            player2.setBoard(board);
            if(handIndex.equals("null")) {
                Map<String, Object> hand = player2.getHand();
                hand.remove(handIndex);
                player2.setHand(hand);
            }
            player2.setPlayerInfo(party.getId(), 2);
        }

    }

    @Override
    public void onTaskCompleted(Boolean isFull) {

    }

    public void watchOtherMove() {
        DocumentReference query = Helper.getInstance().db.collection("Party").document(party.getId());
        registration = query.addSnapshotListener(
                (snapshot, e) -> {
                    if (e != null)
                    {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists())
                    {
                        for(Card c : player1Card){
                            if(!c.isActive()){
                                Log.d("discard","state card"+c.getName());
                                Log.d("discard","state card"+c.getLevel());
                            }
                        }

                        if(party.getNumberRound() != Integer.parseInt(snapshot.getData().get("number_round").toString())) {
                            party.setNumberRound(Integer.parseInt(snapshot.getData().get("number_round").toString()));
                        }


                        if (!playerTurn.equals(snapshot.getData().get("current_player").toString())) {
                            playerTurn = snapshot.getData().get("current_player").toString();
                            if(party.getNumberRound() != 1) {
                                startTimer();
                                pickCard(playerTurn);
                            }
                            Log.d(TAG, "round : "+party.getNumberRound());

                        }

                        if (currentPlayer == 1) {
                            if(snapshot.getData().get("player1Info") != null) {
                                Map<String, Object> playerInfo = (Map<String, Object>) snapshot.getData().get("player1Info");
                                if(Integer.parseInt(playerInfo.get("life").toString()) <= 0) {
                                    clock.cancel();
                                    registration.remove();
                                    Long tsLong = System.currentTimeMillis()/1000;
                                    String ts = tsLong.toString();
                                    String winner = player2.getId();
                                    String defeat = player1.getId();
                                    archiveParty(winner, defeat, ts);
                                    defeat_layout.setVisibility(View.VISIBLE);
                                    filter.setVisibility(View.VISIBLE);
                                    registration.remove();
                                    Helper.playTheme(this, "lose");
                                }
                                else {
                                    player1.updatePlayer(playerInfo);
                                    if(playerInfo.get("discarding") != null) {
                                        Map<String, Object> discarding = (Map<String, Object>) playerInfo.get("discarding");
                                        Drawable verso_card = getDrawable(getResources().getIdentifier("verso", "drawable", getPackageName()));
                                        discard_user.setImageDrawable(verso_card);
                                        for (Map.Entry<String, Object> entry : discarding.entrySet()) {
                                            int index = Integer.parseInt(entry.getValue().toString());
                                            player1Card.get(index).setActive(false);
                                            player1Card.get(index).setInHand(false);
                                            player1Card.get(index).setOnGround(false);
                                        }

                                    }
                                    user_life.setText("LP :"+player1.getLifepoint());
                                    updateBoard(player1.getBoard());
                                }

                            }
                            if (snapshot.getData().get("player2Info") != null) {
                                Map<String, Object> playerInfo = (Map<String, Object>) snapshot.getData().get("player2Info");
                                if(Integer.parseInt(playerInfo.get("life").toString()) <= 0) {
                                    clock.cancel();
                                    Long tsLong = System.currentTimeMillis()/1000;
                                    String ts = tsLong.toString();
                                    String winner = player1.getId();
                                    String defeat = player2.getId();
                                    archiveParty(winner, defeat, ts);
                                    victory_layout.setVisibility(View.VISIBLE);
                                    prize.setText("25");
                                    filter.setVisibility(View.VISIBLE);
                                    registration.remove();
                                    Helper.playTheme(this, "victory");

                                }
                                player2.updatePlayer(playerInfo);
                                opponent_mana.setText(String.format("%d/%d",player2.getMana(),player2.getManaMax()));
                                updateOpponent(player2);
                            }
                        } else if (currentPlayer == 2) {
                            if(snapshot.getData().get("player2Info") != null) {
                                Map<String, Object> playerInfo = (Map<String, Object>) snapshot.getData().get("player2Info");
                                if(Integer.parseInt(playerInfo.get("life").toString()) <= 0) {
                                    clock.cancel();
                                    registration.remove();
                                    defeat_layout.setVisibility(View.VISIBLE);
                                    filter.setVisibility(View.VISIBLE);
                                    Helper.playTheme(this, "lose");
                                }
                                player2.updatePlayer(playerInfo);
                                if(playerInfo.get("discarding") != null) {
                                    Map<String, Object> discarding = (Map<String, Object>) playerInfo.get("discarding");
                                    Drawable verso_card = getDrawable(getResources().getIdentifier("verso", "drawable", getPackageName()));
                                    discard_user.setImageDrawable(verso_card);
                                    for (Map.Entry<String, Object> entry : discarding.entrySet()) {
                                        int index = Integer.parseInt(entry.getValue().toString());
                                        player2Card.get(index).setActive(false);
                                        player2Card.get(index).setInHand(false);
                                        player2Card.get(index).setOnGround(false);
                                    }

                                }
                                user_life.setText(player2.getLifepoint()+"");
                                updateBoard(player2.getBoard());
                            }

                            if (snapshot.getData().get("player1Info") != null) {
                                Map<String, Object> playerInfo = (Map<String, Object>) snapshot.getData().get("player1Info");
                                if(Integer.parseInt(playerInfo.get("life").toString()) <= 0) {
                                    clock.cancel();
                                    prize.setText("25");
                                    victory_layout.setVisibility(View.VISIBLE);
                                    filter.setVisibility(View.VISIBLE);
                                    Helper.playTheme(this, "victory");
                                }

                                player1.updatePlayer(playerInfo);
                                opponent_mana.setText(String.format("%d/%d",player1.getMana(),player1.getManaMax()));
                                updateOpponent(player1);
                            }
                        }

                    }
                    else
                    {
                        Log.d("passe","else");
                    }
                });
    }

    public void updateBoard(Map<String, Object> board) {
        Map<String, Object> row;

        if(board != null) {
            Log.d("battle","board non vide");
            resetUserBoard(board);

            if(board.size() != 0) {
                for (Map.Entry<String, Object> entry : board.entrySet()) {

                    row = (Map<String, Object>) entry.getValue();
                    Map<String, Object> card = ( Map<String, Object>) row.get("card");
                    Log.d("battle",card.get("assetPath").toString());
                    Drawable source = getDrawable(getResources()
                            .getIdentifier(card.get("assetPath").toString(), "drawable", getPackageName()));
                    String atk;
                    switch (row.get("pos").toString()) {
                        case "left":
                            if (user_attack_left.getTag(R.id.name) == null) {
                                user_attack_left.setImageDrawable(source);
                                user_attack_left.setTag(R.id.name, card.get("assetPath").toString());
                                user_attack_left.setTag(R.id.fight, false);
                                atk = "atk : " + card.get("attack");
                                user_left.setText(atk);
                                user_left.setVisibility(View.VISIBLE);
                            }
                            break;
                        case "right":
                            if (user_attack_right.getTag(R.id.name) == null) {
                                user_attack_right.setImageDrawable(source);
                                user_attack_right.setTag(R.id.name, card.get("assetPath"));
                                user_attack_right.setTag(R.id.fight, false);
                                atk = "atk : " + card.get("attack");
                                user_right.setText(atk);
                                user_right.setVisibility(View.VISIBLE);
                            }
                            break;
                        case "top":
                            if (user_defense.getTag(R.id.name) == null) {
                                user_defense.setImageDrawable(source);
                                user_defense.setTag(R.id.name, card.get("assetPath"));
                                String def = "def : " + card.get("defend");
                                user_top.setText(def);
                                user_top.setVisibility(View.VISIBLE);
                            }
                            break;
                    }
                }
            }
            else
            {
                Drawable path = getDrawable(getResources().getIdentifier("card_section", "drawable", getPackageName()));
                user_attack_left.setImageDrawable(path);
                user_attack_left.setTag(null);
                user_left.setText("");
                user_left.setVisibility(View.INVISIBLE);


                user_attack_right.setImageDrawable(path);
                user_attack_right.setTag(null);
                user_right.setText("");
                user_right.setVisibility(View.INVISIBLE);

                user_defense.setImageDrawable(path);
                user_defense.setTag(null);
                user_top.setText("");
                user_top.setVisibility(View.INVISIBLE);

            }

        }
        else {
            Log.d("battle","board vide");
        }

    }

    public void updateOpponent(Player player) {
        String life = getString(R.string.life_prefix) + player.getLifepoint();
        opponent_life.setText(life);
        String mana;
        if (party.getNumberRound() < 2 && player.getMana() == 3)
            mana = player.getMana() + "/" + (player.getMana()-1);
        else
            mana = player.getMana() + "/" + (party.getNumberRound()+1);
        opponent_mana.setText(mana);
        if (player.getHand() != null)
            setAdvHand(player.getHand());
        if (player.getBoard() != null)
            setAdvBoard(player.getBoard());
    }

    public void setAdvHand(Map<String, Object> hand) {
        switch (hand.size()) {
            case 1:
                hand_opponent_5.setVisibility(View.VISIBLE);
                hand_opponent_4.setVisibility(View.INVISIBLE);
                hand_opponent_3.setVisibility(View.INVISIBLE);
                hand_opponent_2.setVisibility(View.INVISIBLE);
                hand_opponent_1.setVisibility(View.INVISIBLE);
                break;
            case 2:
                hand_opponent_5.setVisibility(View.VISIBLE);
                hand_opponent_4.setVisibility(View.VISIBLE);
                hand_opponent_3.setVisibility(View.INVISIBLE);
                hand_opponent_2.setVisibility(View.INVISIBLE);
                hand_opponent_1.setVisibility(View.INVISIBLE);
                break;
            case 3:
                hand_opponent_5.setVisibility(View.VISIBLE);
                hand_opponent_4.setVisibility(View.VISIBLE);
                hand_opponent_3.setVisibility(View.VISIBLE);
                hand_opponent_2.setVisibility(View.INVISIBLE);
                hand_opponent_1.setVisibility(View.INVISIBLE);
                break;
            case 4:
                hand_opponent_5.setVisibility(View.VISIBLE);
                hand_opponent_4.setVisibility(View.VISIBLE);
                hand_opponent_3.setVisibility(View.VISIBLE);
                hand_opponent_2.setVisibility(View.VISIBLE);
                hand_opponent_1.setVisibility(View.INVISIBLE);
                break;
            case 5:
                hand_opponent_5.setVisibility(View.VISIBLE);
                hand_opponent_4.setVisibility(View.VISIBLE);
                hand_opponent_3.setVisibility(View.VISIBLE);
                hand_opponent_2.setVisibility(View.VISIBLE);
                hand_opponent_1.setVisibility(View.VISIBLE);
                break;
            default:
                hand_opponent_5.setVisibility(View.INVISIBLE);
                hand_opponent_4.setVisibility(View.INVISIBLE);
                hand_opponent_3.setVisibility(View.INVISIBLE);
                hand_opponent_2.setVisibility(View.INVISIBLE);
                hand_opponent_1.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void setAdvBoard(Map<String, Object> board) {

        resetBoard(board);
        Map<String, Object> row;

        for (Map.Entry<String, Object> entry : board.entrySet()) {
            row = (Map<String, Object>) entry.getValue();
            Map<String, Object> card = ( Map<String, Object>) row.get("card");

            Drawable source = getDrawable(getResources()
                    .getIdentifier(card.get("assetPath").toString(), "drawable", getPackageName()));
            String atk;
            switch (row.get("pos").toString()) {
                case "left":
                    Log.d(TAG, "setAdvBoard: "+opponent_attack_left.getTag(R.id.name)+" = "+card.get("assetPath"));
                    if (opponent_attack_left.getTag(R.id.name) == null) {
                        opponent_attack_left.setImageDrawable(source);
                        opponent_attack_left.setTag(R.id.index, row.get("index").toString());
                        opponent_attack_left.setTag(R.id.name, card.get("assetPath").toString());
                        atk = "atk : " + card.get("attack");
                        opponent_left.setText(atk);
                        opponent_left.setVisibility(View.VISIBLE);
                        Helper.playVoice(this, card.get("assetPath").toString());
                    }
                    break;
                case "right":
                    if (opponent_attack_right.getTag(R.id.name) == null) {
                        opponent_attack_right.setImageDrawable(source);
                        opponent_attack_right.setTag(R.id.index, row.get("index").toString());
                        opponent_attack_right.setTag(R.id.name, card.get("assetPath"));
                        atk = "atk : " + card.get("attack");
                        opponent_right.setText(atk);
                        opponent_right.setVisibility(View.VISIBLE);
                        Helper.playVoice(this, card.get("assetPath").toString());
                    }
                    break;
                case "top":
                    if (opponent_defense.getTag(R.id.name) == null) {
                        opponent_defense.setImageDrawable(source);
                        opponent_defense.setTag(R.id.index, row.get("index").toString());
                        opponent_defense.setTag(R.id.name, card.get("assetPath"));
                        String def = "def : " + card.get("defend");
                        opponent_top.setText(def);
                        opponent_top.setVisibility(View.VISIBLE);
                        Helper.playVoice(this, card.get("assetPath").toString());
                    }
                    break;
            }
        }
    }

    public void resetBoard(Map<String, Object> board) {
        String[] resetException = new String[3];
        int i = 0;
        Map<String, Object> row;

        for (Map.Entry<String, Object> entry : board.entrySet()) {
            row = (Map<String, Object>) entry.getValue();
            Map<String, Object> card;
            switch (row.get("pos").toString()) {
                case "left":
                    card = (Map<String, Object>) row.get("card");
                    resetException[i] = "left";
                    opponent_left.setText("ATK :"+ card.get("attack"));
                    i++;
                    break;
                case "right":
                    card = (Map<String, Object>) row.get("card");
                    resetException[i] = "right";
                    opponent_right.setText("ATK :"+ card.get("attack"));
                    i++;
                    break;
                case "top":
                    card = (Map<String, Object>) row.get("card");
                    resetException[i] = "top";
                    opponent_top.setText("DEF :"+ card.get("defend"));
                    i++;
                    break;
            }
        }

        Drawable path = getDrawable(getResources().getIdentifier("card_section", "drawable", getPackageName()));
        if(!Arrays.asList(resetException).contains("left")) {
            opponent_attack_left.setImageDrawable(path);
            opponent_attack_left.setTag(null);
            opponent_attack_left.setTag(R.id.name, null);
            opponent_attack_left.setTag("left");
            opponent_left.setText("");
            opponent_left.setVisibility(View.INVISIBLE);
        }

        if(!Arrays.asList(resetException).contains("right")) {
            opponent_attack_right.setImageDrawable(path);
            opponent_attack_right.setTag(null);
            opponent_attack_right.setTag(R.id.name, null);
            opponent_attack_right.setTag("right");
            opponent_right.setText("");
            opponent_right.setVisibility(View.INVISIBLE);
        }

        if(!Arrays.asList(resetException).contains("top")) {
            Log.d("pick_card","passe dans le top ...");
            opponent_defense.setImageDrawable(path);
            opponent_defense.setTag(null);
            opponent_defense.setTag(R.id.name, null);
            opponent_defense.setTag("top");
            opponent_top.setText("");
            opponent_top.setVisibility(View.INVISIBLE);
        }

    }

    public void resetUserBoard(Map<String, Object> board) {
        String[] resetException = new String[3];
        int i = 0;
        Map<String, Object> row;

        for (Map.Entry<String, Object> entry : board.entrySet()) {
            row = (Map<String, Object>) entry.getValue();

            switch (row.get("pos").toString()) {
                case "left":
                    resetException[i] = "left";
                    i++;
                    break;
                case "right":
                    resetException[i] = "right";
                    i++;
                    break;
                case "top":
                    resetException[i] = "top";
                    i++;
                    break;
            }
        }

        Drawable path = getDrawable(getResources().getIdentifier("card_section", "drawable", getPackageName()));
        if(!Arrays.asList(resetException).contains("left")) {
            Log.d("fought","passe dans le left");
            user_attack_left.setImageDrawable(path);
            user_attack_left.setTag(null);
            user_attack_left.setTag(R.id.name, null);
            user_attack_left.setTag("left");
            user_left.setText("");
            user_left.setVisibility(View.INVISIBLE);
        }

        if(!Arrays.asList(resetException).contains("right")) {
            user_attack_right.setImageDrawable(path);
            user_attack_right.setTag(null);
            user_attack_right.setTag(R.id.name, null);
            user_attack_right.setTag("right");
            user_right.setText("");
            user_right.setVisibility(View.INVISIBLE);
        }

        if(!Arrays.asList(resetException).contains("top")) {
            Log.d("pick_card","passe dans le top ...");
            user_defense.setImageDrawable(path);
            user_defense.setTag(null);
            user_defense.setTag(R.id.name, null);
            user_defense.setTag("top");
            user_top.setText("");
            user_top.setVisibility(View.INVISIBLE);
        }
    }

    public void startTimer()
    {
        Log.d(TAG,"Dans le start timer");
        Log.d(TAG,"playerTurn : "+playerTurn);
        Log.d(TAG,"getUid : "+helper.mAuth.getUid());
        counter = 30;
        if(playerTurn.equals(helper.mAuth.getUid()))
        {
            Log.d(TAG,"dans le if");
            clock = new CountDownTimer(30000, 1000){
                public void onTick(long millisUntilFinished){
                    Log.d(TAG,"dans le onTick");
                    timer.setText(String.valueOf(counter));
                    counter--;
                }
                public  void onFinish(){
                    Log.d(TAG,"dans le onFinish");
                    timer.setText(String.valueOf(30));
                    updatePlayerTurn();
                }
            }.start();
        }
    }

    public void pickCard(String playerTurn)
    {
        for(int i = 0; i < player2Card.size();i++) {
            Card parent = (Card) player2Card.get(i);
            Log.d("watch_pick_debug","name :" + parent.getName());
            Log.d("watch_pick_debug","id :" + parent.getId());
            Log.d("watch_pick_debug","isActive :" + parent.isActive());
            Log.d("watch_pick_debug","isInHand :" + parent.isInHand());
            Log.d("watch_pick_debug","isOnGround :" + parent.isOnGround());
        }

        if(helper.mAuth.getUid().equals(playerTurn))
        {
              Log.d("watch_pick","dans la fonction du pick");
              ImageView[] hands = new ImageView[5];
              hands[0] = hand_user_1;
              hands[1] = hand_user_2;
              hands[2] = hand_user_3;
              hands[3] = hand_user_4;
              hands[4] = hand_user_5;

              boolean emptyHand = false;
              int pos = 0;

              for (int i = 0; i < hands.length;i++)
              {
                  if(hands[i].getVisibility() == View.INVISIBLE)
                  {
                      emptyHand = true;
                      pos = i;
                      break;
                  }
              }

              Log.d("watch_pick", "index vide : "+pos);
              if(emptyHand)
              {
                  boolean isActive = true;
                  boolean emptyDeck = false;
                  Card parent = null;
                  int i = 0;
                  while (isActive) {
                      Random randomGenerator = new Random();

                      if(currentPlayer == 1)
                      {
                          if(i == player1Card.size()) {
                              isActive = false;
                              emptyDeck = true;
                          }
                          else {
                              parent = player1Card.get(i);
                              if(parent.isActive() && !parent.isInHand() && !parent.isOnGround()) {
                                  isActive = false;
                                  player1Card.get(i).setInHand(true);
                              }
                              if(isActive) {
                                  i++;
                              }
                          }

                      }
                      else if(currentPlayer == 2)
                      {
                          if(i == player2Card.size()) {
                              isActive = false;
                              emptyDeck = true;
                          }
                          else {
                              parent = player2Card.get(i);
                              if(parent.isActive() && !parent.isInHand() && !parent.isOnGround()) {
                                  isActive = false;
                                  player2Card.get(i).setInHand(true);
                              }
                              if(isActive) {
                                  i++;
                              }
                          }
                      }
                  }


                  Log.d("watch_pick","main utilisateur "+currentPlayer+" vide");

                  if(!emptyDeck) {
                      if (parent instanceof CardEntity) {
                          CardEntity card = (CardEntity) parent;
                          Drawable path = getDrawable(getResources().getIdentifier(card.getAssetPath(), "drawable", getPackageName()));
                          hands[pos].setImageDrawable(path);
                          hands[pos].setTag(R.id.id, card.getId());
                          hands[pos].setTag(R.id.typeCard, 1);
                          hands[pos].setTag(R.id.atk, card.getAttack());
                          hands[pos].setTag(R.id.def, card.getDefend());
                          hands[pos].setTag(R.id.cost, card.getLevel());
                          hands[pos].setTag(R.id.name, card.getAssetPath());
                          hands[pos].setTag(R.id.index, i);
                          hands[pos].setVisibility(View.VISIBLE);
                      }
                      else if(parent instanceof CardSpell) {
                          CardSpell card = (CardSpell) parent;
                          Log.d("spell_card_path",parent.getAssetPath());
                          Drawable path = getDrawable(getResources().getIdentifier(card.getAssetPath(), "drawable", getPackageName()));
                          hands[pos].setImageDrawable(path);
                          hands[pos].setTag(R.id.id, card.getId());
                          hands[pos].setTag(R.id.typeCard, 2);
                          hands[pos].setTag(R.id.cost, card.getLevel());
                          hands[pos].setTag(R.id.name, card.getAssetPath());
                          hands[pos].setTag(R.id.effect, card.getIdEffect());
                          hands[pos].setTag(R.id.index, i);
                          hands[pos].setVisibility(View.VISIBLE);
                      }
                  }

              }

        }
    }

    public void archiveParty(String idVictory, String idDefeat, String ts) {

        Map<String, Object> archive =  new HashMap<>();
        archive.put("winner",idVictory);
        archive.put("defeat",idDefeat);
        archive.put("date_end",ts);
        helper.db.collection("Archive_Party").document(party.getId())
                .set(archive)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Log.d("end_party",party.getId());

                        helper.db.collection("Party").document(party.getId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG,"Deleting with success");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error writing document", e);
                    }
                });
    }

}