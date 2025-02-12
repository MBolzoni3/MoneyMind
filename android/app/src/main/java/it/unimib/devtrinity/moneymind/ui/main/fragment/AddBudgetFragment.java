package it.unimib.devtrinity.moneymind.ui.main.fragment;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;

import java.math.BigDecimal;
import java.util.Objects;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.data.repository.BudgetRepository;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.data.repository.ServiceLocator;
import it.unimib.devtrinity.moneymind.ui.SelectionModeListener;
import it.unimib.devtrinity.moneymind.ui.main.adapter.CategoryAdapter;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddBudgetViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddBudgetViewModelFactory;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.Utils;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class AddBudgetFragment extends Fragment {

    private AddBudgetViewModel viewModel;
    private final SelectionModeListener selectionModeListener;

    private BudgetEntity currentBudget;

    private TextInputEditText nameField;
    private TextInputEditText amountField;
    private AutoCompleteTextView categoryDropdown;
    private CategoryEntity selectedCategory;
    private TextInputEditText startDateField;
    private TextInputEditText endDateField;

    private TextInputLayout nameFieldLayout, amountFieldLayout, startDateFieldLayout, endDateFieldLayout, categoryFieldLayout;

    private View thisView;

    public AddBudgetFragment(SelectionModeListener selectionModeListener) {
        this.selectionModeListener = selectionModeListener;
    }

    public void setBudget(BudgetEntity budget) {
        this.currentBudget = budget;
    }

    public BudgetEntity getBudget() {
        return this.currentBudget;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_budget, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        thisView = view;

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navigateBack();
                    }
                }
        );

        BudgetRepository budgetRepository = ServiceLocator.getInstance().getBudgetRepository(requireActivity().getApplication());
        CategoryRepository categoryRepository = ServiceLocator.getInstance().getCategoryRepository(requireActivity().getApplication());
        AddBudgetViewModelFactory factory = new AddBudgetViewModelFactory(budgetRepository, categoryRepository);
        viewModel = new ViewModelProvider(this, factory).get(AddBudgetViewModel.class);

        nameFieldLayout = view.findViewById(R.id.input_budget_name);
        amountFieldLayout = view.findViewById(R.id.input_budget_amount);
        startDateFieldLayout = view.findViewById(R.id.input_start_date);
        endDateFieldLayout = view.findViewById(R.id.input_end_date);
        categoryFieldLayout = view.findViewById(R.id.input_budget_category);

        nameField = view.findViewById(R.id.edit_budget_name);
        amountField = view.findViewById(R.id.edit_budget_amount);

        categoryDropdown = view.findViewById(R.id.edit_budget_category);
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            CategoryAdapter adapter = new CategoryAdapter(requireContext(), categories);
            categoryDropdown.setAdapter(adapter);

            if (currentBudget != null) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    CategoryEntity category = adapter.getItem(i);

                    if (category != null && category.getFirestoreId().equals(currentBudget.getCategoryId())) {
                        selectedCategory = category;
                        categoryDropdown.setText(category.getName(), false);
                        break;
                    }
                }
            }
        });

        categoryDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            selectedCategory = (CategoryEntity) parent.getItemAtPosition(position);
            if (selectedCategory != null) {
                categoryDropdown.setText(selectedCategory.getName(), false);
            }
        });

        startDateField = view.findViewById(R.id.edit_start_date);
        endDateField = view.findViewById(R.id.edit_end_date);

        startDateField.setOnClickListener(v -> {
            Utils.showDatePicker(startDateField::setText, this);
        });

        endDateField.setOnClickListener(v -> {
            Utils.showEndDatePicker(endDateField::setText, this, startDateField);
        });

        compileFields();
    }

    public void onSaveButtonClick() {
        if(validateFields()){
            saveBudget();
            navigateBack();
        }

    }

    private void compileFields() {
        if (currentBudget == null) return;

        nameField.setText(currentBudget.getName());
        amountField.setText(currentBudget.getAmount().toString());
        startDateField.setText(Utils.dateToString(currentBudget.getStartDate()));
        endDateField.setText(Utils.dateToString(currentBudget.getEndDate()));
    }

    private boolean validateFields() {

        if (isEmptyField(nameField, nameFieldLayout, getString(R.string.empty_name_error))) {
            return false;
        } else if (!validateName(nameField, nameFieldLayout)) {
            return false;
        } else {
            nameFieldLayout.setError(null);
            nameFieldLayout.setBoxBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_background));
        }

        if (isEmptyField(amountField, amountFieldLayout, getString(R.string.empty_amount_error))) {
            return false;
        } else {
            amountFieldLayout.setError(null);
            amountFieldLayout.setBoxBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_background));
        }

        if (selectedCategory == null) {
            categoryFieldLayout.setError(getString(R.string.empty_category_error));
            categoryFieldLayout.setBoxBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_errorContainer));
            return false;
        } else {
            categoryFieldLayout.setError(null);
            categoryFieldLayout.setBoxBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_background));
        }

        if (isEmptyField(startDateField, startDateFieldLayout, getString(R.string.empty_startdate_error))) {
            return false;
        } else {
            startDateFieldLayout.setError(null);
            startDateFieldLayout.setBoxBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_background));
        }

        if (isEmptyField(endDateField, endDateFieldLayout, getString(R.string.empty_enddate_error))) {
            return false;
        } else {
            endDateFieldLayout.setError(null);
            endDateFieldLayout.setBoxBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_background));
        }

        return true;
    }

    private boolean validateName(TextInputEditText field, TextInputLayout fieldLayout){
        String name = Objects.requireNonNull(field.getText()).toString();
        if (!name.matches("[a-zA-Z]+")) {
            fieldLayout.setError(getString(R.string.invalid_name_error));
            fieldLayout.setBoxBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_errorContainer));
            return false;
        } else {
            return true;
        }
    }

    private boolean isEmptyField(TextInputEditText field, TextInputLayout fieldLayout, String fieldError) {
        if (TextUtils.isEmpty(field.getText())) {
            fieldLayout.setError(fieldError);
            fieldLayout.setBoxBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_errorContainer));
            return true;
        } else {
            return false;
        }
    }

    private void navigateBack() {
        selectionModeListener.onExitEditMode();
    }

    private void saveBudget() {
        BudgetEntity budget = new BudgetEntity(
                nameField.getText().toString(),
                Utils.safeParseBigDecimal(amountField.getText().toString(), BigDecimal.ZERO),
                Utils.stringToDate(startDateField.getText().toString()),
                Utils.stringToDate(endDateField.getText().toString()),
                selectedCategory.getFirestoreId(),
                FirebaseHelper.getInstance().getCurrentUser().getUid());

        if (currentBudget != null) {
            currentBudget.setName(nameField.getText().toString());
            currentBudget.setAmount(Utils.safeParseBigDecimal(amountField.getText().toString(), BigDecimal.ZERO));
            currentBudget.setStartDate(Utils.stringToDate(startDateField.getText().toString()));
            currentBudget.setEndDate(Utils.stringToDate(endDateField.getText().toString()));
            currentBudget.setCategoryId(selectedCategory.getFirestoreId());
            currentBudget.setUpdatedAt(Timestamp.now());
            currentBudget.setSynced(false);

            budget = currentBudget;
        }

        viewModel.insertBudget(budget, new GenericCallback<>() {
            @Override
            public void onSuccess(Void result) {
                navigateBack();
            }

            @Override
            public void onFailure(String errorMessage) {
                Utils.makeSnackBar(thisView, errorMessage);
            }
        });
    }


}
