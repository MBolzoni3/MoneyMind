package it.unimib.devtrinity.moneymind.utils;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import it.unimib.devtrinity.moneymind.constant.Constants;
import it.unimib.devtrinity.moneymind.data.sync.SyncWorker;

public class SyncHelper {

    public static void scheduleSyncJob(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(
                SyncWorker.class,
                Constants.REPEAT_INTERVAL_MIN,
                TimeUnit.MINUTES
        ).setConstraints(constraints).build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                Constants.UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
        );
    }

    public static CompletableFuture<Void> triggerManualSync(Context context) {
        return CompletableFuture.supplyAsync(() -> {
            WorkManager workManager = WorkManager.getInstance(context);

            try {
                List<WorkInfo> workInfos = workManager.getWorkInfosForUniqueWork(Constants.UNIQUE_WORK_NAME).get();

                boolean isRunning = workInfos.stream().anyMatch(info ->
                        info.getState() == WorkInfo.State.RUNNING
                );

                if (!isRunning) {
                    OneTimeWorkRequest syncRequest = new OneTimeWorkRequest.Builder(SyncWorker.class)
                            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                            .build();

                    workManager.enqueue(syncRequest);

                    WorkInfo workInfo;
                    do {
                        workInfo = workManager.getWorkInfoById(syncRequest.getId()).get();
                        Thread.sleep(50);
                    } while (workInfo != null && workInfo.getState() == WorkInfo.State.RUNNING);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

            return null;
        });
    }

}
