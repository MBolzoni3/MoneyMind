package it.unimib.devtrinity.moneymind.data.local.entity;

import androidx.room.Embedded;

public class RecurringTransactionEntityWithCategory {

    @Embedded
    protected RecurringTransactionEntity transaction;

    @Embedded(prefix = "category_")
    private CategoryEntity category;

    public RecurringTransactionEntity getTransaction() {
        return transaction;
    }

    public void setTransaction(RecurringTransactionEntity transaction) {
        this.transaction = transaction;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }
}
