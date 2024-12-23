package it.unimib.devtrinity.moneymind.data.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;

import java.math.BigDecimal;
import java.util.Date;

import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;
import it.unimib.devtrinity.moneymind.constant.RecurrenceTypeEnum;
import it.unimib.devtrinity.moneymind.utils.Utils;

@Entity(tableName = "recurring_transactions")
public class RecurringTransactionEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String firestoreId;
    private String name;
    private MovementTypeEnum type;
    private Long amount;
    private String currency;
    private Date date;
    private RecurrenceTypeEnum recurrenceType;
    private int recurrenceInterval;
    private Date recurrenceEndDate;
    private Date lastGeneratedDate;
    private int categoryId;
    private String notes;
    private boolean deleted;
    private boolean synced;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String userId;

    public RecurringTransactionEntity(int id, String firestoreId, String name, MovementTypeEnum type, Long amount, String currency, Date date, RecurrenceTypeEnum recurrenceType, int recurrenceInterval, Date recurrenceEndDate, Date lastGeneratedDate, int categoryId, String notes, boolean deleted, boolean synced, Timestamp createdAt, Timestamp updatedAt, String userId) {
        this.id = id;
        this.firestoreId = firestoreId;
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.recurrenceType = recurrenceType;
        this.recurrenceInterval = recurrenceInterval;
        this.recurrenceEndDate = recurrenceEndDate;
        this.lastGeneratedDate = lastGeneratedDate;
        this.categoryId = categoryId;
        this.notes = notes;
        this.deleted = deleted;
        this.synced = synced;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
    }

    @Ignore
    public RecurringTransactionEntity(String name, MovementTypeEnum type, BigDecimal amount, String currency, Date date, RecurrenceTypeEnum recurrenceType, int recurrenceInterval, Date recurrenceEndDate, Date lastGeneratedDate, int categoryId, String notes, String userId) {
        this.name = name;
        this.type = type;
        this.amount = Utils.bigDecimalToLong(amount);
        this.currency = currency;
        this.date = date;
        this.recurrenceType = recurrenceType;
        this.recurrenceInterval = recurrenceInterval;
        this.recurrenceEndDate = recurrenceEndDate;
        this.lastGeneratedDate = lastGeneratedDate;
        this.categoryId = categoryId;
        this.notes = notes;
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

    public MovementTypeEnum getType() {
        return type;
    }

    public void setType(MovementTypeEnum type) {
        this.type = type;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public RecurrenceTypeEnum getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(RecurrenceTypeEnum recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    public int getRecurrenceInterval() {
        return recurrenceInterval;
    }

    public void setRecurrenceInterval(int recurrenceInterval) {
        this.recurrenceInterval = recurrenceInterval;
    }

    public Date getRecurrenceEndDate() {
        return recurrenceEndDate;
    }

    public void setRecurrenceEndDate(Date recurrenceEndDate) {
        this.recurrenceEndDate = recurrenceEndDate;
    }

    public Date getLastGeneratedDate() {
        return lastGeneratedDate;
    }

    public void setLastGeneratedDate(Date lastGeneratedDate) {
        this.lastGeneratedDate = lastGeneratedDate;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
