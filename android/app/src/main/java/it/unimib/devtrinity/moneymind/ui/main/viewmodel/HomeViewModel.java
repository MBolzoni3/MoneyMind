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
    private final MutableLiveData<GenericState<Double>> income = new MutableLiveData<>();
    private final TransactionRepository transactionRepository;

    public HomeViewModel(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public LiveData<GenericState<Double>> getPositiveTransactions() {
        income.setValue(new GenericState.Loading<>());

        transactionRepository.getPositiveTransactions(new GenericCallback<List<TransactionEntity>>() {

            @Override
            public void onSuccess(List<TransactionEntity> positiveTransactions) {
                double total = 0;
                for (TransactionEntity transaction : positiveTransactions) {
                    total += transaction.getAmount();
                }
                income.setValue(new GenericState.Success<>(total));
            }

            @Override
            public void onFailure(String errorMessage) {
                income.setValue(new GenericState.Failure<>(errorMessage));
            }
        });

        return income;
    }

}
