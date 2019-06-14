package fr.arena.monster.monster_arena;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class homePageActivity extends AppCompatActivity implements View.OnClickListener {

    Button editDeck;
    Button playButton;
    FirebaseFirestore db;
    String TAG = "HomePageActivity";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e)
        {

        }

        setContentView(R.layout.activity_home_page);

        editDeck = (Button) findViewById(R.id.edit_deck_button);
        playButton = (Button) findViewById(R.id.play_button);
        playButton.setOnClickListener(this);
        editDeck.setOnClickListener(this);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.play_button:
                final CollectionReference docRef = db.collection("SearchParty");
                docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            if(task.getResult().isEmpty())
                            {
                                Long tsLong = System.currentTimeMillis()/1000;
                                String ts = tsLong.toString();

                                Map<String, Object> party = new HashMap<>();
                                party.put("id_user",user.getUid());
                                party.put("timestamp", ts);
                                party.put("id_opponent","");


                                db.collection("SearchParty")
                                        .add(party)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(final DocumentReference documentReference) {

                                                final DocumentReference docRef = db.collection("SearchParty").document(documentReference.getId());
                                                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                                        @Nullable FirebaseFirestoreException e) {
                                                        if (e != null) {
                                                            Log.w(TAG, "Listen failed.", e);
                                                            return;
                                                        }

                                                        if (snapshot != null && snapshot.exists()) {

                                                            Log.d(TAG, "Current data: " + snapshot.getData());
                                                            Map<String, Object> search_party = snapshot.getData();
                                                            String id_opponent = search_party.get("id_opponent").toString();
                                                            if(id_opponent != "")
                                                            {
                                                                Map<String, Object> party = new HashMap<>();
                                                                Long tsLong = System.currentTimeMillis()/1000;
                                                                String ts = tsLong.toString();

                                                                party.put("id", documentReference.getId());
                                                                party.put("time_game", 0);
                                                                party.put("number_round", 0);
                                                                party.put("created_at", ts);
                                                                party.put("updated_at", ts);

                                                                db.collection("Party").document(documentReference.getId())
                                                                        .set(party, SetOptions.merge())
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.d("Added doc", "added");
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.w(TAG, "Error writing document", e);
                                                                            }
                                                                        });
                                                            }


                                                        } else {
                                                            Log.d(TAG, "Current data: null");
                                                        }
                                                    }
                                                });

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });

                            }
                            else
                            {
                                for (final QueryDocumentSnapshot document : task.getResult())
                                {
                                    Map<String, Object> party = new HashMap<>();
                                    //party.put("id_user",search_party.get("id_user").toString());
                                    //party.put("timestamp", search_party.get("timestamp").toString());
                                    party.put("id_opponent",user.getUid());
                                    db.collection("SearchParty").document(document.getId())
                                            .set(party, SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("Added doc", document.getId());
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error writing document", e);
                                                }
                                            });
                                    Log.d("testing app",document.getId());
                                }
                            }

                            for(QueryDocumentSnapshot document : task.getResult())
                            {
                                //récupération de la ligne et initialisation
                            }
                        }
                        else
                        {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
                break;
            case R.id.edit_deck_button:
                //Intent intent = new Intent(this, homePageActivity.class);
                //startActivity(intent);
                break;
            case R.id.shop_button:
                break;
            case R.id.settings_text:
                break;
        }
    }
}
