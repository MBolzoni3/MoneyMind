package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;

public class AddBudgetViewModel extends ViewModel {
    private final LiveData<List<CategoryEntity>> categories;

    public AddBudgetViewModel(CategoryRepository repository) {
        this.categories = repository.getAllCategories();
    }

    public LiveData<List<CategoryEntity>> getCategories() {
        return categories;
    }
}
