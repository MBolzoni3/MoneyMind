package it.unimib.devtrinity.moneymind.data.local.entity;

import androidx.room.Embedded;

public class TransactionEntityWithCategory {

    @Embedded
    private TransactionEntity transaction;

    @Embedded(prefix = "category_")
    private CategoryEntity category;

    public TransactionEntity getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionEntity transaction) {
        this.transaction = transaction;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }
}
