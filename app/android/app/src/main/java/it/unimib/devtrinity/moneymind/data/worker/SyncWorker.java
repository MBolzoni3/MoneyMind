package it.unimib.devtrinity.moneymind.data.worker;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.unimib.devtrinity.moneymind.data.repository.BudgetRepository;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.data.repository.GoalRepository;
import it.unimib.devtrinity.moneymind.data.repository.RecurringTransactionRepository;
import it.unimib.devtrinity.moneymind.data.repository.ServiceLocator;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;
import it.unimib.devtrinity.moneymind.utils.Utils;
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

        Application application = (Application) context.getApplicationContext();

        budgetRepository = ServiceLocator.getInstance().getBudgetRepository(application);
        categoryRepository = ServiceLocator.getInstance().getCategoryRepository(application);
        goalRepository = ServiceLocator.getInstance().getGoalRepository(application);
        recurringTransactionRepository = ServiceLocator.getInstance().getRecurringTransactionRepository(application);
        transactionRepository = ServiceLocator.getInstance().getTransactionRepository(application);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            if(!Utils.isInternetAvailable(getApplicationContext())){
                return Result.failure();
            }

            List<CompletableFuture<Void>> syncFutures = new ArrayList<>();
            syncFutures.add(categoryRepository.sync());

            if (FirebaseHelper.getInstance().isUserLoggedIn()) {
                syncFutures.add(budgetRepository.sync());
                syncFutures.add(goalRepository.sync());
                syncFutures.add(recurringTransactionRepository.sync());
                syncFutures.add(transactionRepository.sync());
            }

            CompletableFuture.allOf(syncFutures.toArray(new CompletableFuture[0])).join();

            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error in worker sync: " + e.getMessage(), e);
            return Result.failure();
        }
    }

}
