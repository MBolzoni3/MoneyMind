package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.GenericState;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;


public class HomeViewModel extends ViewModel {
    private final TransactionRepository transactionRepository;

    public HomeViewModel(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public LiveData<List<TransactionEntity>> getPositiveTransactions() {
        return transactionRepository.getPositiveTransactions();
    }

}
