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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class tutoGameActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnDragListener {

    ImageView hand_user_1, hand_user_2, hand_user_3, hand_user_4, hand_user_5, cardDetail, user_attack_left, user_attack_right, user_defense, dropZone = null;
    TextView user_left, user_top, user_right, opponent_left, opponent_top, opponent_right;
    FrameLayout filter;
    String label = null;

    Player  player;
    ArrayList<Card> playerCard = new ArrayList<>();
    ArrayList<CardEntity>   playerCardEntity = new ArrayList<>();
    ArrayList<Card> current_player_hand = new ArrayList<>();

    String TAG = "tutoGameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Helper.playTheme(this, "fight");

        setContentView(R.layout.activity_game_board);

        player = new Player(Helper.getInstance().mAuth.getUid(), 2000, 2);
        getPlayerCard(player.getId());

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
    }

    public void getPlayerCard(String userId)
    {
        DocumentReference docRef = Helper.getInstance().db.collection("User_Deck").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        List list = new ArrayList<String>();
                        list = (List) document.getData().get("cards");

                        getDetailsCard(list, playerCard, playerCardEntity);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void getDetailsCard(List listDoc, ArrayList<Card> rawCard, ArrayList<CardEntity> entity)
    {
        for (int i = 0; i < listDoc.size(); i++) {
            DocumentReference doc = (DocumentReference) listDoc.get(i);
            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> obj = document.getData();

                            Card card = new Card(
                                    obj.get("asset_path").toString(),
                                    obj.get("id").toString(),
                                    Integer.parseInt(obj.get("level").toString()),
                                    obj.get("name").toString(),
                                    Integer.parseInt(obj.get("type_card").toString()),
                                    obj.get("card_detail").toString()
                            );

                            rawCard.add(card);

                            if(card.getCardDetail().equals("entity"))
                            {
                                CardEntity e = new CardEntity(
                                        obj.get("asset_path").toString(),
                                        Integer.parseInt(obj.get("attack").toString()),
                                        Integer.parseInt(obj.get("defend").toString()),
                                        obj.get("id").toString(),
                                        Integer.parseInt(obj.get("level").toString()),
                                        obj.get("name").toString(),
                                        Integer.parseInt(obj.get("type_card").toString()),
                                        obj.get("card_detail").toString()
                                );

                                entity.add(e);

                            }
                            Log.d(TAG, "onComplete: "+rawCard);
                            if (rawCard.size() == listDoc.size()) {
                                getHand();
                            }


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

    public void getHand() {
        Log.d(TAG, "getHand 1: " + playerCard);
        Collections.shuffle(playerCard);
        Log.d(TAG, "getHand 2: " + playerCard);
        for(int i = 0;i < 4;i++)
        {

            current_player_hand.add(playerCard.get(0));
            playerCard.remove(0);
            Log.d(TAG, "getHand: "+current_player_hand.get(i).assetPath);
            //setCardBg(i);
            if (current_player_hand.get(i).assetPath.equals("ratatoskr")) {
                Drawable card = getDrawable(getResources().getIdentifier(current_player_hand.get(i).assetPath, "drawable", getPackageName()));
                Log.d(TAG, "getHand card: " + card);
            }
        }
    }

    public void setCardBg(int i) {
        Drawable card = null;
        Log.d(TAG, "setCardBg: " + i);
        switch (i) {
            case 0:
                card = getDrawable(getResources().getIdentifier(current_player_hand.get(i).assetPath, "drawable", getPackageName()));
                Log.d(TAG, "setCardBg case 0: "+ card);
                /*hand_user_1.setImageDrawable(card);
                hand_user_1.setVisibility(View.VISIBLE);*/
                break;
            case 1:
                card = getDrawable(getResources().getIdentifier(current_player_hand.get(i).assetPath, "drawwable", getPackageName()));
                Log.d(TAG, "setCardBg case 1: "+ card);
                /*hand_user_2.setImageDrawable(card);
                hand_user_2.setVisibility(View.VISIBLE);*/
                break;
            case 2:
                card = getDrawable(getResources().getIdentifier(current_player_hand.get(i).assetPath, "drawwable", getPackageName()));
                Log.d(TAG, "setCardBg case 2: "+ card);
                /*hand_user_3.setImageDrawable(card);
                hand_user_3.setVisibility(View.VISIBLE);*/
                break;
            case 3:
                card = getDrawable(getResources().getIdentifier(current_player_hand.get(i).assetPath, "drawwable", getPackageName()));
                Log.d(TAG, "setCardBg case 3: "+ card);
                /*hand_user_4.setImageDrawable(card);
                hand_user_4.setVisibility(View.VISIBLE);*/
                break;
        }
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
}
