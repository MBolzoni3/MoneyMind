package it.unimib.devtrinity.moneymind.data.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;

import java.util.Date;

@Entity(tableName = "goals")
public class GoalEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String firestoreId;
    private String name;
    private Long targetAmount;
    private Long savedAmount;
    private Date startDate;
    private Date endDate;
    private int categoryId;
    private boolean deleted;
    private boolean synced;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String userId;

    public GoalEntity(int id, String firestoreId, String name, Long targetAmount, Long savedAmount, Date startDate, Date endDate, int categoryId, boolean deleted, boolean synced, Timestamp createdAt, Timestamp updatedAt, String userId) {
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
    public GoalEntity(String name, Long targetAmount, Date startDate, Date endDate, int categoryId, String userId){
        this.name = name;
        this.targetAmount = targetAmount;
        this.savedAmount = 0L;
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

    public Long getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(Long amount) {
        this.targetAmount = amount;
    }

    public Long getSavedAmount() {
        return savedAmount;
    }

    public void setSavedAmount(Long amount) {
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
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
}

