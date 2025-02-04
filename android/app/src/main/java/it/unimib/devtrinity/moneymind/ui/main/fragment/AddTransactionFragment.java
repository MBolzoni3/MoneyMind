package it.unimib.devtrinity.moneymind.ui.main.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;
import it.unimib.devtrinity.moneymind.constant.RecurrenceTypeEnum;
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;
import it.unimib.devtrinity.moneymind.ui.main.adapter.CategoryAdapter;
import it.unimib.devtrinity.moneymind.ui.main.adapter.RecurrenceTypeAdapter;
import it.unimib.devtrinity.moneymind.ui.main.adapter.TransactionTypeAdapter;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddTransactionViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddTransactionViewModelFactory;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class AddTransactionFragment extends Fragment {

    private TransactionEntity currentTransaction;
    private TransactionRepository transactionRepository;
    private CategoryRepository categoryRepository;

    private TextInputEditText nameField;
    private TextInputEditText amountField;
    private TextInputEditText convertedAmountField;
    private TextInputEditText dateField;
    private MaterialAutoCompleteTextView currencyDropdown;
    private MaterialAutoCompleteTextView typeDropdown;
    private MovementTypeEnum selectedType;
    private MaterialAutoCompleteTextView categoryDropdown;
    private CategoryEntity selectedCategory;
    private TextInputEditText notesField;
    private MaterialCheckBox recurringCheckbox;
    private TextInputLayout recurrenceLayout;
    private MaterialAutoCompleteTextView recurrenceTypeDropdown;
    private RecurrenceTypeEnum selectedRecurrence;
    private TextInputEditText recurrenceInterval;
    private TextInputEditText endDateField;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false);
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

        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(getString(currentTransaction == null ? R.string.add_transaction_title : R.string.edit_transaction_title));

        transactionRepository = new TransactionRepository(requireContext());
        categoryRepository = new CategoryRepository(requireContext());
        AddTransactionViewModelFactory factory = new AddTransactionViewModelFactory(categoryRepository);
        AddTransactionViewModel viewModel = new ViewModelProvider(this, factory).get(AddTransactionViewModel.class);

        nameField = view.findViewById(R.id.edit_transaction_name);
        amountField = view.findViewById(R.id.edit_transaction_amount);
        convertedAmountField = view.findViewById(R.id.edit_transaction_converted_amount);

        dateField = view.findViewById(R.id.edit_date);
        dateField.setOnClickListener(v -> {
            Utils.showDatePicker(dateField::setText, this);
        });

        currencyDropdown = view.findViewById(R.id.edit_transaction_currency);
        viewModel.getCurrencies().observe(getViewLifecycleOwner(), currencies -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.custom_spinner_item, currencies);
            adapter.setDropDownViewResource(R.layout.custom_spinner_item);
            currencyDropdown.setAdapter(adapter);

            if (currentTransaction != null) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    String currencyStr = adapter.getItem(i);
                    if (currencyStr != null) {
                        String code = currencyStr.split(" - ")[0];
                        if (code.equals(currentTransaction.getCurrency())) {
                            currencyDropdown.setText(currencyStr, false);
                            break;
                        }
                    }
                }
            }

            if(currencyDropdown.getText().toString().isEmpty()){
                currencyDropdown.setText(adapter.getItem(0), false);
            }
        });

        typeDropdown = view.findViewById(R.id.edit_transaction_type);
        TransactionTypeAdapter transactionTypeAdapter = new TransactionTypeAdapter(requireContext(), List.of(MovementTypeEnum.values()));
        typeDropdown.setAdapter(transactionTypeAdapter);

        typeDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            selectedType = (MovementTypeEnum) parent.getItemAtPosition(position);
            if (selectedType != null) {
                switch (selectedType){
                    case INCOME:
                        typeDropdown.setText(getString(R.string.income), false);
                        break;
                    case EXPENSE:
                        typeDropdown.setText(getString(R.string.expense), false);
                        break;
                }
            }
        });

        if (transactionTypeAdapter.getCount() > 0) {
            MovementTypeEnum firstItem = transactionTypeAdapter.getItem(0);
            typeDropdown.setText(firstItem == MovementTypeEnum.INCOME ? getString(R.string.income) : getString(R.string.expense), false);

            typeDropdown.post(() -> typeDropdown.performCompletion());
        }

        categoryDropdown = view.findViewById(R.id.edit_category);
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            CategoryAdapter adapter = new CategoryAdapter(requireContext(), categories);
            categoryDropdown.setAdapter(adapter);

            if (currentTransaction != null) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    CategoryEntity category = adapter.getItem(i);

                    if (category != null && category.getFirestoreId().equals(currentTransaction.getCategoryId())) {
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

        notesField = view.findViewById(R.id.edit_transaction_notes);

        recurringCheckbox = view.findViewById(R.id.checkbox_recurring);
        recurringCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> toggleRecurringSection(isChecked));

        recurrenceLayout = view.findViewById(R.id.input_recurrence_type);
        recurrenceTypeDropdown = view.findViewById(R.id.edit_recurrence_type);
        RecurrenceTypeAdapter recurrenceTypeAdapter = new RecurrenceTypeAdapter(requireContext(), List.of(RecurrenceTypeEnum.values()));
        recurrenceTypeDropdown.setAdapter(recurrenceTypeAdapter);

        recurrenceTypeDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            selectedRecurrence = (RecurrenceTypeEnum) parent.getItemAtPosition(position);
            if (selectedRecurrence != null) {
                switch (selectedRecurrence){
                    case DAILY:
                        recurrenceTypeDropdown.setText(R.string.daily);
                        break;
                    case WEEKLY:
                        recurrenceTypeDropdown.setText(R.string.weekly);
                        break;
                    case MONTHLY:
                        recurrenceTypeDropdown.setText(R.string.monthly);
                        break;
                    case YEARLY:
                        recurrenceTypeDropdown.setText(R.string.yearly);
                        break;
                }
            }
        });

        recurrenceInterval = view.findViewById(R.id.edit_recurrence_interval);

        endDateField = view.findViewById(R.id.edit_end_date);
        endDateField.setOnClickListener(v -> {
            Utils.showDatePicker(endDateField::setText, this);
        });

        MaterialButton saveButton = view.findViewById(R.id.button_save);
        saveButton.setOnClickListener(v -> {
            //saveBudget();
            navigateBack();
        });

        MaterialButton cancelButton = view.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(v -> navigateBack());

        viewModel.fetchCurrencies();

        //TODO handle this later
        recurringCheckbox.setChecked(true);
        recurringCheckbox.setChecked(false);
    }

    private void navigateBack() {
        getParentFragmentManager().popBackStack();
    }

    private void toggleRecurringSection(boolean toggle){
        if(toggle){
            recurrenceLayout.setVisibility(View.VISIBLE);
            recurrenceTypeDropdown.setVisibility(View.VISIBLE);
            recurrenceInterval.setVisibility(View.VISIBLE);
            endDateField.setVisibility(View.VISIBLE);
        } else {
            recurrenceLayout.setVisibility(View.GONE);
            recurrenceTypeDropdown.setVisibility(View.GONE);
            recurrenceInterval.setVisibility(View.GONE);
            endDateField.setVisibility(View.GONE);
        }
    }

}