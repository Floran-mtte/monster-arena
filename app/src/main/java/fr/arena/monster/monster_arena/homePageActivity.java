package fr.arena.monster.monster_arena;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class homePageActivity extends AppCompatActivity implements View.OnClickListener {

    Button editDeck;
    ImageView playButton;
    Button shopButton;
    Button settingButton;
    String TAG = "HomePageActivity";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FrameLayout filter;
    TextView textView;
    ProgressBar loader;
    private FirebaseAuth mAuth;
    ListenerRegistration registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e)
        {

        }

        mAuth = FirebaseAuth.getInstance();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Helper.playTheme(this, "lobby");

        setContentView(R.layout.activity_home_page);

        shopButton = (Button) findViewById(R.id.shop_button);
        settingButton = (Button) findViewById(R.id.settings_button);
        editDeck = (Button) findViewById(R.id.edit_deck_button);
        playButton = (ImageView) findViewById(R.id.play_button);
        playButton.setOnClickListener(this);
        editDeck.setOnClickListener(this);
        filter = findViewById(R.id.filter_layout);
        textView = findViewById(R.id.textView2);
        loader = findViewById(R.id.loader);

    }

    public static boolean isAppWentToBg = false;

    public static boolean isWindowFocused = false;

    protected void onStart() {
        applicationWillEnterForeground();
        super.onStart();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        Helper.getInstance().db.setFirestoreSettings(settings);
    }

    private void applicationWillEnterForeground() {
        if (isAppWentToBg) {
            isAppWentToBg = false;
            Helper.getInstance().mp.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        applicationdidenterbackground();

    }

    public void applicationdidenterbackground() {
        if (!isWindowFocused) {
            isAppWentToBg = true;
            Helper.getInstance().mp.pause();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.play_button:
                configView();
                searchParty();
                break;
            case R.id.edit_deck_button:
                //Intent intent = new Intent(this, homePageActivity.class);
                //startActivity(intent);
                break;
            case R.id.shop_button:
                break;
            case R.id.settings_button:
                break;
        }
    }

    public void configView()
    {
        filter.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        loader.setVisibility(View.VISIBLE);

        playButton.setEnabled(false);
        editDeck.setEnabled(false);
        shopButton.setEnabled(false);
        settingButton.setEnabled(false);
    }

    public void searchParty()
    {
        final CollectionReference docRef = Helper.getInstance().db.collection("SearchParty");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    //if searchParty is empty we create line
                    if(task.getResult().isEmpty())
                    {
                        Long tsLong = System.currentTimeMillis()/1000;
                        String ts = tsLong.toString();

                        Map<String, Object> party = new HashMap<>();
                        party.put("id_user",user.getUid());
                        party.put("timestamp", ts);
                        party.put("id_opponent","");

                        Helper.getInstance().db.collection("SearchParty")
                                .add(party).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(final DocumentReference documentReference)
                            {
                                waitForOpponent(documentReference.getId());
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        Log.d(TAG, "Error writing document", e);
                                    }
                                });

                    }
                    else
                    {
                        for (final QueryDocumentSnapshot document : task.getResult())
                        {
                            Log.d(TAG,document.getData().toString());
                            joinParty(document.getId());
                        }
                    }
                }
                else
                {
                    Log.d(TAG, "get failed with", task.getException());

                }
            }
        });
    }

    public void waitForOpponent(String documentId)
    {
        final DocumentReference docRef = Helper.getInstance().db.collection("SearchParty").document(documentId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null)
                {
                    Log.d(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists())
                {
                    Map<String, Object> search_party = snapshot.getData();
                    String id_opponent = search_party.get("id_opponent").toString();
                    if(id_opponent != "")
                    {
                        Map<String, Object> party = new HashMap<>();
                        Long tsLong = System.currentTimeMillis()/1000;
                        String ts = tsLong.toString();

                        party.put("id", documentId);
                        party.put("time_game", 0);
                        party.put("number_round", 0);
                        party.put("created_at", ts);
                        party.put("updated_at", ts);
                        party.put("player_1", user.getUid());
                        party.put("player_2", id_opponent);
                        party.put("life_point_player_1", 2500);
                        party.put("life_point_player_2", 2500);

                        Helper.getInstance().db.collection("Party").document(documentId)
                                .set(party, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        goToParty(documentId, user.getUid(), id_opponent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    }


                }
                else
                {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    public void joinParty(String documentId)
    {
        Map<String, Object> party = new HashMap<>();
        party.put("id_opponent",user.getUid());
        Helper.getInstance().db.collection("SearchParty").document(documentId)
                .set(party, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String idParty = documentId;

                        DocumentReference query = Helper.getInstance().db.collection("Party").document(idParty);
                        registration = query.addSnapshotListener(
                                (snapshot, e) -> {
                                    if (e != null)
                                    {
                                        Log.w(TAG, "Listen failed.", e);
                                        return;
                                    }

                                    if (snapshot != null && snapshot.exists())
                                    {

                                        Helper.getInstance().db.collection("SearchParty").document(idParty)
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid1) {
                                                        goToParty(idParty, snapshot.getData().get("player_1").toString(), user.getUid(), registration);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error deleting document", e);
                                                    }
                                                });

                                    }
                                    else
                                    {
                                        Log.d("passe","else");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Dans le onFailure");
                        Log.d(TAG, "Error writing document", e);
                    }
                });
    }

    public void goToParty(String documentId, String player1, String player2)
    {
        finish();
        isWindowFocused = true;
        Intent intent = new Intent(this, gameBoardActivity.class);
        intent.putExtra("partyId", documentId);
        intent.putExtra("player_1", player1);
        intent.putExtra("player_2", player2);
        startActivity(intent);
    }

    public void goToParty(String documentId, String player1, String player2, ListenerRegistration registration)
    {
        finish();
        registration.remove();
        isWindowFocused = true;
        Intent intent = new Intent(this, gameBoardActivity.class);
        intent.putExtra("partyId", documentId);
        intent.putExtra("player_1", player1);
        intent.putExtra("player_2", player2);
        startActivity(intent);
    }
}
