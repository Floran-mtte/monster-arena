package fr.arena.monster.monster_arena;

import com.google.firebase.auth.FirebaseAuth;

public class Helper {

    FirebaseAuth mAuth  = FirebaseAuth.getInstance();

    private static final Helper ourInstance = new Helper();

    public static Helper getInstance() {
        return ourInstance;
    }

    private Helper() {
    }
}
