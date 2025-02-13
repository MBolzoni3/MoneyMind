package it.unimib.devtrinity.moneymind.ui.auth.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.devtrinity.moneymind.data.repository.UserRepository;

public class RegisterViewModelFactory implements ViewModelProvider.Factory {

    private final UserRepository userRepository;

    public RegisterViewModelFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}

