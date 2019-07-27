package fr.arena.monster.monster_arena;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class EditDeckActivity extends AppCompatActivity implements View.OnClickListener {
    Button bookmark, bookmark_quantity, bookmark1, bookmark_quantity1, bookmark2, bookmark_quantity2;
    int current_bookmark = 0;
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

        bookmark.setOnClickListener(this);
        bookmark1.setOnClickListener(this);
        bookmark2.setOnClickListener(this);

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
}
