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

    private final SelectionModeListener selectionModeListener;

    private BudgetEntity currentBudget;
    private BudgetRepository budgetRepository;
    private CategoryRepository categoryRepository;

    private TextInputEditText nameField;
    private TextInputEditText amountField;
    private AutoCompleteTextView categoryDropdown;
    private CategoryEntity selectedCategory;
    private TextInputEditText startDateField;
    private TextInputEditText endDateField;

    TextInputLayout nameFieldLayout;
    TextInputLayout amountFieldLayout;
    TextInputLayout startDateFieldLayout;
    TextInputLayout endDateFieldLayout;

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

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navigateBack();
                    }
                }
        );

        budgetRepository = ServiceLocator.getInstance().getBudgetRepository(requireContext());
        categoryRepository = ServiceLocator.getInstance().getCategoryRepository(requireContext());
        AddBudgetViewModelFactory factory = new AddBudgetViewModelFactory(categoryRepository);
        AddBudgetViewModel viewModel = new ViewModelProvider(this, factory).get(AddBudgetViewModel.class);

        nameFieldLayout = view.findViewById(R.id.input_budget_name);
        amountFieldLayout = view.findViewById(R.id.input_budget_amount);
        startDateFieldLayout = view.findViewById(R.id.input_start_date);
        endDateFieldLayout = view.findViewById(R.id.input_end_date);

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
        if (validateField()) {
            saveBudget();
            navigateBack();
        }
    }

    private boolean validateField() {
        boolean isValid = true;

        if (isEmptyField(nameField, nameFieldLayout, "Il campo relativo al nome del budget non può essere vuoto")) {
            isValid = false;
        }
        if (isEmptyField(amountField, amountFieldLayout, "Il campo relativo all'importo non può essere vuoto")) {
            isValid = false;
        }
        if (selectedCategory == null) {
            Toast.makeText(requireContext(), "Il campo categoria non può essere vuoto", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (isEmptyField(startDateField, startDateFieldLayout, "Il campo relativo alla data di inizio non può essere vuoto")) {
            isValid = false;
        }
        if (isEmptyField(endDateField, endDateFieldLayout, "Il campo relativo alla data di fine non può essere vuoto")) {
            isValid = false;
        }

        return isValid;
    }


    private boolean isEmptyField(TextInputEditText field, TextInputLayout fieldLayout, String fieldError) {

        if (TextUtils.isEmpty(field.getText())) {
            // fieldLayout.setBoxStrokeColor(ContextCompat.getColor(requireContext(), R.color.md_theme_error));
            // fieldLayout.setHintTextColor(ContextCompat.getColorStateList(requireContext(), R.color.md_theme_error));
            fieldLayout.setBoxBackgroundColor(ContextCompat.getColor(requireContext(), R.color.md_theme_errorContainer));
            Toast.makeText(requireContext(), fieldError, Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    private void compileFields() {
        if (currentBudget == null) return;

        nameField.setText(currentBudget.getName());
        amountField.setText(currentBudget.getAmount().toString());
        startDateField.setText(Utils.dateToString(currentBudget.getStartDate()));
        endDateField.setText(Utils.dateToString(currentBudget.getEndDate()));
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

        budgetRepository.insertBudget(
                budget,
                new GenericCallback<>() {

                    @Override
                    public void onSuccess(Boolean result) {
                        navigateBack();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                    }
                }
        );
    }


}

