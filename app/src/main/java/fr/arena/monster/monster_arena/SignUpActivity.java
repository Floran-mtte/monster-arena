package fr.arena.monster.monster_arena;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import static android.app.PendingIntent.getActivity;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;
import static android.graphics.Color.YELLOW;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    TextView    sign_in;
    EditText    username;
    EditText    email;
    EditText    pass1;
    EditText    pass2;
    Button      login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e){}

        setContentView(R.layout.activity_sign_up);
        sign_in = (TextView) findViewById(R.id.to_inscription);
        sign_in.setOnClickListener(this);

        username = (EditText) findViewById(R.id.username_input);
        email = (EditText) findViewById(R.id.email_input);
        pass1 = (EditText) findViewById(R.id.password_input);
        pass2 = (EditText) findViewById(R.id.password_confirm_input);
        login = (Button) findViewById(R.id.connect);
        login.setOnClickListener(this);
    }

    final public void goToSignUp() {
        finish();
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    final public void goToHome() {
        finish();
        Intent intent = new Intent(this, homePageActivity.class);
        startActivity(intent);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.to_inscription:
                goToSignUp();
                break;
            case R.id.connect:
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.error_toast,
                        (ViewGroup) findViewById(R.id.error_toast_container));

                TextView text = (TextView) layout.findViewById(R.id.text);
                boolean email_valid = Helper.getInstance().getEmail(email.getText().toString());
                if (username.getText().toString().equals("")) {
                    text.setText(R.string.sign_up_no_username);
                    Helper.getInstance().showErrorToast(getApplicationContext(), layout);
                } else if (!email_valid) {
                    text.setText(R.string.sing_up_error_email);
                    Helper.getInstance().showErrorToast(getApplicationContext(), layout);
                } else if (email.getText().toString().equals("")) {
                    text.setText(R.string.sing_up_error_email);
                    Helper.getInstance().showErrorToast(getApplicationContext(), layout);
                } else if (pass1.getText().toString().equals("")) {
                    text.setText(R.string.sign_up_no_password);
                    Helper.getInstance().showErrorToast(getApplicationContext(), layout);
                } else if (!pass1.getText().toString().equals(pass2.getText().toString())) {
                    text.setText(R.string.sign_up_not_same);
                    Helper.getInstance().showErrorToast(getApplicationContext(), layout);
                } else {
                    Helper.getInstance().mAuth.createUserWithEmailAndPassword(email.getText().toString(), pass1.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String uId = Helper.getInstance().mAuth.getCurrentUser().getUid();
                                        SharedPreferences.Editor editor = getSharedPreferences("App", MODE_PRIVATE).edit();
                                        editor.putString("Username", username.getText().toString());
                                        editor.putString("Email", email.getText().toString());
                                        editor.putString("idUser", uId);
                                        editor.putBoolean("isLogged", true);
                                        editor.apply();
                                        goToHome();
                                        //toast personnalisée
                                        /*LayoutInflater inflater = getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.error_toast,
                                                (ViewGroup) findViewById(R.id.error_toast_container));

                                        TextView text = (TextView) layout.findViewById(R.id.text);
                                        text.setText("inscription reussit");
                                        Helper.getInstance().showErrorToast(getApplicationContext(), layout);*/
                                    } else {
                                    }
                                }
                            });
                }

                break;
        }
    }
}
