package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntityWithCategory;
import it.unimib.devtrinity.moneymind.data.repository.BudgetRepository;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;

public class BudgetViewModel extends ViewModel {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public BudgetViewModel(BudgetRepository budgetRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    public LiveData<List<BudgetEntityWithCategory>> getBudgets() {
        return budgetRepository.getAll();
    }

    public LiveData<Long> getSpentAmount(BudgetEntityWithCategory budget) {
        return transactionRepository.getSpentAmount(
                budget.getBudget().getCategoryId(),
                budget.getBudget().getStartDate().getTime(),
                budget.getBudget().getEndDate().getTime()
        );
    }

    public void deleteBudgets(List<BudgetEntityWithCategory> budgets) {
        budgetRepository.delete(budgets);
    }

}
