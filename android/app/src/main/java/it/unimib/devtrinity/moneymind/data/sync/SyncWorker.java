package it.unimib.devtrinity.moneymind.data.sync;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import it.unimib.devtrinity.moneymind.data.repository.BudgetRepository;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.data.repository.GoalRepository;
import it.unimib.devtrinity.moneymind.data.repository.RecurringTransactionRepository;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;

public class SyncWorker extends Worker {

    private static final String TAG = SyncWorker.class.getSimpleName();

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final GoalRepository goalRepository;
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final TransactionRepository transactionRepository;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);

        budgetRepository = new BudgetRepository(context);
        categoryRepository = new CategoryRepository(context);
        goalRepository = new GoalRepository(context);
        recurringTransactionRepository = new RecurringTransactionRepository(context);
        transactionRepository = new TransactionRepository(context);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        Log.d(TAG, "Starting sync work");

        budgetRepository.syncBudgets();
        categoryRepository.syncCategories();
        goalRepository.syncGoals();
        recurringTransactionRepository.syncRecurringTransactions();
        transactionRepository.syncTransactions();

        Log.d(TAG, "Finished sync work");
        return ListenableWorker.Result.success();
    }
}