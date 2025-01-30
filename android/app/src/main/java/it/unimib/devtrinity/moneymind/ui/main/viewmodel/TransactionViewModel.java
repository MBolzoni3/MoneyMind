package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntityWithCategory;
import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntityWithCategory;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntityWithCategory;
import it.unimib.devtrinity.moneymind.data.repository.BudgetRepository;
import it.unimib.devtrinity.moneymind.data.repository.RecurringTransactionRepository;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;
import it.unimib.devtrinity.moneymind.ui.main.fragment.TransactionFragment;

public class TransactionViewModel extends ViewModel {
    private final TransactionRepository transactionRepository;
    private final RecurringTransactionRepository recurringTransactionRepository;

    public TransactionViewModel(TransactionRepository transactionRepository, RecurringTransactionRepository recurringTransactionRepository) {
        this.transactionRepository = transactionRepository;
        this.recurringTransactionRepository = recurringTransactionRepository;
    }

    public LiveData<List<Object>> getTransactions() {
        return Transformations.switchMap(transactionRepository.getTransactionsWithCategory(), transactions ->
                Transformations.map(recurringTransactionRepository.getRecurringTransactions(), recurringTransactions ->
                        combineData(transactions, recurringTransactions)
                )
        );
    }

    private List<Object> combineData(List<TransactionEntityWithCategory> transactions, List<RecurringTransactionEntityWithCategory> recurringTransactions) {
        List<Object> combinedList = new ArrayList<>();

        if (recurringTransactions != null && !recurringTransactions.isEmpty()) {
            combinedList.addAll(recurringTransactions);
        }

        if (transactions != null && !transactions.isEmpty()) {
            combinedList.add("divider");
            combinedList.addAll(transactions);
        }

        return combinedList;
    }

    public void deleteTransactions(List<Object> selectedItems) {

    }
}
