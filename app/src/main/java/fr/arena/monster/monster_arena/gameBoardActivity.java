package fr.arena.monster.monster_arena;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class gameBoardActivity extends AppCompatActivity {

    ArrayList<Card> player_1 = new ArrayList<Card>();
    ArrayList<Card> player_2 = new ArrayList<Card>();
    String player_1_id;
    String player_2_id;
    Map<String, Object> user_card = new HashMap<>();
    String userId;
    String userOpponent;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "gameBoardActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Helper.playTheme(this, "fight");

        setContentView(R.layout.activity_game_board);
        String sessionId = getIntent().getStringExtra("partyId");
        initParty(sessionId);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    public void test()
    {
        Log.d(TAG,"test"+ player_1.size() );
        for (int i = 0; i < player_1.size(); i++)
        {
           Card card = player_1.get(i);
            Log.d(TAG,card.getName());
        }

        //Log.d(TAG, userId);
        //Log.d(TAG, userOpponent);
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

                            for (int i = 0; i < list.size(); i++) {
                            DocumentReference doc = (DocumentReference) list.get(i);
                            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Map<String, Object> obj = document.getData();

                                            Card card = new Card(
                                                    obj.get("asset_path").toString(),
                                                    Integer.parseInt(obj.get("defend").toString()),
                                                    Integer.parseInt(obj.get("attack").toString()),
                                                    obj.get("id").toString(),
                                                    Integer.parseInt(obj.get("level").toString()),
                                                    obj.get("name").toString(),
                                                    Integer.parseInt(obj.get("type_card").toString())té
                                            );

                                            player_1.add(card);
                                            test();

                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });
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

                        for (int i = 0; i < list.size(); i++) {
                            DocumentReference doc = (DocumentReference) list.get(i);
                            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Map<String, Object> obj = document.getData();

                                            Card card = new Card(
                                                    obj.get("asset_path").toString(),
                                                    Integer.parseInt(obj.get("defend").toString()),
                                                    Integer.parseInt(obj.get("attack").toString()),
                                                    obj.get("id").toString(),
                                                    Integer.parseInt(obj.get("level").toString()),
                                                    obj.get("name").toString(),
                                                    Integer.parseInt(obj.get("type_card").toString())
                                            );

                                            player_2.add(card);

                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });
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



    public void getDetailsCard(DocumentReference doc)
    {

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
                        player_1_id = party.get("player_1").toString();
                        player_2_id = party.get("player_2").toString();

                        getPlayer1Card(player_1_id);
                        getPlayer2Card(player_2_id);

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

}
