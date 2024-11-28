package it.unimib.devtrinity.moneymind.ui.auth.viewmodel;

import com.google.firebase.auth.FirebaseUser;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import it.unimib.devtrinity.moneymind.data.repository.UserRepository;
import it.unimib.devtrinity.moneymind.domain.model.User;
import it.unimib.devtrinity.moneymind.domain.usecase.AuthenticateUserUseCase;
import it.unimib.devtrinity.moneymind.utils.FirebaseHelper;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.GenericState;

public class LoginViewModel extends ViewModel {
   private final AuthenticateUserUseCase authenticateUserUseCase;
   private final MutableLiveData<GenericState<String>> loginState = new MutableLiveData<>();

   public LoginViewModel() {
      this.authenticateUserUseCase = new AuthenticateUserUseCase(new UserRepository(FirebaseHelper.getInstance()));
   }

   public LiveData<GenericState<String>> getLoginState() {
      return loginState;
   }

   public void login(String email, String password) {
      loginState.setValue(new GenericState.Loading<>());

      authenticateUserUseCase.execute(email, password, new GenericCallback<>() {
         @Override
         public void onSuccess(User user) {
            loginState.setValue(new GenericState.Success<>(user.getEmail()));
         }

         @Override
         public void onFailure(String errorMessage) {
            loginState.setValue(new GenericState.Failure<>(errorMessage));
         }
      });
   }
}
