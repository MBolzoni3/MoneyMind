package it.unimib.devtrinity.moneymind.data.repository;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.unimib.devtrinity.moneymind.constant.Constants;
import it.unimib.devtrinity.moneymind.data.local.DatabaseClient;
import it.unimib.devtrinity.moneymind.data.local.dao.GoalDao;
import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntity;
import it.unimib.devtrinity.moneymind.utils.google.FirestoreHelper;

public class GoalRepository extends GenericRepository {
    private static final String TAG = GoalRepository.class.getSimpleName();
    private static final String COLLECTION_NAME = "goals";

    private final GoalDao goalDao;

    public GoalRepository(Context context) {
        super(context, Constants.GOALS_LAST_SYNC_KEY, TAG);
        this.goalDao = DatabaseClient.getInstance(context).goalDao();
    }

    @Override
    protected CompletableFuture<Void> syncLocalToRemoteAsync() {
        return CompletableFuture.runAsync(() -> {
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
                        .addOnSuccessListener(executorService, aVoid -> {
                            goalDao.setSynced(goal.getId());

                            Log.d(TAG, "Goal synced to remote: " + goal.getFirestoreId());
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error syncing goal to remote: " + e.getMessage(), e);
                        });
            }
        }, executorService);
    }

    @Override
    protected CompletableFuture<Void> syncRemoteToLocalAsync(long lastSyncedTimestamp) {
        return CompletableFuture.runAsync(() -> {
            FirestoreHelper.getInstance().getUserCollection(COLLECTION_NAME)
                    .whereGreaterThan("lastSyncedAt", lastSyncedTimestamp)
                    .get()
                    .addOnSuccessListener(executorService, querySnapshot -> {
                        for (GoalEntity remoteGoal : querySnapshot.toObjects(GoalEntity.class)) {
                            GoalEntity localGoal = goalDao.getByFirestoreId(remoteGoal.getFirestoreId());

                            if (localGoal == null) {
                                goalDao.insertOrUpdate(remoteGoal);
                            } else {
                                GoalEntity resolvedGoal = resolveConflict(localGoal, remoteGoal);
                                goalDao.insertOrUpdate(resolvedGoal);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error syncing goals from remote: " + e.getMessage(), e);
                    });
        }, executorService);
    }

    private GoalEntity resolveConflict(GoalEntity local, GoalEntity remote) {
        return (remote.getUpdatedAt().compareTo(local.getUpdatedAt()) > 0) ? remote : local;
    }

}
