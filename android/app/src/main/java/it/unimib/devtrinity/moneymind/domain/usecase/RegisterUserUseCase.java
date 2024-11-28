package it.unimib.devtrinity.moneymind.domain.usecase;

import it.unimib.devtrinity.moneymind.data.repository.UserRepository;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;

public class RegisterUserUseCase {
   private final UserRepository userRepository;

   public RegisterUserUseCase(UserRepository userRepository) {
      this.userRepository = userRepository;
   }

   public void execute(String name, String email, String password, GenericCallback<Void> callback) {
      userRepository.register(name, email, password, new GenericCallback<>() {
         @Override
         public void onSuccess(Void result) {
            callback.onSuccess(null);
         }

         @Override
         public void onFailure(String errorMessage) {
            callback.onFailure(errorMessage);
         }
      });
   }
}

