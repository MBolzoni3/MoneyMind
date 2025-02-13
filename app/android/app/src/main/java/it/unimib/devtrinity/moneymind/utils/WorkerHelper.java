package it.unimib.devtrinity.moneymind.utils;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    public static CompletableFuture<Void> triggerManualSync(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(SyncWorker.class).build();

        try {
            workManager.beginUniqueWork(
                    Constants.MANUAL_WORK_SYNC_NAME,
                    ExistingWorkPolicy.REPLACE,
                    request
            ).enqueue();
        } catch (Exception e) {
            Log.e("Sync Job", e.getMessage(), e);
            return CompletableFuture.completedFuture(null);
        }

        return triggerManualWorker(workManager, request);
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

    public static CompletableFuture<Void> triggerManualRecurring(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(RecurringTransactionWorker.class).build();

        try {
            workManager.beginUniqueWork(
                    Constants.MANUAL_WORK_RECURRING_NAME,
                    ExistingWorkPolicy.REPLACE,
                    request
            ).enqueue();
        } catch (Exception e) {
            Log.e("Recurring Job", e.getMessage(), e);
            return CompletableFuture.completedFuture(null);
        }

        return triggerManualWorker(workManager, request);
    }

    private static CompletableFuture<Void> triggerManualWorker(WorkManager workManager, OneTimeWorkRequest request){
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        LiveData<WorkInfo> liveData = workManager.getWorkInfoByIdLiveData(request.getId());

        try {
            WorkInfo info = workManager.getWorkInfoById(request.getId()).get(500, TimeUnit.MILLISECONDS);
            if (info != null && info.getState().isFinished()) {
                completableFuture.complete(null);
                return completableFuture;
            }
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            Log.e("Recurring Job", e.getMessage(), e);
        }

        Observer<WorkInfo> observer = new Observer<>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo != null && workInfo.getState().isFinished()) {
                    completableFuture.complete(null);
                    liveData.removeObserver(this);
                }
            }
        };

        liveData.observeForever(observer);
        return completableFuture;
    }

}
