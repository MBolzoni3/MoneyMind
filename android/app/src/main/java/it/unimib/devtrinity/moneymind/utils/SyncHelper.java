package it.unimib.devtrinity.moneymind.utils;

import android.content.Context;

import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.concurrent.CompletableFuture;
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

    public static CompletableFuture<WorkInfo> triggerManualSync(Context context) {
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(SyncWorker.class).build();
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.enqueue(request);

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
