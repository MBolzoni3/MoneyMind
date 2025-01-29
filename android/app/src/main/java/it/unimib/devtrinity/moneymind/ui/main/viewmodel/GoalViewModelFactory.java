package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.devtrinity.moneymind.data.repository.BudgetRepository;
import it.unimib.devtrinity.moneymind.data.repository.GoalRepository;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;

public class GoalViewModelFactory implements ViewModelProvider.Factory {
    private final GoalRepository goalRepository;

    public GoalViewModelFactory(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GoalViewModel.class)) {
            return (T) new GoalViewModel(goalRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
