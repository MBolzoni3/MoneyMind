package it.unimib.devtrinity.moneymind.ui.auth.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.unimib.devtrinity.moneymind.data.repository.UserRepository;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.GenericState;

public class RegisterViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<GenericState<Void>> registerState = new MutableLiveData<>();

    public RegisterViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<GenericState<Void>> getRegisterState() {
        return registerState;
    }

    public void register(String name, String email, String password) {
        registerState.setValue(new GenericState.Loading<>());

        userRepository.register(name, email, password, new GenericCallback<>() {
            @Override
            public void onSuccess(Void result) {
                registerState.setValue(new GenericState.Success<>(null));
            }

            @Override
            public void onFailure(String errorMessage) {
                registerState.setValue(new GenericState.Failure<>(errorMessage));
            }
        });
    }

}

