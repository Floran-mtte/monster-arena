package fr.arena.monster.monster_arena;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class Helper {

    FirebaseAuth mAuth  = FirebaseAuth.getInstance();

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

    private static final Helper ourInstance = new Helper();

    public static Helper getInstance() {
        return ourInstance;
    }

    private Helper() {
    }
}
