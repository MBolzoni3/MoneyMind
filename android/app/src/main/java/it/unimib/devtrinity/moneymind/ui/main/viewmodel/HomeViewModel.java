package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;

import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntityWithCategory;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;
import it.unimib.devtrinity.moneymind.utils.Utils;


public class HomeViewModel extends ViewModel {

    private final TransactionRepository transactionRepository;

    private final MutableLiveData<Integer> monthsBack = new MutableLiveData<>(3);
    private final LiveData<Map<String, List<TransactionEntity>>> transactionsByMonth;
    private Long oldestTransactionDate;
    private final MutableLiveData<Integer> currentCarouselPage = new MutableLiveData<>(-1);

    private final Observer<Long> oldestTransactionObserver = date -> oldestTransactionDate = date;

    public HomeViewModel(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;

        this.transactionRepository.getOldestTransactionDate().observeForever(oldestTransactionObserver);
        this.transactionsByMonth = Transformations.switchMap(monthsBack, transactionRepository::getTransactionsByMonth);

        this.transactionsByMonth.observeForever(transactions -> {
            if (currentCarouselPage.getValue() != null && currentCarouselPage.getValue() == -1 && transactions != null && !transactions.isEmpty()) {
                currentCarouselPage.setValue(transactions.size() - 1);
            }
        });
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

    public LiveData<Integer> getCurrentPage() {
        return currentCarouselPage;
    }

    public void setCurrentPage(int page) {
        currentCarouselPage.setValue(page);
    }

}
