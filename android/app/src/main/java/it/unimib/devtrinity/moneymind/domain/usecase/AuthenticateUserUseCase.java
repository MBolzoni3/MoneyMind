package it.unimib.devtrinity.moneymind.domain.usecase;

import it.unimib.devtrinity.moneymind.data.repository.UserRepository;
import it.unimib.devtrinity.moneymind.domain.model.User;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;

public class AuthenticateUserUseCase {
    private final UserRepository userRepository;

    public AuthenticateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(String email, String password, GenericCallback<User> callback) {
        userRepository.authenticate(email, password, new GenericCallback<>() {
            @Override
            public void onSuccess(User user) {
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure(errorMessage);
            }
        });
    }
}



