package fr.arena.monster.monster_arena;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class homePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e){}

        setContentView(R.layout.activity_home_page);
    }
}