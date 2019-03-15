package fr.arena.monster.monster_arena;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    TextView sign_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e){}

        setContentView(R.layout.activity_sign_up);
        sign_in = (TextView) findViewById(R.id.to_inscription);

        sign_in.setOnClickListener(this);
    }

    final public void goToSignUp() {
        finish();
        Intent intent = new Intent(this, SignInActivity.class);
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
