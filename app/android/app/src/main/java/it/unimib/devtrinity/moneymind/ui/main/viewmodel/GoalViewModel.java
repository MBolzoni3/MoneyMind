package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntityWithCategory;
import it.unimib.devtrinity.moneymind.data.repository.GoalRepository;

public class GoalViewModel extends ViewModel {
    private final GoalRepository goalRepository;

    public GoalViewModel(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public LiveData<List<GoalEntityWithCategory>> getGoals() {
        return goalRepository.getAll();
    }

    public void deleteGoals(List<GoalEntityWithCategory> goals) {
        goalRepository.delete(goals);
    }
}
