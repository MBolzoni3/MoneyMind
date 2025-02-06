package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;

public class AddBudgetViewModelFactory implements ViewModelProvider.Factory {
    private final CategoryRepository repository;

    public AddBudgetViewModelFactory(CategoryRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddBudgetViewModel(repository);
    }
}
