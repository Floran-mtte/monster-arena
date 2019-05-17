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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 0;
    TextView sign_up;
    SignInButton googleSignIn;
    GoogleSignInClient mGoogleSignInClient;
    EditText email;
    EditText password;
    Button login;
    ConstraintLayout signin_container;
    CallbackManager callbackManager;
    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e){}

        setContentView(R.layout.activity_sign_in);

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignIn = (SignInButton) findViewById(R.id.sign_in_button);
        googleSignIn.setPadding(0,0,0,0);
        googleSignIn.setOnClickListener(this);
        customizeGooglePlusButton(googleSignIn);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("fb-success", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("fb-cancel", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("fb-error", "facebook:onError", error);
                // ...
            }
        });

        email = (EditText) findViewById(R.id.email_input);
        password = (EditText) findViewById(R.id.password_input);
        login = (Button) findViewById(R.id.connect);
        login.setOnClickListener(this);

        sign_up = (TextView) findViewById(R.id.to_inscription);
        sign_up.setOnClickListener(this);
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


        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        Helper.getInstance().mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = Helper.getInstance().mAuth.getCurrentUser();
                            goToHome();
                        } else {
                            String uId = Helper.getInstance().mAuth.getCurrentUser().getUid();
                            String email = Helper.getInstance().mAuth.getCurrentUser().getEmail();
                            SharedPreferences.Editor editor = getSharedPreferences("App", MODE_PRIVATE).edit();
                            editor.putString("Email", email);
                            editor.putString("idUser", uId);
                            editor.putBoolean("isLogged", true);
                            editor.apply();
                        }
                    }
                });
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

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("fb-connect", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        Helper.getInstance().mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("fb-connect-success", "signInWithCredential:success");
                            FirebaseUser user = Helper.getInstance().mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("fb-connect-error", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

}
