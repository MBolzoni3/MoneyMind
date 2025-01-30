package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.math.BigDecimal;
import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;


public class HomeViewModel extends ViewModel {
    private final TransactionRepository transactionRepository;

    public HomeViewModel(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public LiveData<List<TransactionEntity>> getTransactions() {
        return transactionRepository.getTransactions();
    }

    public int setProgressBar(BigDecimal incomeTotal) {
        double doubleIncome = incomeTotal.doubleValue();
        double roundedIncome = Math.ceil((doubleIncome + 99.0) / 100) * 100;

        return (int) ((doubleIncome*100)/roundedIncome);
    }
}
