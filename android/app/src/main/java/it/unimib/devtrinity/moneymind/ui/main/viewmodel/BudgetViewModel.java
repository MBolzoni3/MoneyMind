package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntity;
import it.unimib.devtrinity.moneymind.data.repository.BudgetRepository;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;

public class BudgetViewModel extends ViewModel {
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public BudgetViewModel(BudgetRepository budgetRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    public LiveData<List<BudgetEntity>> getBudgets() {
        return budgetRepository.getAll();
    }

    public void addBudget(BudgetEntity budget) {
        budgetRepository.insertBudget(budget);
    }

    public LiveData<Integer> getProgress(BudgetEntity budget) {
        return Transformations.map(
                transactionRepository.getSpentAmount(
                        budget.getCategoryId(),
                        budget.getStartDate().getTime(),
                        budget.getEndDate().getTime()
                ),
                spentAmount -> {
                    if (spentAmount == null) {
                        return 0;
                    }
                    return (int) ((double) (spentAmount * budget.getAmount()) / 100);
                }
        );
    }
}
