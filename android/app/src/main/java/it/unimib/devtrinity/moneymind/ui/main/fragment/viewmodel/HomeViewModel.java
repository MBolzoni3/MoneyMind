package it.unimib.devtrinity.moneymind.ui.main.fragment.viewmodel;

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
    private final MutableLiveData<GenericState<Double>> homeState = new MutableLiveData<>();
    private TransactionRepository transactionRepository;
    private Context context; //DA RIVEDERE

    public HomeViewModel() {
        this.transactionRepository = new TransactionRepository(context);
    }

    public LiveData<GenericState<Double>> expense() {
        homeState.setValue(new GenericState.Loading<>());

        transactionRepository.getPositiveTransactions(new GenericCallback<List<TransactionEntity>>() {

            @Override
            public void onSuccess(List<TransactionEntity> positiveTransactions) {
                double total = 0.0;

                if (positiveTransactions != null && !positiveTransactions.isEmpty()) {
                    for (TransactionEntity transaction : positiveTransactions) {
                        total += transaction.getAmount(); // Somma i valori
                    }
                }

                homeState.setValue(new GenericState.Success<>(total));
            }

            @Override
            public void onFailure(String errorMessage) {
                homeState.setValue(new GenericState.Failure<>(errorMessage));
            }
        });

        return homeState;
    }
}
