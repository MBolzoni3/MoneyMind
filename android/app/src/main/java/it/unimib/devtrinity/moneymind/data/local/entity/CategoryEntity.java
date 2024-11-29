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
    private Timestamp lastUpdated;

    public CategoryEntity(String firestoreId, String name, Timestamp lastUpdated) {
        this.firestoreId = firestoreId;
        this.name = name;
        this.lastUpdated = lastUpdated;
    }

    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

