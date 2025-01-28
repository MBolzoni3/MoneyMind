package it.unimib.devtrinity.moneymind.data.local.entity;

import androidx.room.Ignore;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public abstract class FirestoreEntity extends BaseEntity {

    protected String firestoreId;
    @Exclude
    protected boolean synced;

    public FirestoreEntity() {

    }

    public FirestoreEntity(boolean deleted, Timestamp createdAt, Timestamp updatedAt, String userId, String firestoreId, boolean synced) {
        super(deleted, createdAt, updatedAt, userId);
        this.firestoreId = firestoreId;
        this.synced = synced;
    }

    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    @Ignore
    public long getLastSyncedAt() {
        return System.currentTimeMillis();
    }
}
