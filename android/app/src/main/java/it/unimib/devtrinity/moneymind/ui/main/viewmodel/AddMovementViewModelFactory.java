package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AddMovementViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AddMovementViewModel.class)) {
            // If you had parameters, you would pass them to the constructor here:
            // return (T) new AddMovementViewModel(repository);
            return (T) new AddMovementViewModel();
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}