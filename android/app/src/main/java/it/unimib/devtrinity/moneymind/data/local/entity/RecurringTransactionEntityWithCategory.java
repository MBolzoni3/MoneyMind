package it.unimib.devtrinity.moneymind.data.local.entity;

import androidx.room.Embedded;

public class RecurringTransactionEntityWithCategory {

    @Embedded
    private RecurringTransactionEntity recurringTransaction;

    @Embedded(prefix = "category_")
    private CategoryEntity category;

    public RecurringTransactionEntity getRecurringTransaction() {
        return recurringTransaction;
    }

    public void setRecurringTransaction(RecurringTransactionEntity recurringTransaction) {
        this.recurringTransaction = recurringTransaction;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }
}
