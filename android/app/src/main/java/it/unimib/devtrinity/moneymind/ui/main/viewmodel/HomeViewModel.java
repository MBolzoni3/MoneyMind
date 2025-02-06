package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import android.os.Build;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntityWithCategory;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;
import it.unimib.devtrinity.moneymind.utils.Utils;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;


public class HomeViewModel extends ViewModel {
    private final TransactionRepository transactionRepository;

    private final MutableLiveData<Integer> monthsBack = new MutableLiveData<>(3);
    private final LiveData<Map<String, List<TransactionEntity>>> transactionsByMonth;
    private Long oldestTransactionDate = null;

    private final Observer<Long> oldestTransactionObserver = date -> oldestTransactionDate = date;

    public HomeViewModel(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;

        this.transactionRepository.getOldestTransactionDate().observeForever(oldestTransactionObserver);
        this.transactionsByMonth = Transformations.switchMap(monthsBack, transactionRepository::getTransactionsByMonth);
    }

    @Override
    protected void onCleared() {
        transactionRepository.getOldestTransactionDate().removeObserver(oldestTransactionObserver);
        super.onCleared();
    }

    public void loadMoreMonths() {
        int currentMonthsBack = monthsBack.getValue() != null ? monthsBack.getValue() : 0;
        int newMonthsBack = currentMonthsBack + 3;

        long candidateStartDate = Utils.getStartDateFromMonthsBack(newMonthsBack);

        if (oldestTransactionDate != null) {
            if (candidateStartDate < oldestTransactionDate) {
                int requiredMonthsBack = Utils.getMonthsDifference(oldestTransactionDate);
                if (currentMonthsBack < requiredMonthsBack) {
                    monthsBack.setValue(requiredMonthsBack);
                }

                return;
            }
        }

        monthsBack.setValue(newMonthsBack);
    }

    public LiveData<Map<String, List<TransactionEntity>>> getTransactionsByMonth() {
        return transactionsByMonth;
    }

    public LiveData<List<TransactionEntityWithCategory>> getLastTransactions() {
        return transactionRepository.getLastTransactions();
    }

}
