package dev.moutamid.earnreal;

import com.google.firebase.database.FirebaseDatabase;

public class ContextEarnReal extends android.app.Application{

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
