package it.unimib.devtrinity.moneymind.ui.main.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;

import java.math.BigDecimal;
import java.util.Date;

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
import it.unimib.devtrinity.moneymind.utils.ResourceHelper;
import it.unimib.devtrinity.moneymind.utils.TextInputHelper;
import it.unimib.devtrinity.moneymind.utils.Utils;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class AddBudgetFragment extends Fragment {

    private AddBudgetViewModel viewModel;
    private final SelectionModeListener selectionModeListener;

    private BudgetEntity currentBudget;

    private TextInputEditText nameField, amountField, startDateField, endDateField;
    private AutoCompleteTextView categoryDropdown;
    private CategoryEntity selectedCategory;
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
                        categoryDropdown.setText(ResourceHelper.getCategoryName(getContext(), category.getName()), false);
                        break;
                    }
                }
            }
        });

        categoryDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            selectedCategory = (CategoryEntity) parent.getItemAtPosition(position);
            if (selectedCategory != null) {
                categoryDropdown.setText(ResourceHelper.getCategoryName(getContext(), selectedCategory.getName()), false);
            }
        });

        startDateField = view.findViewById(R.id.edit_start_date);
        endDateField = view.findViewById(R.id.edit_end_date);

        startDateField.setOnClickListener(v -> {
            Utils.showDatePicker(startDateField::setText, this);
        });

        endDateField.setOnClickListener(v -> {
            Utils.showEndDatePicker(endDateField::setText, this, startDateField, getString(R.string.select_date));
        });

        compileFields();
        bindInputValidation();
    }

    public void onSaveButtonClick() {
        if (validateFields()) {
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

    private void bindInputValidation() {
        TextInputHelper.addValidationWatcher(nameFieldLayout, nameField, getString(R.string.error_field_required), getString(R.string.invalid_name_error), TextInputHelper.ENTITY_NAME_REGEX);
        TextInputHelper.addValidationWatcher(amountFieldLayout, amountField, getString(R.string.empty_amount_error), null, null);
        TextInputHelper.addValidationWatcher(categoryFieldLayout, categoryDropdown, getString(R.string.empty_category_error), null, null);
        TextInputHelper.addValidationWatcher(startDateFieldLayout, startDateField, getString(R.string.empty_startdate_error), null, null);
        TextInputHelper.addValidationWatcher(endDateFieldLayout, endDateField, getString(R.string.empty_enddate_error), null, null);
    }

    private boolean validateFields() {
        if (!TextInputHelper.validateField(nameFieldLayout, nameField, getString(R.string.error_field_required), getString(R.string.invalid_name_error), TextInputHelper.ENTITY_NAME_REGEX)) {
            return false;
        }

        if (!TextInputHelper.validateField(amountFieldLayout, amountField, getString(R.string.empty_amount_error), null, null)) {
            return false;
        }

        if (!TextInputHelper.validateField(categoryFieldLayout, selectedCategory == null ? "" : "category", getString(R.string.empty_category_error), null, null)) {
            return false;
        }

        if (!TextInputHelper.validateField(startDateFieldLayout, startDateField, getString(R.string.empty_startdate_error), null, null)) {
            return false;
        }

        if (!TextInputHelper.validateField(endDateFieldLayout, endDateField, getString(R.string.empty_enddate_error), null, null)) {
            return false;
        }

        BigDecimal amount = Utils.safeParseBigDecimal(amountField.getText().toString(), BigDecimal.ZERO);
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            TextInputHelper.setError(amountFieldLayout, getString(R.string.not_zero_amount_error));
            return false;
        } else {
            TextInputHelper.clearError(amountFieldLayout);
        }

        Date startDate = Utils.stringToDate(startDateField.getText().toString());
        Date endDate = Utils.stringToDate(endDateField.getText().toString());
        if(startDate.compareTo(endDate) > 0){
            TextInputHelper.setError(startDateFieldLayout, getString(R.string.invalid_start_date));
            return false;
        } else {
            TextInputHelper.clearError(startDateFieldLayout);
        }

        return true;
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
