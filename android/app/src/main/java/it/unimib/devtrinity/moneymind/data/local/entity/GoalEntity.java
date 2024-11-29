package it.unimib.devtrinity.moneymind.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "goals",
        foreignKeys = @ForeignKey(
                entity = CategoryEntity.class,
                parentColumns = "firestoreId",
                childColumns = "categoryId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "categoryId")}
)
public class GoalEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String firestoreId;
    private double targetAmount;
    private double savedAmount;
    private String startDate; // ISO 8601
    private String endDate;   // ISO 8601
    private int categoryId;   // Foreign key to Category
    private boolean isSynced;
    private long lastUpdated;

    public GoalEntity(int id, String firestoreId, double targetAmount, double savedAmount, String startDate, String endDate, int categoryId, boolean isSynced, long lastUpdated) {
        this.id = id;
        this.firestoreId = firestoreId;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categoryId = categoryId;
        this.isSynced = isSynced;
        this.lastUpdated = lastUpdated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getSavedAmount() {
        return savedAmount;
    }

    public void setSavedAmount(double savedAmount) {
        this.savedAmount = savedAmount;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

