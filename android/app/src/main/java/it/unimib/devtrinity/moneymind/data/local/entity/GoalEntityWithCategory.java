package it.unimib.devtrinity.moneymind.data.local.entity;

import androidx.room.Embedded;

public class GoalEntityWithCategory {

    @Embedded
    private GoalEntity goal;

    @Embedded(prefix = "category_")
    private CategoryEntity category;

    public GoalEntity getGoal() {
        return goal;
    }

    public void setGoal(GoalEntity goal) {
        this.goal = goal;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }
}
