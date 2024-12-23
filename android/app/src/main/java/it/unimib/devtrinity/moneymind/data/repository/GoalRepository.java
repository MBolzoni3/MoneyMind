package it.unimib.devtrinity.moneymind.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import it.unimib.devtrinity.moneymind.constant.Constants;
import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;
import it.unimib.devtrinity.moneymind.data.local.dao.GoalDao;
import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntity;
import it.unimib.devtrinity.moneymind.utils.SharedPreferencesHelper;
import it.unimib.devtrinity.moneymind.utils.google.FirestoreHelper;

public class GoalRepository extends GenericRepository {
    private static final String TAG = GoalRepository.class.getSimpleName();
    private static final String COLLECTION_NAME = "goals";

    private final GoalDao goalDao;
    private final SharedPreferences sharedPreferences;

    public GoalRepository(Context context) {
        this.goalDao = DatabaseClient.getInstance(context).goalDao();
        this.sharedPreferences = SharedPreferencesHelper.getPreferences(context);
    }

    public void insertGoal(GoalEntity goal) {
        executorService.execute(() -> goalDao.insertOrUpdate(goal));
    }

    public void syncGoals() {
        long lastSyncedTimestamp = sharedPreferences.getLong(Constants.GOALS_LAST_SYNC_KEY, 0);

        syncLocalToRemote();
        syncRemoteToLocal(lastSyncedTimestamp);

        sharedPreferences.edit().putLong(Constants.GOALS_LAST_SYNC_KEY, System.currentTimeMillis()).apply();
    }

    private void syncLocalToRemote() {
        List<GoalEntity> unsyncedGoals = goalDao.getUnsyncedGoals();

        for (GoalEntity goal : unsyncedGoals) {
            String documentId = goal.getFirestoreId();
            DocumentReference docRef;

            if (documentId == null || documentId.isEmpty()) {
                docRef = FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME).document();
                goal.setFirestoreId(docRef.getId());
            } else {
                docRef = FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME).document(documentId);
            }

            docRef.set(goal)
                    .addOnSuccessListener(aVoid -> {
                        goal.setSynced(true);
                        executorService.execute(() -> goalDao.insertOrUpdate(goal));

                        Log.d(TAG, "Goal synced to remote: " + goal.getFirestoreId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error syncing goal to remote: " + e.getMessage(), e);
                    });
        }
    }

    private void syncRemoteToLocal(long lastSyncedTimestamp) {
        FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME)
                .whereGreaterThan("updated_at", lastSyncedTimestamp)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (GoalEntity remoteGoal : querySnapshot.toObjects(GoalEntity.class)) {
                        GoalEntity localGoal = goalDao.getByFirestoreId(remoteGoal.getFirestoreId());

                        if (localGoal == null) {
                            executorService.execute(() -> goalDao.insertOrUpdate(remoteGoal));
                        } else {
                            GoalEntity resolvedGoal = resolveConflict(localGoal, remoteGoal);
                            executorService.execute(() -> goalDao.insertOrUpdate(resolvedGoal));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error syncing goals from remote: " + e.getMessage(), e);
                });
    }

    private GoalEntity resolveConflict(GoalEntity local, GoalEntity remote) {
        return (remote.getUpdatedAt().compareTo(local.getUpdatedAt()) > 0) ? remote : local;
    }

}
