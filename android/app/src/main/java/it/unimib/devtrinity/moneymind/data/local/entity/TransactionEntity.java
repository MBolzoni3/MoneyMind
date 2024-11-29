package it.unimib.devtrinity.moneymind.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "transactions",
        foreignKeys = @ForeignKey(
                entity = CategoryEntity.class,
                parentColumns = "firestoreId",
                childColumns = "categoryId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "categoryId")}
)
public class TransactionEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String firestoreId;
    private String type; // Income or Expense
    private double amount;
    private String date; // Store as ISO 8601 string
    private int categoryId; // Foreign key to CategoryEntity
    private String description;
    private boolean isSynced;
    private long lastUpdated;

    @Ignore
    public TransactionEntity() {

    }

    public TransactionEntity(int id, String firestoreId, String type, double amount, String date, int categoryId, String description, boolean isSynced, long lastUpdated) {
        this.id = id;
        this.firestoreId = firestoreId;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.categoryId = categoryId;
        this.description = description;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
