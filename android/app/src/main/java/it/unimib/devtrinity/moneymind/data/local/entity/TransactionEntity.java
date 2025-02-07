package it.unimib.devtrinity.moneymind.data.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.math.BigDecimal;
import java.util.Date;

import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;
import it.unimib.devtrinity.moneymind.utils.Utils;

@Entity(tableName = "transactions")
public class TransactionEntity extends FirestoreEntity {

    @Exclude
    @PrimaryKey(autoGenerate = true)
    protected int id;
    protected String name;
    protected MovementTypeEnum type;
    protected BigDecimal amount;
    protected String currency;
    protected Date date;
    protected String categoryId;
    protected String notes;

    @Ignore
    public TransactionEntity() {
    }

    public TransactionEntity(int id, String firestoreId, String name, MovementTypeEnum type, BigDecimal amount, String currency, Date date, String categoryId, String notes, boolean deleted, boolean synced, Timestamp createdAt, Timestamp updatedAt, String userId) {
        super(deleted, createdAt, updatedAt, userId, firestoreId, synced);
        this.id = id;
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.categoryId = categoryId;
        this.notes = notes;
    }

    @Ignore
    public TransactionEntity(String name, MovementTypeEnum type, BigDecimal amount, String currency, Date date, String categoryId, String notes, String userId) {
        super(false, Timestamp.now(), Timestamp.now(), userId, null, false);
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.categoryId = categoryId;
        this.notes = notes;
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

    public MovementTypeEnum getType() {
        return type;
    }

    public void setType(MovementTypeEnum type) {
        this.type = type;
    }

    @Exclude
    public BigDecimal getAmount() {
        return amount;
    }

    @Exclude
    public void setAmount(BigDecimal amount) {
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
