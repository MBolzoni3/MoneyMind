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

@Entity(tableName = "goals")
public class GoalEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String firestoreId;
    private String name;
    @Exclude
    private BigDecimal targetAmount;
    @Exclude
    private BigDecimal savedAmount;
    private Date startDate;
    private Date endDate;
    private String categoryId;
    private boolean deleted;
    private boolean synced;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String userId;

    public GoalEntity(int id, String firestoreId, String name, BigDecimal targetAmount, BigDecimal savedAmount, Date startDate, Date endDate, String categoryId, boolean deleted, boolean synced, Timestamp createdAt, Timestamp updatedAt, String userId) {
        this.id = id;
        this.firestoreId = firestoreId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
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
    public GoalEntity(String name, BigDecimal targetAmount, Date startDate, Date endDate, String categoryId, String userId) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.savedAmount = BigDecimal.ZERO;
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
    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    @Exclude
    public void setTargetAmount(BigDecimal amount) {
        this.targetAmount = amount;
    }

    @Exclude
    public BigDecimal getSavedAmount() {
        return savedAmount;
    }

    @Exclude
    public void setSavedAmount(BigDecimal amount) {
        this.savedAmount = amount;
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
    @PropertyName("targetAmount")
    public Long getTargetAmountForFirestore() {
        return Utils.bigDecimalToLong(this.targetAmount);
    }

    @Ignore
    @PropertyName("targetAmount")
    public void setTargetAmountFromFirestore(long amountInCents) {
        this.targetAmount = Utils.longToBigDecimal(amountInCents);
    }

    @Ignore
    @PropertyName("savedAmount")
    public Long getSavedAmountForFirestore() {
        return Utils.bigDecimalToLong(this.savedAmount);
    }

    @Ignore
    @PropertyName("savedAmount")
    public void setSavedAmountFromFirestore(long amountInCents) {
        this.savedAmount = Utils.longToBigDecimal(amountInCents);
    }
}

