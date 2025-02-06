package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntityWithCategory;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntityWithCategory;
import it.unimib.devtrinity.moneymind.data.repository.RecurringTransactionRepository;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;

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

    private List<Object> combineData(List<TransactionEntityWithCategory> transactions, List<TransactionEntityWithCategory> recurringTransactions) {
        List<Object> combinedList = new ArrayList<>();

        if (recurringTransactions != null && !recurringTransactions.isEmpty()) {
            combinedList.add("divider");
            combinedList.addAll(recurringTransactions);
            combinedList.add("divider-no-text");
        }

        if (transactions != null && !transactions.isEmpty()) {
            combinedList.add("divider");
            combinedList.addAll(transactions);
        }

        return combinedList;
    }

    public void deleteTransactions(List<Object> selectedItems) {
        List<TransactionEntity> transactionsToDelete = new ArrayList<>();
        List<RecurringTransactionEntity> recurringTransactionsToDelete = new ArrayList<>();

        for (Object item : selectedItems) {
            if(item instanceof TransactionEntityWithCategory){
                TransactionEntityWithCategory transaction = (TransactionEntityWithCategory) item;
                if(transaction.getTransaction() instanceof RecurringTransactionEntity){
                    recurringTransactionsToDelete.add((RecurringTransactionEntity) transaction.getTransaction());
                } else {
                    transactionsToDelete.add(transaction.getTransaction());
                }
            }
        }

        transactionRepository.delete(transactionsToDelete);
        recurringTransactionRepository.delete(recurringTransactionsToDelete);
    }
}
