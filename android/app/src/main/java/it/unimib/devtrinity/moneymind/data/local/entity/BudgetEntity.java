package it.unimib.devtrinity.moneymind.data.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.math.BigDecimal;
import java.util.Date;

import it.unimib.devtrinity.moneymind.utils.Utils;

@Entity(tableName = "budgets")
public class BudgetEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String firestoreId;
    private String name;
    @Exclude
    private BigDecimal amount;
    private Date startDate;
    private Date endDate;
    private String categoryId;
    private boolean deleted;
    private boolean synced;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String userId;

    public BudgetEntity(int id, String firestoreId, String name, BigDecimal amount, Date startDate, Date endDate, String categoryId, boolean deleted, boolean synced, Timestamp createdAt, Timestamp updatedAt, String userId) {
        this.id = id;
        this.firestoreId = firestoreId;
        this.name = name;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categoryId = categoryId;
        this.deleted = deleted;
        this.synced = synced;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
    }

    @Ignore
    public BudgetEntity(String name, BigDecimal amount, Date startDate, Date endDate, String categoryId, String userId) {
        this.name = name;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categoryId = categoryId;
        this.deleted = false;
        this.synced = false;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
        this.userId = userId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public BigDecimal getAmount() {
        return amount;
    }

    @Exclude
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Ignore
    @PropertyName("amount")
    public Long getAmountForFirestore() {
        return Utils.bigDecimalToLong(this.amount);
    }

    @Ignore
    @PropertyName("amount")
    public void setAmountFromFirestore(long amountInCents) {
        this.amount = Utils.longToBigDecimal(amountInCents);
    }

}
