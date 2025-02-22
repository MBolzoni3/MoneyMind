package it.unimib.devtrinity.moneymind.ui.auth.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.devtrinity.moneymind.data.local.entity.User;
import it.unimib.devtrinity.moneymind.data.repository.UserRepository;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.GenericState;
import it.unimib.devtrinity.moneymind.utils.WorkerHelper;

public class LoginViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<GenericState<String>> loginState = new MutableLiveData<>();

    public LoginViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<GenericState<String>> getLoginState() {
        return loginState;
    }

    public void login(String email, String password, Context context) {
        loginState.setValue(new GenericState.Loading<>());

        userRepository.authenticate(email, password, new GenericCallback<>() {
            @Override
            public void onSuccess(User user) {
                WorkerHelper.triggerManualSync(context)
                        .thenRun(() -> {
                            WorkerHelper.triggerManualRecurring(context)
                                    .thenRun(() -> loginState.setValue(new GenericState.Success<>(user.getEmail())));
                        });
            }

            @Override
            public void onFailure(String errorMessage) {
                loginState.setValue(new GenericState.Failure<>(errorMessage));
            }
        });
    }

}
