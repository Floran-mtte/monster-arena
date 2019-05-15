package fr.arena.monster.monster_arena;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 0;
    TextView sign_up;
    SignInButton googleSignIn;
    GoogleSignInClient mGoogleSignInClient;
    EditText email;
    EditText password;
    Button login;
    ConstraintLayout signin_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e){}

        getHeightForLayout();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignIn = (SignInButton) findViewById(R.id.sign_in_button);
        googleSignIn.setPadding(0,0,0,0);
        googleSignIn.setOnClickListener(this);
        //customizeGooglePlusButton(googleSignIn);

        email = (EditText) findViewById(R.id.email_input);
        password = (EditText) findViewById(R.id.password_input);
        login = (Button) findViewById(R.id.connect);
        login.setOnClickListener(this);

        sign_up = (TextView) findViewById(R.id.to_inscription);
        sign_up.setOnClickListener(this);
    }

    protected void getHeightForLayout() {
        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int width = size. x;
        int height = size. y;

        if (height <= 800) {
            setContentView(R.layout.activity_sign_in_height_800);
        } else if (height > 800 && height <= 1280) {
            setContentView(R.layout.activity_sign_in_height_1280);
        } else {
            setContentView(R.layout.activity_sign_in);
        }
    }

    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Log.i("login","already logged");

    }

    final public void goToSignUp() {
        finish();
        Intent intent = new Intent(this, SignUpActivity.class);
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
            case  R.id.sign_in_button:
                signIn();
                break;
            case R.id.connect:
                if (verify_data()) {
                    Helper.getInstance().mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String uId = Helper.getInstance().mAuth.getCurrentUser().getUid();
                                        SharedPreferences.Editor editor = getSharedPreferences("App", MODE_PRIVATE).edit();
                                        editor.putString("Email", email.getText().toString());
                                        editor.putString("idUser", uId);
                                        editor.putBoolean("isLogged", true);
                                        editor.apply();
                                        goToHome();
                                    } else {
                                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                break;
        }
    }

    public boolean verify_data() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.error_toast,
                (ViewGroup) findViewById(R.id.error_toast_container));

        TextView text = (TextView) layout.findViewById(R.id.text);
        if (email.getText().toString().equals("")) {
            text.setText(R.string.sing_up_error_email);
            Helper.getInstance().showErrorToast(getApplicationContext(), layout);
            return false;
        } else {
            boolean email_valid = Helper.getInstance().getEmail(email.getText().toString());
            if (!email_valid) {
                text.setText(R.string.sing_up_error_email);
                Helper.getInstance().showErrorToast(getApplicationContext(), layout);
                return false;
            }
        }

        if (password.getText().toString().equals("")) {
            text.setText(R.string.sign_up_no_password);
            Helper.getInstance().showErrorToast(getApplicationContext(), layout);
            return false;
        } else {
            if (password.getText().toString().length() < 6) {
                text.setText(R.string.sign_up_no_password);
                Helper.getInstance().showErrorToast(getApplicationContext(), layout);
                return false;
            }
        }

        return true;
    }

    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         String debug =  Integer.toString(requestCode);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Log.i("Login","Success");
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google signIn error", "signInResult:failed code=" + e.getStatusCode());

        }
    }

    public static void customizeGooglePlusButton(SignInButton signInButton) {
        for (int i = 0; i < signInButton.getChildCount(); i++)
        {
            View v = signInButton.getChildAt(i);

            String test = v.toString();

            if (v instanceof TextView)
            {
                TextView tv = (TextView) v;
                tv.setText(R.string.sign_in_google);
                tv.setBackgroundResource(R.color.colorPrimary);
                tv.setPadding(5,5,5,5);
                return;
            }

        }
    }

}
