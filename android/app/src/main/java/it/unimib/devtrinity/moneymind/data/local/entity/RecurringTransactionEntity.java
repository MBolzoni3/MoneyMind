package it.unimib.devtrinity.moneymind.data.local.entity;

import android.content.Context;
import android.content.res.Resources;

import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.constant.Constants;
import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;
import it.unimib.devtrinity.moneymind.constant.RecurrenceTypeEnum;

@Entity(tableName = Constants.RECURRING_TRANSACTIONS_TABLE_NAME)
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
    public String getFormattedRecurrence(Context context) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        StringBuilder description = new StringBuilder();
        Resources res = context.getResources();

        int quantity = recurrenceInterval;
        int pluralId;

        switch (recurrenceType) {
            case DAILY:
                pluralId = R.plurals.recurrence_day;
                break;
            case WEEKLY:
                pluralId = R.plurals.recurrence_week;
                break;
            case MONTHLY:
                pluralId = R.plurals.recurrence_month;
                break;
            case YEARLY:
                pluralId = R.plurals.recurrence_year;
                break;
            default:
                throw new IllegalArgumentException("Tipo di ricorrenza non valido: " + recurrenceType);
        }

        String recurrenceUnit = res.getQuantityString(pluralId, quantity, quantity);

        description.append(res.getString(R.string.recurrence_every)).append(" ").append(recurrenceUnit);

        if (date != null) {
            description.append(" ").append(res.getString(R.string.recurrence_from)).append(" ").append(dateFormat.format(date));
        }

        if (recurrenceEndDate != null) {
            description.append(" ").append(res.getString(R.string.recurrence_until)).append(" ").append(dateFormat.format(recurrenceEndDate));
        }

        return description.toString();
    }

}
