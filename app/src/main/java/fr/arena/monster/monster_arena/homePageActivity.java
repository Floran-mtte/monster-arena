package fr.arena.monster.monster_arena;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class homePageActivity extends AppCompatActivity implements View.OnClickListener {

    Button editDeck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e){}

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setContentView(R.layout.activity_home_page);

        editDeck = (Button) findViewById(R.id.edit_deck_button);

        editDeck.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.edit_deck_button:
                Intent intent = new Intent(this, homePageActivity.class);
                startActivity(intent);
                break;
            case R.id.shop_button:
                break;
            case R.id.settings_text:
                break;
        }
    }
}
