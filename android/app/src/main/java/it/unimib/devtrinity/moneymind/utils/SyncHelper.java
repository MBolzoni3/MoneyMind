package it.unimib.devtrinity.moneymind.utils;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import it.unimib.devtrinity.moneymind.data.sync.SyncWorker;

public class SyncHelper {

    private static final String TAG = SyncHelper.class.getSimpleName();

    private static final long REPEAT_INTERVAL_MIN = 30; //15 minutes is the minimun for WorkManager

    public static void scheduleSyncJob(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(SyncWorker.class, REPEAT_INTERVAL_MIN, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueue(syncRequest);
    }

    public static void triggerManualSync(Context context) {
        OneTimeWorkRequest syncRequest = new OneTimeWorkRequest.Builder(SyncWorker.class).build();
        WorkManager.getInstance(context).enqueue(syncRequest);
    }
}
