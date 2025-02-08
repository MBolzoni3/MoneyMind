package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.data.repository.ExchangeRepository;

public class AddTransactionViewModelFactory implements ViewModelProvider.Factory {
    private final CategoryRepository categoryRepository;
    private final ExchangeRepository exchangeRepository;

    public AddTransactionViewModelFactory(CategoryRepository categoryRepository, ExchangeRepository exchangeRepository) {
        this.categoryRepository = categoryRepository;
        this.exchangeRepository = exchangeRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddTransactionViewModel(categoryRepository, exchangeRepository);
    }
}
