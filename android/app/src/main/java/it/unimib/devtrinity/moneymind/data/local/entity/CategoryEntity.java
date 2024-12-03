package it.unimib.devtrinity.moneymind.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;

@Entity(tableName = "categories")
public class CategoryEntity {
    @PrimaryKey
    @NonNull
    private String firestoreId;
    private String name;
    private boolean deleted;
    private Timestamp lastUpdated;

    public CategoryEntity(@NonNull String firestoreId, String name, boolean deleted, Timestamp lastUpdated) {
        this.firestoreId = firestoreId;
        this.name = name;
        this.deleted = deleted;
        this.lastUpdated = lastUpdated;
    }

    @NonNull
    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(@NonNull String firestoreId) {
        this.firestoreId = firestoreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

