package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.data.repository.BudgetRepository;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;

public class AddBudgetViewModel extends ViewModel {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;

    public AddBudgetViewModel(BudgetRepository budgetRepository, CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
    }

    public LiveData<List<CategoryEntity>> getCategories() {
        return categoryRepository.getAllCategories();
    }

    public void insertBudget(BudgetEntity budget, GenericCallback<Void> callback) {
        budgetRepository.insertBudget(
                budget,
                new GenericCallback<>() {

                    @Override
                    public void onSuccess(Boolean result) {
                        callback.onSuccess(null);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        callback.onFailure(errorMessage);
                    }
                }
        );
    }

}