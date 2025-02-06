package it.unimib.devtrinity.moneymind.data.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;
import it.unimib.devtrinity.moneymind.constant.RecurrenceTypeEnum;

@Entity(tableName = "recurring_transactions")
public class RecurringTransactionEntity extends TransactionEntity {

    private RecurrenceTypeEnum recurrenceType;
    private int recurrenceInterval;
    private Date recurrenceEndDate;
    private Date lastGeneratedDate;

    @Ignore
    public RecurringTransactionEntity() {
    }

    public RecurringTransactionEntity(int id, String firestoreId, String name, MovementTypeEnum type, BigDecimal amount, String currency, Date date, RecurrenceTypeEnum recurrenceType, int recurrenceInterval, Date recurrenceEndDate, Date lastGeneratedDate, String categoryId, String notes, boolean deleted, boolean synced, Timestamp createdAt, Timestamp updatedAt, String userId) {
        super(id, firestoreId, name, type, amount, currency, date, categoryId, notes, deleted, synced, createdAt, updatedAt, userId);
        this.recurrenceType = recurrenceType;
        this.recurrenceInterval = recurrenceInterval;
        this.recurrenceEndDate = recurrenceEndDate;
        this.lastGeneratedDate = lastGeneratedDate;
    }

    @Ignore
    public RecurringTransactionEntity(String name, MovementTypeEnum type, BigDecimal amount, String currency, Date date, RecurrenceTypeEnum recurrenceType, int recurrenceInterval, Date recurrenceEndDate, Date lastGeneratedDate, String categoryId, String notes, String userId) {
        super(name, type, amount, currency, date, categoryId, notes, userId);
        this.recurrenceType = recurrenceType;
        this.recurrenceInterval = recurrenceInterval;
        this.recurrenceEndDate = recurrenceEndDate;
        this.lastGeneratedDate = lastGeneratedDate;
    }

    @Ignore
    public RecurringTransactionEntity(TransactionEntity transaction, RecurrenceTypeEnum recurrenceType, int recurrenceInterval, Date recurrenceEndDate) {
        super(transaction.getName(), transaction.getType(), transaction.getAmount(), transaction.getCurrency(), transaction.getDate(), transaction.getCategoryId(), transaction.getNotes(), transaction.getUserId());
        this.recurrenceType = recurrenceType;
        this.recurrenceInterval = recurrenceInterval;
        this.recurrenceEndDate = recurrenceEndDate;
        this.lastGeneratedDate = null;
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

    @Ignore
    @Exclude
    public String getFormattedRecurrence() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        StringBuilder description = new StringBuilder();

        String recurrenceUnit;
        switch (recurrenceType) {
            case DAILY:
                recurrenceUnit = recurrenceInterval == 1 ? "giorno" : "giorni";
                break;
            case WEEKLY:
                recurrenceUnit = recurrenceInterval == 1 ? "settimana" : "settimane";
                break;
            case MONTHLY:
                recurrenceUnit = recurrenceInterval == 1 ? "mese" : "mesi";
                break;
            case YEARLY:
                recurrenceUnit = recurrenceInterval == 1 ? "anno" : "anni";
                break;
            default:
                throw new IllegalArgumentException("Tipo di ricorrenza non valido: " + recurrenceType);
        }

        description.append("Ogni ");
        if (recurrenceInterval > 1) {
            description.append(recurrenceInterval).append(" ");
        }
        description.append(recurrenceUnit);

        if (date != null) {
            description.append(" a partire dal ").append(dateFormat.format(date));
        }

        if (recurrenceEndDate != null) {
            description.append(" fino al ").append(dateFormat.format(recurrenceEndDate));
        }

        return description.toString();
    }
}
