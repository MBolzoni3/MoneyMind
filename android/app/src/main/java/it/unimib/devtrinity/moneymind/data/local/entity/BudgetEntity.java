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
public class BudgetEntity extends FirestoreEntity {

    @Exclude
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    @Exclude
    private BigDecimal amount;
    private Date startDate;
    private Date endDate;
    private String categoryId;

    @Ignore
    public BudgetEntity() {
    }

    public BudgetEntity(int id, String firestoreId, String name, BigDecimal amount, Date startDate, Date endDate, String categoryId, boolean deleted, boolean synced, Timestamp createdAt, Timestamp updatedAt, String userId) {
        super(deleted, createdAt, updatedAt, userId, firestoreId, synced);
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categoryId = categoryId;
    }

    @Ignore
    public BudgetEntity(String name, BigDecimal amount, Date startDate, Date endDate, String categoryId, String userId) {
        super(false, Timestamp.now(), Timestamp.now(), userId, null, false);
        this.name = name;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categoryId = categoryId;
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
