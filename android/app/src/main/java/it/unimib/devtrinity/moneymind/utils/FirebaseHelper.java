package it.unimib.devtrinity.moneymind.utils;

import android.util.Log;

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

    public void loginUser(String email, String password, GenericCallback<FirebaseUser> callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Log.d(TAG, "Login successful: " + (user != null ? user.getEmail() : "No email"));
                        callback.onSuccess(user);
                    } else {
                        Log.e(TAG, "Login failed: " + task.getException().getMessage());
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void registerUser(String email, String password, String name, GenericCallback<FirebaseUser> callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Aggiorna il profilo con il nome
                            user.updateProfile(new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build())
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            callback.onSuccess(user);
                                        } else {
                                            callback.onFailure(profileTask.getException().getMessage());
                                        }
                                    });
                        } else {
                            callback.onFailure("User not found after registration");
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void logoutUser() {
        auth.signOut();
        Log.d(TAG, "User logged out");
    }
}

