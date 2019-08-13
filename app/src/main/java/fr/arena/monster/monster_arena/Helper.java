package fr.arena.monster.monster_arena;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Helper {

    FirebaseAuth mAuth  = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MediaPlayer mp;
    String lastSound = "";
    User user = new User();

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
        Log.i("sound", sound);
        if (Helper.getInstance().mp != null) {
            Log.i("sound 2", sound);
            if (sound == Helper.getInstance().lastSound)
                return;
            Log.i("sound 3", sound);
            Helper.getInstance().mp.stop();
        }

        Helper.getInstance().lastSound = sound;

        if (sound == "lobby") {
            Helper.getInstance().mp = MediaPlayer.create(context, R.raw.lobby);
        } else if (sound == "fight") {
            Helper.getInstance().mp = MediaPlayer.create(context, R.raw.fight);
        } else if (sound == "victory") {
            Helper.getInstance().mp = MediaPlayer.create(context, R.raw.victory);
        } else if (sound == "lose") {
            Helper.getInstance().mp = MediaPlayer.create(context, R.raw.lose);
        } else if (sound == "intro") {
            Helper.getInstance().mp = MediaPlayer.create(context, R.raw.intro);
        }
        Helper.getInstance().mp.setLooping(true);
        //Helper.getInstance().mp.start();
    }

    public static void playVoice(Context context,  String entity) {
        MediaPlayer mp = MediaPlayer.create(context, context.getResources().getIdentifier("raw/"+entity, null, context.getPackageName()));
        mp.start();
    }

    public static List<String> convertObjectToListofString(Object obj) {
        List<?> list = new ArrayList<>();
        if (obj.getClass().isArray()) {
            list = Arrays.asList((Object[])obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<?>)obj);
        }

        List<String> strings = list.stream()
                .map(object -> Objects.toString(object, null))
                .collect(Collectors.toList());
        return strings;
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
