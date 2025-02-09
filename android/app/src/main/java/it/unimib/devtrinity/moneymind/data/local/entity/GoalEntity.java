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
public class GoalEntity extends FirestoreEntity {

    @Exclude
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    @Exclude
    private BigDecimal targetAmount;
    @Exclude
    private BigDecimal savedAmount;
    private Date startDate;
    private Date endDate;
    private String categoryId;

    @Ignore
    public GoalEntity() {
    }

    public GoalEntity(boolean deleted, Timestamp createdAt, Timestamp updatedAt, String userId, String firestoreId, boolean synced, int id, String name, BigDecimal targetAmount, BigDecimal savedAmount, Date startDate, Date endDate, String categoryId) {
        super(deleted, createdAt, updatedAt, userId, firestoreId, synced);
        this.id = id;
        this.name = name;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categoryId = categoryId;
    }

    @Ignore
    public GoalEntity(String name, BigDecimal targetAmount, BigDecimal savedAmount, Date startDate, Date endDate, String categoryId, String userId) {
        super(false, Timestamp.now(), Timestamp.now(), userId, null, false);
        this.name = name;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
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

