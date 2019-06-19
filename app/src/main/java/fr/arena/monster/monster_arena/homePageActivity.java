package fr.arena.monster.monster_arena;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

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
    Button shopButton;
    Button settingButton;
    FirebaseFirestore db;
    String TAG = "HomePageActivity";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FrameLayout filter;
    TextView textView;
    ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e)
        {

        }

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Helper.playTheme(this, "lobby");

        setContentView(R.layout.activity_home_page);

        shopButton = (Button) findViewById(R.id.shop_button);
        settingButton = (Button) findViewById(R.id.settings_button);
        editDeck = (Button) findViewById(R.id.edit_deck_button);
        playButton = (Button) findViewById(R.id.play_button);
        playButton.setOnClickListener(this);
        editDeck.setOnClickListener(this);
        filter = findViewById(R.id.filter_layout);
        textView = findViewById(R.id.textView2);
        loader = findViewById(R.id.loader);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
    }

    public static boolean isAppWentToBg = false;

    public static boolean isWindowFocused = false;

    protected void onStart() {
        applicationWillEnterForeground();
        super.onStart();

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
        final CollectionReference docRef = db.collection("SearchParty");
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

                        db.collection("SearchParty")
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
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });

                    }
                    else
                    {
                        for (final QueryDocumentSnapshot document : task.getResult())
                        {
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
        final DocumentReference docRef = db.collection("SearchParty").document(documentId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null)
                {
                    Log.w(TAG, "Listen failed.", e);
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

                        db.collection("Party").document(documentId)
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
        db.collection("SearchParty").document(documentId)
                .set(party, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String idParty = documentId;

                        final DocumentReference docRef = db.collection("Party").document(idParty);
                        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null)
                                {
                                    Log.w(TAG, "Listen failed.", e);
                                    return;
                                }

                                if (snapshot != null && snapshot.exists())
                                {
                                    Log.d(TAG, "Current data: " + snapshot.getData());

                                    db.collection("SearchParty").document(idParty)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                    //go to gameBoardActivity
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
                                    //RAS
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
        Log.d("testing app",documentId);
    }

}
