package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.GoalEntity;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.data.repository.GoalRepository;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;

public class AddGoalViewModel extends ViewModel {

    private final GoalRepository goalRepository;
    private final CategoryRepository categoryRepository;

    public AddGoalViewModel(GoalRepository goalRepository, CategoryRepository categoryRepository) {
        this.goalRepository = goalRepository;
        this.categoryRepository = categoryRepository;
    }

    public LiveData<List<CategoryEntity>> getCategories() {
        return categoryRepository.getAllCategories();
    }

    public void insertGoal(GoalEntity goal, GenericCallback<Void> callback) {
        goalRepository.insertGoal(
                goal,
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