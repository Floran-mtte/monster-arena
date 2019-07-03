package fr.arena.monster.monster_arena;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetCardTask extends AsyncTask<String, Void, ArrayList<Card>> {

    Helper helper = Helper.getInstance();
    String TAG = "gameBoardActivity";

    ArrayList<Card>         player1Card = new ArrayList<>();

    //private OnTaskCompleted listener;

    public GetCardTask(OnTaskCompleted listener)
    {
        //this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<Card> doInBackground(String... params) {

        String userId = params[0];

        CardEntity carding = new CardEntity("kvj",2000,2000, "lfkfjgfgfd4f554gfd" ,3,"Chronos",1,"Entity");
        player1Card.add(carding);
        DocumentReference docRef = helper.db.collection("User_Deck").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        List list = new ArrayList<String>();
                        list = (List) document.getData().get("cards");

                        for (int i = 0; i < list.size(); i++) {
                            Log.d(TAG, "Test");
                            DocumentReference doc = (DocumentReference) list.get(i);
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

                                                player1Card.add(card);
                                                //addCardToParty(card);
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


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Card> result) {

        Log.d(TAG,"dans la task");
        Log.d(TAG, String.valueOf(player1Card.size()));
        //listener.onTaskCompleted(player1Card);
        super.onPostExecute(result);

    }

    public void getPlayer1Card(String userId)
    {

    }

    public void getDetailsCard(List listDoc, ArrayList<Card> playerCard)
    {

    }
}
