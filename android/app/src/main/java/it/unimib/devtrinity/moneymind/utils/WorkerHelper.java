package it.unimib.devtrinity.moneymind.utils;

import android.content.Context;

import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import it.unimib.devtrinity.moneymind.constant.Constants;
import it.unimib.devtrinity.moneymind.data.worker.RecurringTransactionWorker;
import it.unimib.devtrinity.moneymind.data.worker.SyncWorker;

public class WorkerHelper {

    public static void scheduleSyncJob(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(
                SyncWorker.class,
                Constants.REPEAT_INTERVAL_SYNC_MIN,
                TimeUnit.MINUTES
        ).setConstraints(constraints).build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                Constants.UNIQUE_WORK_SYNC_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
        );
    }

    public static CompletableFuture<WorkInfo> triggerManualSync(Context context) {
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(SyncWorker.class).build();
        WorkManager workManager = WorkManager.getInstance(context);

        workManager.beginUniqueWork(Constants.MANUAL_WORK_SYNC_NAME, ExistingWorkPolicy.REPLACE, request).enqueue();

        CompletableFuture<WorkInfo> completableFuture = new CompletableFuture<>();

        workManager.getWorkInfoByIdLiveData(request.getId()).observeForever(new Observer<>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo != null && workInfo.getState().isFinished()) {
                    completableFuture.complete(workInfo);
                    workManager.getWorkInfoByIdLiveData(request.getId()).removeObserver(this);
                }
            }
        });

        return completableFuture;
    }

    public static void scheduleRecurringJob(Context context) {
        Constraints constraints = new Constraints.Builder()
                .build();

        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(
                RecurringTransactionWorker.class,
                Constants.REPEAT_INTERVAL_RECURRING_HOURS,
                TimeUnit.HOURS
        ).setConstraints(constraints).build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                Constants.UNIQUE_WORK_RECURRING_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
        );
    }

    public static CompletableFuture<WorkInfo> triggerManualRecurring(Context context) {
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(RecurringTransactionWorker.class).build();
        WorkManager workManager = WorkManager.getInstance(context);

        workManager.beginUniqueWork(Constants.MANUAL_WORK_RECURRING_NAME, ExistingWorkPolicy.REPLACE, request).enqueue();

        CompletableFuture<WorkInfo> completableFuture = new CompletableFuture<>();

        workManager.getWorkInfoByIdLiveData(request.getId()).observeForever(new Observer<>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo != null && workInfo.getState().isFinished()) {
                    completableFuture.complete(workInfo);
                    workManager.getWorkInfoByIdLiveData(request.getId()).removeObserver(this);
                }
            }
        });

        return completableFuture;
    }

}
