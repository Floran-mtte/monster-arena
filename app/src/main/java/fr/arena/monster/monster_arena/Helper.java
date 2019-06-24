package fr.arena.monster.monster_arena;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class Helper {

    FirebaseAuth mAuth  = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MediaPlayer mp;
    String lastSound = "";

    public static boolean getEmail(String email) {
        Log.i("check email", email);
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        boolean response = pattern.matcher(email).matches();
        Log.i("check email", String.valueOf(response));
        return response;
    }

    public static void showErrorToast(Context context, View view) {
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    public static void playTheme(Context context, String sound) {
        if (Helper.getInstance().mp != null) {
            if (sound == Helper.getInstance().lastSound)
                return;
            Helper.getInstance().mp.stop();
        }

        Helper.getInstance().lastSound = sound;

        if (sound == "lobby") {
            Helper.getInstance().mp = MediaPlayer.create(context, R.raw.lobby);
            Helper.getInstance().mp.setLooping(true);
        } else if (sound == "fight") {
            Helper.getInstance().mp = MediaPlayer.create(context, R.raw.fight);
            Helper.getInstance().mp.setLooping(true);
        } else if (sound == "victory") {
            Helper.getInstance().mp = MediaPlayer.create(context, R.raw.victory);
            Helper.getInstance().mp.setLooping(true);
        } else if (sound == "lose") {
            Helper.getInstance().mp = MediaPlayer.create(context, R.raw.lose);
            Helper.getInstance().mp.setLooping(true);
        } else if (sound == "intro") {
            Helper.getInstance().mp = MediaPlayer.create(context, R.raw.intro);
        }
        //Helper.getInstance().mp.start();
    }

    public static void replayTheme() {
        Helper.getInstance().mp.start();
    }

    private static final Helper ourInstance = new Helper();

    public static Helper getInstance() {
        return ourInstance;
    }

    private Helper() {
    }
}
