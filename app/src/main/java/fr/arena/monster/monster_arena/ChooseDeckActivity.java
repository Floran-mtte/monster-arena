package fr.arena.monster.monster_arena;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ChooseDeckActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView olympie, ragnarok, armana;
    private Button validate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e){}

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

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
            case R.id.olympie :
                olympie.setBackground(getDrawable(R.drawable.deck_border));
                ragnarok.setBackground(null);
                armana.setBackground(null);
                validate.setVisibility(View.VISIBLE);
                break;
            case R.id.ragnarok :
                ragnarok.setBackground(getDrawable(R.drawable.deck_border));
                olympie.setBackground(null);
                armana.setBackground(null);
                validate.setVisibility(View.VISIBLE);
                break;
            case R.id.armana :
                armana.setBackground(getDrawable(R.drawable.deck_border));
                ragnarok.setBackground(null);
                olympie.setBackground(null);
                validate.setVisibility(View.VISIBLE);
                break;
            case R.id.validate :
                //Todo : attribute the wrigth deck to current user
                break;
        }
    }
}
