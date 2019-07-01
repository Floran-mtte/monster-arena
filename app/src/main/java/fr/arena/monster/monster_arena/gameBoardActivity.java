package fr.arena.monster.monster_arena;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class gameBoardActivity extends AppCompatActivity {

    Party   party;
    Player  player1;
    Player  player2;

    ArrayList<Card>         player1Card = new ArrayList<>();
    ArrayList<CardEntity>   player1CardEntity = new ArrayList<>();
    ArrayList<Card>         player2Card = new ArrayList<>();
    ArrayList<CardEntity>   player2CardEntity = new ArrayList<>();

    ArrayList<Card> current_player_hand = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "gameBoardActivity";
    int currentPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Helper.playTheme(this, "fight");

        setContentView(R.layout.activity_game_board);
        String partyId = getIntent().getStringExtra("partyId");
        getParty(partyId);
        initParty(party.getId());

        Helper helper = Helper.getInstance();
        FirebaseUser user = helper.mAuth.getCurrentUser();
        currentPlayer = currentPlayer(user.getUid());

        Random r = new Random();
        int lastNumber = 999;
        int number = 0;
        if(currentPlayer == 1)
        {
            for(int i = 0;i < 4;i++)
            {
                if(lastNumber != number)
                {
                    number = r.nextInt(0 - player1Card.size());
                }
                lastNumber = number;

                current_player_hand.add(player1Card.get(number));
            }

        }
        else if(currentPlayer == 2)
        {
            for(int i = 0;i < 4;i++)
            {
                if(lastNumber != number)
                {
                    number = r.nextInt(0 - player2Card.size());
                }
                lastNumber = number;

                current_player_hand.add(player2Card.get(number));
            }
        }

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

    public void getPlayer1Card(String userId)
    {
        DocumentReference docRef = db.collection("User_Deck").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        List list = new ArrayList<String>();
                        list = (List) document.getData().get("cards");

                        getDetailsCard(list, player1Card, player1CardEntity);

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
        DocumentReference docRef = db.collection("User_Deck").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        List list = new ArrayList<String>();
                        list = (List) document.getData().get("cards");

                        getDetailsCard(list, player2Card, player2CardEntity);

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
        for (int i = 0; i < rawCard.size(); i++) {
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
                                addCardToPartyEntity(e);

                            }

                            addCardToParty(card);


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

        db.collection("Party_Card").document()
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


    public void addCardToPartyEntity(CardEntity card)
    {

        Map<String, Object> party_card = new HashMap<>();
        party_card.put("id", card.getId());
        party_card.put("id_party", party.getId());
        party_card.put("attack", card.getAttack());
        party_card.put("defend", card.getDefend());
        party_card.put("active", true);

        db.collection("Party_Card_Entity").document()
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
        DocumentReference docRef = db.collection("Party").document(partyId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Map<String, Object> party = document.getData();

                        player1 = new Player(party.get("player_1").toString(), 2000, 2);
                        player2 = new Player(party.get("player_2").toString(), 2000,2);

                        getPlayer1Card(player1.getId());
                        getPlayer2Card(player2.getId());

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
        DocumentReference docRef = db.collection("Party").document(partyId);

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
        return true;
    }
}
