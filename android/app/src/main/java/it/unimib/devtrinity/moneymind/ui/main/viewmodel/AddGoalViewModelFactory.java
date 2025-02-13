package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.data.repository.GoalRepository;

public class AddGoalViewModelFactory implements ViewModelProvider.Factory {

    private final GoalRepository goalRepository;
    private final CategoryRepository categoryRepository;

    public AddGoalViewModelFactory(GoalRepository goalRepository, CategoryRepository categoryRepository) {
        this.goalRepository = goalRepository;
        this.categoryRepository = categoryRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddGoalViewModel(goalRepository, categoryRepository);
    }

}