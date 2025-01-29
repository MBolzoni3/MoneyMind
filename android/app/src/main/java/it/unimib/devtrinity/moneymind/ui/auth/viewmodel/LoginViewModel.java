package it.unimib.devtrinity.moneymind.ui.auth.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.devtrinity.moneymind.data.repository.UserRepository;
import it.unimib.devtrinity.moneymind.data.local.entity.User;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.GenericState;

public class LoginViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<GenericState<String>> loginState = new MutableLiveData<>();

    public LoginViewModel() {
        this.userRepository = new UserRepository();
    }

    public LiveData<GenericState<String>> getLoginState() {
        return loginState;
    }

    public void login(String email, String password) {
        loginState.setValue(new GenericState.Loading<>());

        userRepository.authenticate(email, password, new GenericCallback<>() {
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
