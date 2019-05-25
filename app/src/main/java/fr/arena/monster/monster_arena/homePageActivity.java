package fr.arena.monster.monster_arena;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class homePageActivity extends AppCompatActivity implements View.OnClickListener {

    Button editDeck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e){}

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Helper.playTheme(this, "lobby");

        setContentView(R.layout.activity_home_page);

        editDeck = (Button) findViewById(R.id.edit_deck_button);

        editDeck.setOnClickListener(this);
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
