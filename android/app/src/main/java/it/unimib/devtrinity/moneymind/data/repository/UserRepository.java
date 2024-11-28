package it.unimib.devtrinity.moneymind.data.repository;

import com.google.firebase.auth.FirebaseUser;

import it.unimib.devtrinity.moneymind.domain.model.User;
import it.unimib.devtrinity.moneymind.utils.FirebaseHelper;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;

public class UserRepository {
   private final FirebaseHelper firebaseHelper;

   public UserRepository(FirebaseHelper firebaseHelper) {
      this.firebaseHelper = firebaseHelper;
   }

   public void authenticate(String email, String password, GenericCallback<User> callback) {
      firebaseHelper.loginUser(email, password, new GenericCallback<>() {
         @Override
         public void onSuccess(FirebaseUser firebaseUser) {
            // Converte FirebaseUser in User e passa al callback
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
      firebaseHelper.registerUser(email, password, name, new GenericCallback<>() {
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

