package fr.arena.monster.monster_arena;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class EditDeckActivity extends AppCompatActivity implements View.OnClickListener {
    Button bookmark, bookmark_quantity, bookmark1, bookmark_quantity1, bookmark2, bookmark_quantity2;
    ImageView card_section1, card_section2, card_section3, card_section4;
    int current_bookmark = 0;
    String TAG = "EditDeckActivity";
    ArrayList<Card> collection_card = new ArrayList<>();
    ArrayList<Card> deck = new ArrayList<>();
    ArrayList object_ref = new ArrayList();

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
                    object_ref = (ArrayList) snapshot.getData().get("cards");
                    getCard(cardRefs);
                }
            }
        });
    }



    public void getCard(List cardsRef) {
        Collections.sort(cardsRef);
        Log.d(TAG, "after sort: " + cardsRef.size());
        ArrayList cards = new ArrayList();
        ArrayList repet = new ArrayList();
        String old_ref = "";
        ArrayList tmp = new ArrayList();

        for (int i=0; i < cardsRef.size(); i++) {
            String ref = cardsRef.get(i).toString();
            if (old_ref.isEmpty() || !old_ref.equals(ref)) {
                DocumentReference doc = null;
                for (int j=0; j < object_ref.size(); j++) {
                    Object obj = object_ref.get(j);
                    if (ref.equals(obj.toString())) {
                        doc = (DocumentReference) obj;
                    }
                }

                cards.add(doc);
                if (!old_ref.isEmpty()) {
                    repet.add(tmp);
                    tmp = new ArrayList();
                }
                tmp.add(0, doc);
                tmp.add(1, 0);

            } else {
                ArrayList cpy = new ArrayList();
                cpy.add(tmp.get(0));
                int rp = (Integer) tmp.get(1);
                cpy.add(rp + 1);
                tmp = cpy;
            }
            old_ref = ref;
        }
        object_ref = repet;
        getDetailsCard(cards);
    }

    public void getDetailsCard(ArrayList cards) {
        for (int i=0; i < cards.size(); i++) {
            DocumentReference doc = (DocumentReference) cards.get(i);
            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> obj = document.getData();
                            if (obj.get("card_detail").equals("entity")) {
                                CardEntity card = new CardEntity(
                                        obj.get("asset_path").toString(),
                                        Integer.parseInt(obj.get("defend").toString()),
                                        Integer.parseInt(obj.get("attack").toString()),
                                        obj.get("id").toString(),
                                        Integer.parseInt(obj.get("level").toString()),
                                        obj.get("name").toString(),
                                        Integer.parseInt(obj.get("type_card").toString()),
                                        obj.get("card_detail").toString(),
                                        obj.get("familly").toString()
                                );
                                collection_card.add(card);
                                if (collection_card.size() == cards.size()) {
                                    sortByFamilly();
                                }
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

    public void sortByFamilly() {
        Log.d(TAG, "sortByFamilly: "+collection_card);
        Collections.sort(collection_card, new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                if (o1 instanceof CardEntity && o2 instanceof CardEntity) {
                    CardEntity entity1 = (CardEntity) o1;
                    CardEntity entity2 = (CardEntity) o2;
                    return entity1.getRef_familly().compareTo(entity2.getRef_familly());
                }
                return 0;
            }
        });
        Log.d(TAG, "sortByFamilly: "+collection_card);
        Log.d(TAG, "sortByFamilly: "+collection_card.get(0).getAssetPath());
        showCard(0, 4);
    }

    public void showCard(int index, int max) {
        for (int i=index; i < max; i++) {
            if (i == collection_card.size())
                break;

            Card parent = collection_card.get(i);
            if (parent instanceof CardEntity) {
                CardEntity card = (CardEntity) parent;
                showGod(i, index, card);
            }
        }
    }

    public void showGod(int i, int index, CardEntity card) {
        Drawable path = getDrawable(getResources()
                .getIdentifier(card.getAssetPath(), "drawable", getPackageName()));
        if (i == index) {
            card_section1.setImageDrawable(path);
        } else if (i == index + 1) {
            card_section2.setImageDrawable(path);
        } else if (i == index + 2) {
            card_section3.setImageDrawable(path);
        } else {
            card_section4.setImageDrawable(path);
        }
    }
}
