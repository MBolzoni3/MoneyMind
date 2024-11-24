package it.unimib.devtrinity.moneymind.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import it.unimib.devtrinity.moneymind.ui.MainActivity;
import it.unimib.devtrinity.moneymind.ui.MainNavigationActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";

    private static FirebaseHelper instance;

    public static FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    private final FirebaseAuth auth;

    private FirebaseHelper() {
        auth = FirebaseAuth.getInstance();
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void loginUser(String email, String password, Context context) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Log.d(TAG, "Login successful: " + user.getEmail());

                        NavigationHelper.navigateToMain(context);
                    } else {
                        Toast.makeText(context, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void registerUser(String email, String password, Context context){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Log.d(TAG, "Registration successful: " + user.getEmail());

                        Toast.makeText(context, "Registrazione completata", Toast.LENGTH_SHORT).show();

                        NavigationHelper.navigateToMain(context);
                    } else {
                        Toast.makeText(context, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void logoutUser(Context context) {
        auth.signOut();
        Log.d(TAG, "User logged out");

        NavigationHelper.navigateToLogin(context);
    }
}

