package it.unimib.devtrinity.moneymind.ui.main.home.viewmodel;

import android.app.Application;
import android.content.Context;

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

    public HomeViewModel(Application application) {
        this.transactionRepository = new TransactionRepository(application.getApplicationContext());
    }

    public LiveData<GenericState<Double>> getPositiveTransactions() {
        income.setValue(new GenericState.Loading<>());

        transactionRepository.getPositiveTransactions(new GenericCallback<List<TransactionEntity>>() {

            @Override
            public void onSuccess(List<TransactionEntity> positiveTransactions) {
                income.setValue(new GenericState.Success<>(total));
            }

            @Override
            public void onFailure(String errorMessage) {
                income.setValue(new GenericState.Failure<>(errorMessage));
            }
        });

        return income;
    }

    public void getPositiveTransactions(List<TransactionEntity> positiveTransactions) {
        double total = 0.0;

        if (positiveTransactions != null && !positiveTransactions.isEmpty()) {
            for (TransactionEntity transaction : positiveTransactions) {
                total += transaction.getAmount(); // Somma i valori
            }
        }

        homeState.setValue(new GenericState<Double>);
    }

}
