package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.data.repository.BudgetRepository;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;

public class BudgetViewModelFactory implements ViewModelProvider.Factory {
   private final BudgetRepository budgetRepository;
   private final TransactionRepository transactionRepository;

   public BudgetViewModelFactory(BudgetRepository budgetRepository, TransactionRepository transactionRepository) {
      this.budgetRepository = budgetRepository;
      this.transactionRepository = transactionRepository;
   }

   @NonNull
   @Override
   public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
      if (modelClass.isAssignableFrom(BudgetViewModel.class)) {
         return (T) new BudgetViewModel(budgetRepository, transactionRepository);
      }
      throw new IllegalArgumentException("Unknown ViewModel class");
   }
}
