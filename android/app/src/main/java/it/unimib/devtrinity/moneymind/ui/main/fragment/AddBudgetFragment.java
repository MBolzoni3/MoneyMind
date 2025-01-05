package it.unimib.devtrinity.moneymind.ui.main.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.data.repository.BudgetRepository;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.ui.main.adapter.CategoryAdapter;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddBudgetViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddBudgetViewModelFactory;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.Utils;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class AddBudgetFragment extends Fragment {

    private BudgetRepository budgetRepository;
    private CategoryRepository categoryRepository;

    private TextInputEditText nameField;
    private TextInputEditText amountField;
    private AutoCompleteTextView categoryDropdown;
    private CategoryEntity selectedCategory;
    private TextInputEditText startDateField;
    private TextInputEditText endDateField;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_budget_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        budgetRepository = new BudgetRepository(requireContext());
        categoryRepository = new CategoryRepository(requireContext());
        AddBudgetViewModelFactory factory = new AddBudgetViewModelFactory(categoryRepository);
        AddBudgetViewModel viewModel = new ViewModelProvider(this, factory).get(AddBudgetViewModel.class);

        nameField = view.findViewById(R.id.edit_budget_name);
        amountField = view.findViewById(R.id.edit_budget_amount);

        categoryDropdown = view.findViewById(R.id.edit_budget_category);
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            CategoryAdapter adapter = new CategoryAdapter(requireContext(), categories);
            categoryDropdown.setAdapter(adapter);
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
            showDatePicker(startDateField::setText);
        });

        endDateField.setOnClickListener(v -> {
            showDatePicker(endDateField::setText);
        });

        MaterialButton saveButton = view.findViewById(R.id.button_save_budget);
        MaterialButton cancelButton = view.findViewById(R.id.button_cancel);

        saveButton.setOnClickListener(v -> {
            saveBudget();
            navigateBack();
        });

        cancelButton.setOnClickListener(v -> navigateBack());
    }

    private void navigateBack() {
        getParentFragmentManager().popBackStack();
    }

    private void saveBudget() {
        BudgetEntity budget = new BudgetEntity(
                nameField.getText().toString(),
                new BigDecimal(amountField.getText().toString()),
                Utils.stringToDate(startDateField.getText().toString()),
                Utils.stringToDate(endDateField.getText().toString()),
                selectedCategory.getFirestoreId(),
                FirebaseHelper.getInstance().getCurrentUser().getUid());

        budgetRepository.insertBudget(
                budget,
                new GenericCallback<>() {

                    @Override
                    public void onSuccess(Boolean result) {
                        navigateBack();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        //Show toast
                    }
                }
        );
    }

    private void showDatePicker(OnDateSelectedListener listener) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleziona una data")
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date(selection));
            listener.onDateSelected(formattedDate);
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    @FunctionalInterface
    interface OnDateSelectedListener {
        void onDateSelected(String date);
    }
}

