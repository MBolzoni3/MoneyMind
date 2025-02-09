package it.unimib.devtrinity.moneymind.data.repository;

import com.google.firebase.auth.FirebaseUser;

import it.unimib.devtrinity.moneymind.data.local.entity.User;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class UserRepository {

    public void authenticate(String email, String password, GenericCallback<User> callback) {
        FirebaseHelper.getInstance().loginUser(email, password, new GenericCallback<>() {
            @Override
            public void onSuccess(FirebaseUser firebaseUser) {
                User user = new User(
                        firebaseUser.getUid(),
                        firebaseUser.getDisplayName(),
                        firebaseUser.getEmail()
                );
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure(errorMessage);
            }
        });
    }

    public void register(String name, String email, String password, GenericCallback<Void> callback) {
        FirebaseHelper.getInstance().registerUser(email, password, name, new GenericCallback<>() {
            @Override
            public void onSuccess(FirebaseUser firebaseUser) {
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure(errorMessage);
            }
        });
    }
}

