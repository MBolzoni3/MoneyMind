package it.unimib.devtrinity.moneymind.data.sync;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import it.unimib.devtrinity.moneymind.data.repository.BudgetRepository;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.data.repository.GoalRepository;
import it.unimib.devtrinity.moneymind.data.repository.RecurringTransactionRepository;
import it.unimib.devtrinity.moneymind.data.repository.ServiceLocator;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class SyncWorker extends Worker {

    private static final String TAG = SyncWorker.class.getSimpleName();

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final GoalRepository goalRepository;
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final TransactionRepository transactionRepository;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);

        budgetRepository = ServiceLocator.getInstance().getBudgetRepository(context);
        categoryRepository = ServiceLocator.getInstance().getCategoryRepository(context);
        goalRepository = ServiceLocator.getInstance().getGoalRepository(context);
        recurringTransactionRepository = ServiceLocator.getInstance().getRecurringTransactionRepository(context);
        transactionRepository = ServiceLocator.getInstance().getTransactionRepository(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Starting sync work");

        categoryRepository.sync();
        if (FirebaseHelper.getInstance().isUserLoggedIn()) {
            budgetRepository.sync();
            goalRepository.sync();
            recurringTransactionRepository.sync();
            transactionRepository.sync();
        }

        Log.d(TAG, "Finished sync work");
        return Result.success();
    }

}
