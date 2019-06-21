package fr.arena.monster.monster_arena;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.sql.Array;

public class ChooseDeckActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView olympie, ragnarok, armana;
    private Button validate;
    private String deck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Helper.playTheme(this, "lobby");

        setContentView(R.layout.activity_choose_deck);

        olympie = (ImageView) findViewById(R.id.olympie);
        ragnarok = (ImageView) findViewById(R.id.ragnarok);
        armana = (ImageView) findViewById(R.id.armana);
        validate = (Button) findViewById(R.id.validate);

        olympie.setOnClickListener(this);
        ragnarok.setOnClickListener(this);
        armana.setOnClickListener(this);
        validate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.olympie:
                olympie.setBackground(getDrawable(R.drawable.deck_selector));
                ragnarok.setBackground(null);
                armana.setBackground(null);
                validate.setVisibility(View.VISIBLE);
                deck = "olympie";
                break;
            case R.id.ragnarok:
                olympie.setBackground(null);
                ragnarok.setBackground(getDrawable(R.drawable.deck_selector));
                armana.setBackground(null);
                validate.setVisibility(View.VISIBLE);
                deck = "ragnarok";
                break;
            case R.id.armana:
                olympie.setBackground(null);
                ragnarok.setBackground(null);
                armana.setBackground(getDrawable(R.drawable.deck_selector));
                validate.setVisibility(View.VISIBLE);
                deck = "armana";
                break;
            case R.id.validate:
                /*SharedPreferences.Editor editor = getSharedPreferences("App", MODE_PRIVATE).edit();
                editor.putInt("tuto", 1);
                editor.apply();*/
                saveDeck();
                break;
        }
    }

    public void goToFight() {
        finish();
        Intent intent = new Intent(this, gameBoardActivity.class);
        startActivity(intent);
    }

    public void saveDeck() {
        DocumentReference fb_deck = null;
        Log.d("deck", deck);
        switch (deck) {
            case "olympie":
                fb_deck = Helper.getInstance().db.collection("Deck_default").document("CY0frDL46cbJMiqSINdf");
                break;
            case "ragnarok":
                fb_deck = Helper.getInstance().db.collection("Deck_default").document("DFbe2kvzXDn4VKjPMknY");
                break;
            case "armana":
                fb_deck = Helper.getInstance().db.collection("Deck_default").document("pCn1uKHAa7rckfocofcS");
                break;
        }
        if (fb_deck != null) {
            fb_deck.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("done", "DocumentSnapshot data: " + document.getData().get("cards"));
                            Object cards = document.getData().get("cards");
                            Log.d("card", "object: " + cards);

                        } else {
                            Log.d("clear", "No such document");
                        }
                    } else {
                        Log.d("fail", "get failed with ", task.getException());
                    }
                }
            });
        }
    }
}
