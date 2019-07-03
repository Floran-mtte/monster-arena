package fr.arena.monster.monster_arena;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;

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

    ImageView hand_user_1, hand_user_2, hand_user_3, hand_user_4, hand_user_5, cardDetail, user_attack_left, user_attack_right, user_defense, dropZone = null;
    TextView user_left, user_top, user_right, opponent_left, opponent_top, opponent_right;
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
        getParty(partyId);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    public void initParty(String partyId)
    {
        getUsersId(partyId);
    }

    public void getPLayersCard(String player1, String player2)
    {
        getPlayer1Card(player1);
        getPlayer2Card(player2);
    }

    public void getPlayer1Card(String userId)
    {
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

    public void getPlayer2Card(String userId)
    {
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

    public void getDetailsCard(List listDoc, ArrayList<Card> playerCard)
    {
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

                                if(listDoc.size() == playerCard.size())
                                {
                                    listener.onTaskCompleted(true);
                                }
                                addCardToParty(card);
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

    public void addCardToParty(Card card)
    {
        Map<String, Object> party_card = new HashMap<>();
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
                });
    }

    public void getUsersId(String partyId)
    {
        DocumentReference docRef = helper.db.collection("Party").document(partyId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Map<String, Object> party = document.getData();

                        player1 = new Player(party.get("player_1").toString(), 2000, 2);
                        player2 = new Player(party.get("player_2").toString(), 2000,2);

                        Log.d(TAG, player1.getId());
                        Log.d(TAG, player2.getId());

                        currentPlayer = currentPlayer(helper.mAuth.getCurrentUser().getUid());

                        getPLayersCard(player1.getId(), player2.getId());
                        String test = String.valueOf(currentPlayer);
                        Log.d(TAG, test);


                        listener = new OnTaskCompleted() {
                            @Override
                            public void onTaskCompleted(Boolean isFull) {
                                Log.d(TAG, String.valueOf(currentPlayer));
                                if(isFull)
                                {
                                    if(currentPlayer == 1)
                                    {
                                        ImageView[] hands = new ImageView[5];

                                        hands[0] = hand_user_1;
                                        hands[1] = hand_user_2;
                                        hands[2] = hand_user_3;
                                        hands[3] = hand_user_4;
                                        hands[4] = hand_user_5;

                                        Collections.shuffle(player1Card);
                                    }
                                }
                            }
                        };
                        //GetCardTask task1 = new GetCardTask(listener);
                        //task1.execute(player1.getId());

                        /*ImageView[] hands = new ImageView[5];

                        hands[0] = hand_user_1;
                        hands[1] = hand_user_2;
                        hands[2] = hand_user_3;
                        hands[3] = hand_user_4;
                        hands[4] = hand_user_5;

                        Collections.shuffle(player1Card);

                        if(currentPlayer == 1)
                        {
                            for(int i = 0;i < 4;i++)
                            {
                                current_player_hand.add(i, player1Card.get(i));

                                String path = "@drawable/"+player1Card.get(i).getAssetPath();
                                Log.d(TAG,path);
                            }
                        }
                        else if(currentPlayer == 2)
                        {
                            for(int i = 0;i < 4;i++)
                            {

                                current_player_hand.add(i, player1Card.get(i));
                                String path = "@drawable/"+player1Card.get(i).getAssetPath();
                                Log.d(TAG,path);
                            }
                        }

                        else
                        {
                            Log.d(TAG,"passe la pck async");
                        }*/

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,e.toString());
            }
        });
    }

    public int currentPlayer(String userId)
    {
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

    public void getParty(String partyId)
    {
        DocumentReference docRef = helper.db.collection("Party").document(partyId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Map<String, Object> data = document.getData();

                        party = new Party(
                                data.get("created_at").toString(),
                                data.get("updated_at").toString(),
                                data.get("id").toString(),
                                Integer.parseInt(data.get("number_round").toString()),
                                data.get("player_1").toString(),
                                data.get("player_2").toString(),
                                Integer.parseInt(data.get("time_game").toString())
                                );
                        initParty(party.getId());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,e.toString());
            }
        });
    }

    public boolean isInvokable(Card card, Player player)
    {
        if(card.getLevel() <= player.getMana())
        {
            return true;
        }
        return false;
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
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                if (event.getResult()) {
                    if (label == null)
                        return true;
                    Log.d("dragNDropEvent: ", label);
                    Drawable source;
                    String stat;
                    stat = (String) dropZone.getTag();
                    switch (label) {
                        case "hand_user_1":
                            source = hand_user_1.getDrawable();
                            dropZone.setImageDrawable(source);
                            stat = (String) dropZone.getTag();
                            hand_user_1.setVisibility(View.INVISIBLE);
                            break;
                        case "hand_user_2":
                            source = hand_user_2.getDrawable();
                            dropZone.setImageDrawable(source);
                            stat = (String) dropZone.getTag();
                            hand_user_2.setVisibility(View.INVISIBLE);
                            break;
                        case "hand_user_3":
                            source = hand_user_3.getDrawable();
                            dropZone.setImageDrawable(source);
                            stat = (String) dropZone.getTag();
                            hand_user_3.setVisibility(View.INVISIBLE);
                            break;
                        case "hand_user_4":
                            source = hand_user_4.getDrawable();
                            dropZone.setImageDrawable(source);
                            stat = (String) dropZone.getTag();
                            hand_user_4.setVisibility(View.INVISIBLE);
                            break;
                        case "hand_user_5":
                            source = hand_user_5.getDrawable();
                            dropZone.setImageDrawable(source);
                            stat = (String) dropZone.getTag();
                            hand_user_5.setVisibility(View.INVISIBLE);
                            break;
                    }
                    setStat(stat);

                }
                return true;

            default:
                return false;
        }
    }

    @SuppressLint("SetTextI18n")
    public void setStat(String stat) {
        switch (stat) {
            case "left":
                user_left.setText("atk: 2800");
                user_left.setVisibility(View.VISIBLE);
                break;
            case "top":
                user_top.setText("def: 1700");
                user_top.setVisibility(View.VISIBLE);
                break;
            case "right":
                user_right.setText("atk: 2800");
                user_right.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onTaskCompleted(Boolean isFull) {

    }
}
