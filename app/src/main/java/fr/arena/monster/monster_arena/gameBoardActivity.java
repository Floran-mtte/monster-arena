package fr.arena.monster.monster_arena;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Text;

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

    GameBoardPlayer player1_board;
    GameBoardPlayer player2_board;

    Map<String, Object> current_player_hand;

    Helper helper = Helper.getInstance();
    String TAG = "gameBoardActivity";
    int currentPlayer, counter = 3, manaPLayer1, manaPLayer2;
    CardEntity clickedCard;
    String playerTurn;

    boolean attackMode = false;

    CountDownTimer clock;

    ImageView hand_user_1, hand_user_2, hand_user_3, hand_user_4, hand_user_5, cardDetail, user_attack_left, user_attack_right, user_defense, opponent_attack_left, opponent_attack_right, opponent_defense, hand_opponent_1, hand_opponent_2, hand_opponent_3, hand_opponent_4, hand_opponent_5, dropZone = null, end_tour_button;
    TextView user_left, user_top, user_right, opponent_left, opponent_top, opponent_right, user_mana, opponent_mana, user_life, opponent_life, timer;
    FrameLayout filter;
    String label = null;
    OnTaskCompleted listener;
    ListenerRegistration registration;

    private Task<Void> allTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Helper.playTheme(this, "fight");

        setContentView(R.layout.activity_game_board);

        user_life = (TextView) findViewById(R.id.user_life);

        user_mana = (TextView) findViewById(R.id.user_mana);

        hand_user_1 = (ImageView) findViewById(R.id.hand_user_1);
        hand_user_2 = (ImageView) findViewById(R.id.hand_user_2);
        hand_user_3 = (ImageView) findViewById(R.id.hand_user_3);
        hand_user_4 = (ImageView) findViewById(R.id.hand_user_4);
        hand_user_5 = (ImageView) findViewById(R.id.hand_user_5);

        user_attack_left = (ImageView) findViewById(R.id.left_card_user);
        user_attack_right = (ImageView) findViewById(R.id.right_card_user);
        user_defense = (ImageView) findViewById(R.id.up_card_user);

        user_attack_left.setTag("left");
        user_defense.setTag("top");
        user_attack_right.setTag("right");

        user_left = (TextView) findViewById(R.id.left_card_user_attack);
        user_top = (TextView) findViewById(R.id.up_card_user_defense);
        user_right = (TextView) findViewById(R.id.right_card_user_attack);

        opponent_life = (TextView) findViewById(R.id.opponent_life);
        opponent_mana = (TextView) findViewById(R.id.opponent_mana);
        timer = (TextView) findViewById(R.id.timer);

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
        filter = (FrameLayout) findViewById(R.id.filter);

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

        user_attack_left.setOnClickListener(this);
        user_attack_right.setOnClickListener(this);
        user_defense.setOnClickListener(this);


        opponent_left.setOnClickListener(this);
        opponent_right.setOnClickListener(this);
        opponent_top.setOnClickListener(this);

        end_tour_button.setOnClickListener(this);

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
        player1 = new Player(player_1, "john", 2500, 3,2);
        player2 = new Player(player_2, "daenerys", 2500, 2,2);
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
                            Map<String, Object> player1Hand = new HashMap<>();

                            hands[0] = hand_user_1;
                            hands[1] = hand_user_2;
                            hands[2] = hand_user_3;
                            hands[3] = hand_user_4;
                            hands[4] = hand_user_5;

                            Collections.shuffle(player1Card);

                            for (int i=0; i < 3; i++) {
                                Card parent = player1Card.get(i);
                                player1Hand.put("hand"+(i+1), parent);
                                if (parent instanceof CardEntity) {
                                    CardEntity card = (CardEntity) parent;
                                    Drawable path = getDrawable(getResources()
                                            .getIdentifier(card.getAssetPath(), "drawable", getPackageName()));
                                    hands[i].setImageDrawable(path);
                                    hands[i].setTag(R.id.id, card.getId());
                                    hands[i].setTag(R.id.atk, card.getAttack());
                                    hands[i].setTag(R.id.def, card.getDefend());
                                    hands[i].setTag(R.id.cost, card.getLevel());
                                    hands[i].setTag(R.id.name, card.getAssetPath());
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
                //setPlayer2Info();
                player2.setPlayerInfo(party.getId(), 2);

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
                            Map<String, Object> player2Hand = new HashMap<>();

                            hands[0] = hand_user_1;
                            hands[1] = hand_user_2;
                            hands[2] = hand_user_3;
                            hands[3] = hand_user_4;
                            hands[4] = hand_user_5;

                            Collections.shuffle(player2Card);

                            for (int i=0; i < 3; i++) {
                                Card parent = player2Card.get(i);
                                player2Hand.put("hand"+(i+1), parent);
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
                                    hands[i].setTag(R.id.index, Integer.toString(i));
                                    hands[i].setVisibility(View.VISIBLE);
                                }
                            }
                            //todo add player hand to fb
                            player2.setHand(player2Hand);
                            player2.setPlayerInfo(party.getId(),2);
                            //addCardToParty(current_player_hand);
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
            case R.id.left_card_opponent:
                if(attackMode) {
                    attackMode = false;
                    if(currentPlayer == 1) {

                        CardEntity opponentCard = player2_board.getLeftCardBoard();
                        int difference = clickedCard.getAttack() - opponentCard.getAttack();
                        boolean exist = false;
                        if(difference > 0) {
                            String id = opponentCard.getId();
                            int i = 0;
                            while(i < this.player2Card.size()) {
                                if(this.player2Card.get(i).getId().equals(id)){
                                    exist = true;
                                    break;
                                }
                                i++;
                            }

                            if(exist) {
                                player2Card.remove(i);
                                Drawable path = getDrawable(getResources()
                                        .getIdentifier("card_section", "drawable", getPackageName()));
                                user_attack_left.setImageDrawable(path);
                                player2.setLifepoint(player2.getLifepoint() - difference);
                            } else {
                                Log.d(TAG,"Error card not found");
                            }

                        }
                        else if(difference < 0) {

                        }
                        else if(difference == 0) {

                        }
                    }
                }
                break;
            case R.id.right_card_opponent:

                break;
            case R.id.up_card_opponent:

                break;
            case R.id.left_card_user:

                if(currentPlayer == 1) {
                    clickedCard = player1_board.getLeftCardBoard();
                }
                else if(currentPlayer == 2) {
                    clickedCard = player2_board.getLeftCardBoard();
                }
                attackMode = true;
                break;
            case R.id.right_card_user:

                break;
            case R.id.up_card_user:

                break;
            case R.id.end_tour_button:
                if(playerTurn.equals(helper.mAuth.getUid()))
                {
                    updatePlayerTurn();
                }
                break;
        }
    }

    public void getCardBoardInfo(int pos) {
        if(pos == 1){
            user_attack_left.getTag();
        }
        else if(pos == 2) {

        }
        else if(pos == 3) {

        }
    }

    public void updatePlayerTurn()
    {
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

    public void setCardBoard(ImageView hand_user, String pos) {

        CardEntity card = new CardEntity(Integer.parseInt(hand_user.getTag(R.id.atk).toString()),
                Integer.parseInt(hand_user.getTag(R.id.def).toString()),
                Integer.parseInt(hand_user.getTag(R.id.name).toString()),
                hand_user.getTag(R.id.cost).toString(),
                hand_user.getTag(R.id.id).toString());

        if(pos.equals("left")){
            if(currentPlayer == 1) {
                player1_board.setLeftCard(card);
            }
            else {
                player2_board.setLeftCard(card);
            }
        }
        else if(pos.equals("up")) {
            if(currentPlayer == 1) {
                player1_board.setUpCard(card);
            }
            else {
                player2_board.setUpCard(card);
            }
        }
        else if(pos.equals("right")) {
            if(currentPlayer == 1) {
                player1_board.setRightCard(card);
            }
            else {
                player2_board.setRightCard(card);
            }
        }
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
                if(dropPos.getVisibility() == View.INVISIBLE) {
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
                }
                else {
                    isInvokable = false;
                }

                if (!isInvokable)
                    return false;


                String pos = (String) dropZone.getTag();
                Drawable source;
                ArrayList<String> stat = new ArrayList<>();
                switch (label) {
                    case "hand_user_1":
                        source = hand_user_1.getDrawable();
                        dropZone.setImageDrawable(source);
                        stat.add(hand_user_1.getTag(R.id.atk).toString());
                        stat.add(hand_user_1.getTag(R.id.def).toString());
                        setCardBoard(hand_user_1, pos);
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
                        setCardBoard(hand_user_2, pos);
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
                        setCardBoard(hand_user_2, pos);
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
                        setCardBoard(hand_user_2, pos);
                        manaDecrease(Integer.parseInt(hand_user_4.getTag(R.id.cost).toString()));
                        hand_user_4.setVisibility(View.INVISIBLE);
                        Helper.playVoice(this, hand_user_4.getTag(R.id.name).toString());
                        sendPlayerBoard(dropZone, Integer.parseInt(hand_user_4.getTag(R.id.index).toString()), "hand4");
                        break;
                    case "hand_user_5":
                        source = hand_user_5.getDrawable();
                        dropZone.setImageDrawable(source);
                        stat.add(hand_user_5.getTag(R.id.atk).toString());
                        stat.add(hand_user_5.getTag(R.id.def).toString());
                        setCardBoard(hand_user_2, pos);
                        manaDecrease(Integer.parseInt(hand_user_5.getTag(R.id.cost).toString()));
                        hand_user_5.setVisibility(View.INVISIBLE);
                        Helper.playVoice(this, hand_user_5.getTag(R.id.name).toString());
                        sendPlayerBoard(dropZone, Integer.parseInt(hand_user_5.getTag(R.id.index).toString()), "hand5");
                        break;
                }
                setStat(stat, dropZone);

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
    public void setStat(ArrayList<String> stat, ImageView dropZone) {
        String pos = (String) dropZone.getTag();
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

    public TextView getDropPos(ImageView dropZone) {
        String pos = (String) dropZone.getTag();
        switch (pos) {
            case "left": return user_left;
            case "top": return user_top;
            case "right": return user_right;

        }
        return null;
    }

    public void sendPlayerBoard(ImageView dropZone, int index, String handIndex) {
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
            if (parent instanceof CardEntity)
                cardInfo.put("card",(CardEntity) parent);
            cardInfo.put("pos", pos);
            board.put("board-"+pos ,cardInfo);
            player1.setBoard(board);
            Map<String, Object> hand = player1.getHand();
            hand.remove(handIndex);
            player1.setHand(hand);
            player1.setPlayerInfo(party.getId(), 1);
        } else {
            Card parent = player2Card.get(index);
            Map<String, Object> board = new HashMap<>();
            if (parent instanceof CardEntity)
                cardInfo.put("card",(CardEntity) parent);
            cardInfo.put("pos", pos);
            board.put("board-"+pos, cardInfo);
            player2.setBoard(board);
            Map<String, Object> hand = player2.getHand();
            hand.remove(handIndex);
            player2.setHand(hand);
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

                        if(party.getNumberRound() != Integer.parseInt(snapshot.getData().get("number_round").toString()))
                        {
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
                            if (snapshot.getData().get("player2Info") != null) {
                                Map<String, Object> playerInfo = (Map<String, Object>) snapshot.getData().get("player2Info");
                                player2.updatePlayer(playerInfo);
                                opponent_mana.setText(String.format("%d/%d",player2.getMana(),player2.getManaMax()));
                                updateOpponent(player2);
                            }
                        } else if (currentPlayer == 2){
                            if (snapshot.getData().get("player1Info") != null) {
                                Map<String, Object> playerInfo = (Map<String, Object>) snapshot.getData().get("player1Info");
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
                    if (opponent_attack_left.getTag(R.id.name) == null || !opponent_attack_left.getTag(R.id.name).equals(card.get("assetPath").toString())) {
                        opponent_attack_left.setImageDrawable(source);
                        opponent_attack_left.setTag(R.id.name, card.get("assetPath").toString());
                        atk = "atk : " + card.get("attack");
                        opponent_left.setText(atk);
                        opponent_left.setVisibility(View.VISIBLE);
                        Helper.playVoice(this, card.get("assetPath").toString());
                    }
                    break;
                case "right":
                    if (opponent_attack_right.getTag(R.id.name) == null || !opponent_attack_right.getTag(R.id.name).equals(card.get("assetPath"))) {
                        opponent_attack_right.setImageDrawable(source);
                        opponent_attack_right.setTag(R.id.name, card.get("assetPath"));
                        atk = "atk : " + card.get("attack");
                        opponent_right.setText(atk);
                        opponent_right.setVisibility(View.VISIBLE);
                        Helper.playVoice(this, card.get("assetPath").toString());
                    }
                    break;
                case "top":
                    if (opponent_defense.getTag(R.id.name) == null || !opponent_defense.getTag(R.id.name).equals(card.get("assetPath"))) {
                        opponent_defense.setImageDrawable(source);
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
        Log.d("watch_pick","Dans le pick card");
        Log.d("watch_pick","Id func : "+playerTurn);
        Log.d("watch_pick","Id user : "+ helper.mAuth.getUid());
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
                  Card parent = null;
                  if(currentPlayer == 1)
                  {
                      Collections.shuffle(player1Card);
                      parent = player1Card.get(0);
                  }
                  else if(currentPlayer == 2)
                  {
                      Collections.shuffle(player2Card);
                      parent = player2Card.get(0);
                  }

                  Log.d("watch_pick","main utilisateur "+currentPlayer+" vide");

                  if (parent instanceof CardEntity) {
                      CardEntity card = (CardEntity) parent;
                      Drawable path = getDrawable(getResources().getIdentifier(card.getAssetPath(), "drawable", getPackageName()));
                      hands[pos].setImageDrawable(path);
                      hands[pos].setTag(R.id.id, card.getId());
                      hands[pos].setTag(R.id.atk, card.getAttack());
                      hands[pos].setTag(R.id.def, card.getDefend());
                      hands[pos].setTag(R.id.cost, card.getLevel());
                      hands[pos].setTag(R.id.name, card.getAssetPath());
                      hands[pos].setTag(R.id.index, Integer.toString(pos));
                      hands[pos].setVisibility(View.VISIBLE);
                  }
              }

        }
    }



}
