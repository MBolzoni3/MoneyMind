package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.data.repository.ExchangeRepository;
import it.unimib.devtrinity.moneymind.data.repository.RecurringTransactionRepository;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;

public class AddTransactionViewModelFactory implements ViewModelProvider.Factory {

    private final TransactionRepository transactionRepository;
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final CategoryRepository categoryRepository;
    private final ExchangeRepository exchangeRepository;

    public AddTransactionViewModelFactory(TransactionRepository transactionRepository,
                                          RecurringTransactionRepository recurringTransactionRepository,
                                          CategoryRepository categoryRepository,
                                          ExchangeRepository exchangeRepository) {
        this.transactionRepository = transactionRepository;
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.categoryRepository = categoryRepository;
        this.exchangeRepository = exchangeRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddTransactionViewModel(transactionRepository, recurringTransactionRepository, categoryRepository, exchangeRepository);
    }
}
