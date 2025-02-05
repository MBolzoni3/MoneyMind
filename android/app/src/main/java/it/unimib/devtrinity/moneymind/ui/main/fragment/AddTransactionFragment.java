package it.unimib.devtrinity.moneymind.ui.main.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;
import it.unimib.devtrinity.moneymind.constant.RecurrenceTypeEnum;
import it.unimib.devtrinity.moneymind.data.local.entity.BudgetEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.data.repository.RecurringTransactionRepository;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;
import it.unimib.devtrinity.moneymind.ui.main.adapter.CategoryAdapter;
import it.unimib.devtrinity.moneymind.ui.main.adapter.RecurrenceTypeAdapter;
import it.unimib.devtrinity.moneymind.ui.main.adapter.TransactionTypeAdapter;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddTransactionViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddTransactionViewModelFactory;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.Utils;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class AddTransactionFragment extends Fragment {

    private TransactionEntity currentTransaction;
    private TransactionRepository transactionRepository;
    private RecurringTransactionRepository recurringTransactionRepository;
    private CategoryRepository categoryRepository;
    private AddTransactionViewModel viewModel;

    private TextInputEditText nameField;
    private TextInputEditText amountField;
    private TextInputEditText convertedAmountField;
    private TextInputEditText dateField;
    private MaterialAutoCompleteTextView currencyDropdown;
    private String selectedCurrency;
    private MaterialAutoCompleteTextView typeDropdown;
    private MovementTypeEnum selectedType;
    private MaterialAutoCompleteTextView categoryDropdown;
    private CategoryEntity selectedCategory;
    private TextInputEditText notesField;
    private MaterialCheckBox recurringCheckbox;
    private TextInputLayout recurrenceLayout;
    private MaterialAutoCompleteTextView recurrenceTypeDropdown;
    private RecurrenceTypeAdapter recurrenceTypeAdapter;
    private RecurrenceTypeEnum selectedRecurrence;
    private TextInputEditText recurrenceInterval;
    private TextInputEditText endDateField;

    public void setTransaction(TransactionEntity transaction) {
        this.currentTransaction = transaction;
    }

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
        recurringTransactionRepository = new RecurringTransactionRepository(requireContext());
        categoryRepository = new CategoryRepository(requireContext());
        AddTransactionViewModelFactory factory = new AddTransactionViewModelFactory(categoryRepository);
        viewModel = new ViewModelProvider(this, factory).get(AddTransactionViewModel.class);

        nameField = view.findViewById(R.id.edit_transaction_name);

        amountField = view.findViewById(R.id.edit_transaction_amount);
        amountField.addTextChangedListener(textWatcher);

        convertedAmountField = view.findViewById(R.id.edit_transaction_converted_amount);
        viewModel.getConvertedAmount().observe(getViewLifecycleOwner(), convertedAmount -> {
            convertedAmountField.setEnabled(true);
            convertedAmountField.setText(convertedAmount.toString());
            convertedAmountField.setEnabled(false);
        });

        dateField = view.findViewById(R.id.edit_date);
        dateField.addTextChangedListener(textWatcher);
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
                    if (currencyStr == null) continue;

                    String code = currencyStr.contains(" - ") ? currencyStr.split(" - ")[0] : currencyStr;

                    if (code.equals(currentTransaction.getCurrency())) {
                        currencyDropdown.setText(currencyStr, false);
                        break;
                    }
                }
            }

            if(currencyDropdown.getText().toString().isEmpty()){
                currencyDropdown.setText(adapter.getItem(0), false);
            }

            selectedCurrency = getCurrencyFromDropdownValue();
        });

        currencyDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            triggerConvertedAmount();
            selectedCurrency = getCurrencyFromDropdownValue();
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

        selectedType = currentTransaction == null ? transactionTypeAdapter.getItem(0) : currentTransaction.getType();
        typeDropdown.setText(selectedType == MovementTypeEnum.INCOME ? getString(R.string.income) : getString(R.string.expense), false);

        typeDropdown.performCompletion();

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
        recurrenceTypeAdapter = new RecurrenceTypeAdapter(requireContext(), List.of(RecurrenceTypeEnum.values()));
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
            saveTransaction();
            navigateBack();
        });

        MaterialButton cancelButton = view.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(v -> navigateBack());

        viewModel.fetchCurrencies();
        compileTransaction();
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            triggerConvertedAmount();
        }
    };

    private String getCurrencyFromDropdownValue(){
        String currencyStr = currencyDropdown.getText().toString();
        if(currencyStr.contains(" - ")){
            return currencyStr.split(" - ")[0];
        }

        return currencyStr;
    }

    private void triggerConvertedAmount(){
        viewModel.fetchConvertedAmount(
                Utils.safeParseBigDecimal(amountField.getText().toString(), BigDecimal.ZERO),
                dateField.getText() == null ? null : Utils.stringToDate(dateField.getText().toString()),
                currencyDropdown.getText().toString()
        );
    }

    private void navigateBack() {
        getParentFragmentManager().popBackStack();
    }

    private void compileTransaction(){
        toggleRecurringSection(false);

        if(currentTransaction == null) return;

        nameField.setText(currentTransaction.getName());
        amountField.setText(currentTransaction.getAmount().toString());
        dateField.setText(Utils.dateToString(currentTransaction.getDate()));
        notesField.setText(currentTransaction.getNotes());

        if(currentTransaction instanceof RecurringTransactionEntity){
            RecurringTransactionEntity recurringTransaction = (RecurringTransactionEntity) currentTransaction;
            recurringCheckbox.setChecked(true);

            switch (recurringTransaction.getRecurrenceType()){
                case DAILY:
                    recurrenceTypeDropdown.setText(R.string.daily);
                    selectedRecurrence = RecurrenceTypeEnum.DAILY;
                    break;
                case WEEKLY:
                    recurrenceTypeDropdown.setText(R.string.weekly);
                    selectedRecurrence = RecurrenceTypeEnum.WEEKLY;
                    break;
                case MONTHLY:
                    recurrenceTypeDropdown.setText(R.string.monthly);
                    selectedRecurrence = RecurrenceTypeEnum.MONTHLY;
                    break;
                case YEARLY:
                    recurrenceTypeDropdown.setText(R.string.yearly);
                    selectedRecurrence = RecurrenceTypeEnum.YEARLY;
                    break;
            }

            recurrenceInterval.setText(String.valueOf(recurringTransaction.getRecurrenceInterval()));
            endDateField.setText(Utils.dateToString(recurringTransaction.getRecurrenceEndDate()));
        }
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

    private void saveTransaction(){
        TransactionEntity transaction = new TransactionEntity(
                nameField.getText().toString(),
                selectedType,
                Utils.safeParseBigDecimal(convertedAmountField.getText().toString(), BigDecimal.ZERO),
                selectedCurrency,
                Utils.stringToDate(dateField.getText().toString()),
                selectedCategory.getFirestoreId(),
                notesField.getText().toString(),
                FirebaseHelper.getInstance().getCurrentUser().getUid()
        );

        if(recurringCheckbox.isChecked()){
            RecurringTransactionEntity recurringTransaction = new RecurringTransactionEntity(
                    transaction,
                    selectedRecurrence,
                    Integer.parseInt(recurrenceInterval.getText().toString()),
                    endDateField.getText() == null ? null : Utils.stringToDate(endDateField.getText().toString())
            );
        }

        if(currentTransaction != null){
            currentTransaction.setName(nameField.getText().toString());
            currentTransaction.setAmount(Utils.safeParseBigDecimal(convertedAmountField.getText().toString(), BigDecimal.ZERO));
            currentTransaction.setDate(Utils.stringToDate(dateField.getText().toString()));
            currentTransaction.setCurrency(selectedCurrency);
            currentTransaction.setType(selectedType);
            currentTransaction.setCategoryId(selectedCategory.getFirestoreId());
            currentTransaction.setNotes(notesField.getText().toString());

            if(recurringCheckbox.isChecked()){
                RecurringTransactionEntity currentRecurringTransaction = (RecurringTransactionEntity) currentTransaction;
                currentRecurringTransaction.setRecurrenceType(selectedRecurrence);
                currentRecurringTransaction.setRecurrenceInterval(Integer.parseInt(recurrenceInterval.getText().toString()));
                currentRecurringTransaction.setRecurrenceEndDate(endDateField.getText() == null ? null : Utils.stringToDate(endDateField.getText().toString()));

                currentTransaction = currentRecurringTransaction;
            }

            currentTransaction.setUpdatedAt(Timestamp.now());
            currentTransaction.setSynced(false);

            transaction = currentTransaction;
        }

        if(!recurringCheckbox.isChecked()){
            transactionRepository.insertTransaction(
                    transaction,
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
        } else {
            recurringTransactionRepository.insertTransaction(
                    (RecurringTransactionEntity) transaction,
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

}