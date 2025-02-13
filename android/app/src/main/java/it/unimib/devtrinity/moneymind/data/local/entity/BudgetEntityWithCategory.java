package it.unimib.devtrinity.moneymind.data.local.entity;

import androidx.room.Embedded;

public class BudgetEntityWithCategory {

    @Embedded
    private BudgetEntity budget;

    @Embedded(prefix = "category_")
    private CategoryEntity category;

    public BudgetEntity getBudget() {
        return budget;
    }

    public void setBudget(BudgetEntity budget) {
        this.budget = budget;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }
}
