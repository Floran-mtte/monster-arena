package fr.arena.monster.monster_arena;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class splash_screen extends AppCompatActivity {

    int progress = 0;
    int max = 100;
    int DELAY = 2500;
    ProgressBar loader;
    ImageView imageView;
    AnimatorSet topSmall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e){}


        setContentView(R.layout.activity_splash_screen);

        Helper.playTheme(this, "intro");

        //hideSystemUI();

        /*final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

        exec.schedule(new Runnable(){
            @Override
            public void run(){

                SharedPreferences prefs = getSharedPreferences("App", MODE_PRIVATE);
                Boolean connected = prefs.getBoolean("isLogged", false);

                if (connected) {
                    goToHome();
                } else {
                    goToSignIn();

                }
            }
        }, DELAY, TimeUnit.SECONDS);*/

        imageView = (ImageView) findViewById(R.id.imageView2);
        Log.i("height 0", imageView.toString());
        logoAnim(); //ici ça marche
        (new Handler()).postDelayed(this::whoRedirect, DELAY);
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

    public void whoRedirect() {
        SharedPreferences prefs = getSharedPreferences("App", MODE_PRIVATE);
        Boolean connected = prefs.getBoolean("isLogged", false);
        int tuto = prefs.getInt("tuto", 2);

        if (connected) {
            switch (tuto) {
                case 0:
                    goToChoose();
                    break;
                case 1:
                    goToTuto();
                    break;
                default:
                    goToHome();
                    break;
            }
        } else {
            goToSignIn();

        }
    }

    public void logoAnim() {
        topSmall = new AnimatorSet();
        ValueAnimator toSmall = ObjectAnimator.ofInt(imageView.getMeasuredHeight(), -600);

        toSmall.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) imageView.getLayoutParams();
                params.height = 794 + val;
                imageView.setLayoutParams(params);
            }
        });
        toSmall.setDuration(DELAY);
        ValueAnimator toTop = ObjectAnimator.ofFloat(imageView, "translationY", -930f);
        toTop.setDuration(DELAY);
        topSmall.play(toSmall).with(toTop);
        topSmall.start();
    }

    public void goToSignIn() {
        isWindowFocused = true;
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public void goToHome() {
        isWindowFocused = true;
        Intent intent = new Intent(this, homePageActivity.class);
        //Intent intent = new Intent(this, ChooseDeckActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public void goToChoose() {
        isWindowFocused = true;
        Intent intent = new Intent(this, ChooseDeckActivity.class);
        //Intent intent = new Intent(this, ChooseDeckActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public void goToTuto() {
        isWindowFocused = true;
        Intent intent = new Intent(this, EditDeckActivity.class);
        //Intent intent = new Intent(this, ChooseDeckActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

}
