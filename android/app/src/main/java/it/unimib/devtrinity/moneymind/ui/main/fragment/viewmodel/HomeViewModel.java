package it.unimib.devtrinity.moneymind.ui.main.fragment.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.GenericState;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;


public class HomeViewModel extends ViewModel {
    private final MutableLiveData<GenericState<List<TransactionEntity>>> homeState = new MutableLiveData<>();
    private TransactionRepository transactionRepository;

    public HomeViewModel() {
        this.transactionRepository = new TransactionRepository();
    }

    public LiveData<GenericState<List<TransactionEntity>>> expense() {
        homeState.setValue(new GenericState.Loading<>());

        transactionRepository.getPositiveTransactions(new GenericCallback<LiveData<List<TransactionEntity>>>() {

            @Override
            public void onSuccess(LiveData<List<TransactionEntity>> positiveTransactions) {
                homeState.setValue(new GenericState.Success<>(positiveTransactions));
            }

            @Override
            public void onFailure(String errorMessage) {
                homeState.setValue(new GenericState.Failure<>(errorMessage));
            }
        });

        return homeState;
    }
}
