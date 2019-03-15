package fr.arena.monster.monster_arena;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    TextView sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e){}

        setContentView(R.layout.activity_sign_in);

        sign_up = (TextView) findViewById(R.id.to_inscription);

        sign_up.setOnClickListener(this);
    }

    final public void goToSignUp() {
        finish();
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.to_inscription:
                goToSignUp();
                break;
        }
    }
}
