package it.unimib.devtrinity.moneymind.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "budgets")
public class BudgetEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String firestoreId;
    private double amount;
    private String startDate; // ISO 8601
    private String endDate;   // ISO 8601
    private int categoryId;   // Foreign key to Category
    private boolean isSynced;
    private long lastUpdated;

    public BudgetEntity(int id, String firestoreId, double amount, String startDate, String endDate, int categoryId, boolean isSynced, long lastUpdated) {
        this.id = id;
        this.firestoreId = firestoreId;
        this.amount = amount;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
