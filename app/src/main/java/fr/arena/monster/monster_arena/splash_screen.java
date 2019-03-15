package fr.arena.monster.monster_arena;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        goToSignIn();
                    }
                },
                3000);
    }

    final public void goToSignIn() {
        finish();
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
}
