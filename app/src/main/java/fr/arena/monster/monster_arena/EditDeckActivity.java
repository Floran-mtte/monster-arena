package fr.arena.monster.monster_arena;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

public class EditDeckActivity extends AppCompatActivity implements View.OnClickListener {
    Button bookmark, bookmark_quantity, bookmark1, bookmark_quantity1, bookmark2, bookmark_quantity2;
    ImageView card_section1, card_section2, card_section3, card_section4;
    int current_bookmark = 0;
    String TAG = "EditDeckActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deck);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        bookmark = (Button) findViewById(R.id.bookmark);
        bookmark_quantity = (Button) findViewById(R.id.bookmark_quantity);
        bookmark1 = (Button) findViewById(R.id.bookmark1);
        bookmark_quantity1 = (Button) findViewById(R.id.bookmark_quantity1);
        bookmark2 = (Button) findViewById(R.id.bookmark2);
        bookmark_quantity2 = (Button) findViewById(R.id.bookmark_quantity2);

        bookmark.setScaleX((float) 1.25);
        bookmark.setScaleY((float) 1.25);
        bookmark_quantity.setScaleX((float) 1.25);
        bookmark_quantity.setScaleY((float) 1.25);

        card_section1 = (ImageView) findViewById(R.id.card_section1);
        card_section2 = (ImageView) findViewById(R.id.card_section2);
        card_section3 = (ImageView) findViewById(R.id.card_section3);
        card_section4 = (ImageView) findViewById(R.id.card_section4);

        bookmark.setOnClickListener(this);
        bookmark1.setOnClickListener(this);
        bookmark2.setOnClickListener(this);

        getUserCollection();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bookmark || id == R.id.bookmark_quantity) {
            if (current_bookmark == 1) {
                bookmark1.setScaleX((float) 1);
                bookmark1.setScaleY((float) 1);
                bookmark_quantity1.setScaleX((float) 1);
                bookmark_quantity1.setScaleY((float) 1);
            } else if (current_bookmark == 2) {
                bookmark2.setScaleX((float) 1);
                bookmark2.setScaleY((float) 1);
                bookmark_quantity2.setScaleX((float) 1);
                bookmark_quantity2.setScaleY((float) 1);
            } else {
                return;
            }
            bookmark.setScaleX((float) 1.25);
            bookmark.setScaleY((float) 1.25);
            bookmark_quantity.setScaleX((float) 1.25);
            bookmark_quantity.setScaleY((float) 1.25);
            bookmark_quantity.setTranslationZ(100);
            current_bookmark = 0;
        } else if (id == R.id.bookmark1 || id == R.id.bookmark_quantity1) {
            if (current_bookmark == 0) {
                bookmark.setScaleX((float) 1);
                bookmark.setScaleY((float) 1);
                bookmark_quantity.setScaleX((float) 1);
                bookmark_quantity.setScaleY((float) 1);
            } else if (current_bookmark == 2) {
                bookmark2.setScaleX((float) 1);
                bookmark2.setScaleY((float) 1);
                bookmark_quantity2.setScaleX((float) 1);
                bookmark_quantity2.setScaleY((float) 1);
            } else {
                return;
            }
            bookmark1.setScaleX((float) 1.25);
            bookmark1.setScaleY((float) 1.25);
            bookmark_quantity1.setScaleX((float) 1.25);
            bookmark_quantity1.setScaleY((float) 1.25);
            bookmark_quantity1.setTranslationZ(100);
            current_bookmark = 1;
        } else if (id == R.id.bookmark2 || id == R.id.bookmark_quantity2) {
            if (current_bookmark == 0) {
                bookmark.setScaleX((float) 1);
                bookmark.setScaleY((float) 1);
                bookmark_quantity.setScaleX((float) 1);
                bookmark_quantity.setScaleY((float) 1);
            } else if (current_bookmark == 1) {
                bookmark1.setScaleX((float) 1);
                bookmark1.setScaleY((float) 1);
                bookmark_quantity1.setScaleX((float) 1);
                bookmark_quantity1.setScaleY((float) 1);
            } else {
                return;
            }
            bookmark2.setScaleX((float) 1.25);
            bookmark2.setScaleY((float) 1.25);
            bookmark_quantity2.setScaleX((float) 1.25);
            bookmark_quantity2.setScaleY((float) 1.25);
            bookmark_quantity2.setTranslationZ(100);
            current_bookmark = 2;
        }
    }

    public void getUserCollection() {
        String uid = Helper.getInstance().mAuth.getCurrentUser().getUid();
        final DocumentReference docRef = Helper.getInstance().db.collection("User_card").document(uid);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    List<String> cardRefs = Helper.convertObjectToListofString(snapshot.getData().get("cards"));
                    getCard(cardRefs);
                }
            }
        });
    }



    public void getCard(List cardsRef) {
        Collections.sort(cardsRef);
        Log.d(TAG, "after sort: " + cardsRef.size());

        String old_ref = "";
        for (int i=0; i < cardsRef.size(); i++) {
            String ref = cardsRef.get(i).toString();
            Log.d(TAG, "getCard: "+ i + ref);
            Log.d(TAG, "getCard: " + old_ref);
            if (old_ref.isEmpty() || !old_ref.equals(ref)) {
                Log.d(TAG, "getCard: not same");
            } else {
                Log.d(TAG, "getCard: same");
            }
            old_ref = ref;
        }
    }
}
