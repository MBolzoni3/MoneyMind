package it.unimib.devtrinity.moneymind.ui.main.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import it.unimib.devtrinity.moneymind.R;
import it.unimib.devtrinity.moneymind.constant.MovementTypeEnum;
import it.unimib.devtrinity.moneymind.constant.RecurrenceTypeEnum;
import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.data.repository.ExchangeRepository;
import it.unimib.devtrinity.moneymind.data.repository.RecurringTransactionRepository;
import it.unimib.devtrinity.moneymind.data.repository.ServiceLocator;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;
import it.unimib.devtrinity.moneymind.ui.SelectionModeListener;
import it.unimib.devtrinity.moneymind.ui.main.adapter.CategoryAdapter;
import it.unimib.devtrinity.moneymind.ui.main.adapter.RecurrenceTypeAdapter;
import it.unimib.devtrinity.moneymind.ui.main.adapter.TransactionTypeAdapter;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddTransactionViewModel;
import it.unimib.devtrinity.moneymind.ui.main.viewmodel.AddTransactionViewModelFactory;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.ResourceHelper;
import it.unimib.devtrinity.moneymind.utils.TextInputHelper;
import it.unimib.devtrinity.moneymind.utils.Utils;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class AddTransactionFragment extends Fragment {

    private final SelectionModeListener selectionModeListener;

    private TransactionEntity currentTransaction;
    private AddTransactionViewModel viewModel;

    private TextInputEditText nameField, amountField, convertedAmountField, dateField, notesField, recurrenceInterval, endDateField;
    private Date selectedDate;
    private MaterialAutoCompleteTextView currencyDropdown, typeDropdown, categoryDropdown, recurrenceTypeDropdown;
    private String selectedCurrency;
    private MovementTypeEnum selectedType;
    private CategoryEntity selectedCategory;
    private MaterialCheckBox recurringCheckbox;
    private RecurrenceTypeAdapter recurrenceTypeAdapter;
    private RecurrenceTypeEnum selectedRecurrence;
    private TextInputLayout nameFieldLayout, amountFieldLayout, categoryFieldLayout, recurrenceLayout, recurrenceIntervalLayout;

    private View thisView;

    private boolean autoCompile;
    private boolean oneTime;

    public AddTransactionFragment(SelectionModeListener selectionModeListener) {
        this.selectionModeListener = selectionModeListener;
    }

    public void setTransaction(TransactionEntity transaction) {
        this.currentTransaction = transaction;
    }

    public TransactionEntity getTransaction() {
        return this.currentTransaction;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        thisView = view;
        oneTime = true;

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navigateBack();
                    }
                });

        TransactionRepository transactionRepository = ServiceLocator.getInstance().getTransactionRepository(requireActivity().getApplication());
        RecurringTransactionRepository recurringTransactionRepository = ServiceLocator.getInstance().getRecurringTransactionRepository(requireActivity().getApplication());
        CategoryRepository categoryRepository = ServiceLocator.getInstance().getCategoryRepository(requireActivity().getApplication());
        ExchangeRepository exchangeRepository = ServiceLocator.getInstance().getExchangeRepository(requireActivity().getApplication());

        AddTransactionViewModelFactory factory = new AddTransactionViewModelFactory(transactionRepository, recurringTransactionRepository, categoryRepository, exchangeRepository);
        viewModel = new ViewModelProvider(this, factory).get(AddTransactionViewModel.class);

        nameField = view.findViewById(R.id.edit_transaction_name);
        nameFieldLayout = view.findViewById(R.id.input_transaction_name);

        amountField = view.findViewById(R.id.edit_transaction_amount);
        amountField.addTextChangedListener(amountFieldTextWatcher);
        amountFieldLayout = view.findViewById(R.id.input_transaction_amount);

        convertedAmountField = view.findViewById(R.id.edit_transaction_converted_amount);
        viewModel.getConvertedAmount().observe(getViewLifecycleOwner(), convertedAmount -> {
            convertedAmountField.setEnabled(true);
            convertedAmountField.setText(Utils.formatConvertedAmount(convertedAmount));
            convertedAmountField.setEnabled(false);
        });

        dateField = view.findViewById(R.id.edit_date);
        dateField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String dateString = s.toString().trim();
                selectedDate = Utils.stringToDate(dateString);

                if (selectedDate != null) {
                    viewModel.fetchExchangeRates(selectedDate, requireContext(), new GenericCallback<>() {

                        @Override
                        public void onSuccess(Void result) {
                            triggerConvertedAmount();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Utils.makeSnackBar(view.getRootView(), getString(R.string.error_fetching_exchange_rates));
                        }
                    });
                }
            }
        });
        dateField.setOnClickListener(v -> Utils.showNonFutureDatePicker(dateField::setText, this));

        setupCurrencyDropdown(view);
        setupTransactionTypeDropdown(view);
        setupCategoryDropdown(view);

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
                String label = "";
                switch (selectedRecurrence) {
                    case DAILY:
                        label = getString(R.string.daily);
                        break;
                    case WEEKLY:
                        label = getString(R.string.weekly);
                        break;
                    case MONTHLY:
                        label = getString(R.string.monthly);
                        break;
                    case YEARLY:
                        label = getString(R.string.yearly);
                        break;
                }

                recurrenceTypeDropdown.setText(label, false);
            }
        });

        recurrenceInterval = view.findViewById(R.id.edit_recurrence_interval);

        endDateField = view.findViewById(R.id.edit_end_date);
        endDateField.setOnClickListener(v -> Utils.showDatePicker(endDateField::setText, this));

        categoryFieldLayout = view.findViewById(R.id.input_category);
        recurrenceIntervalLayout = view.findViewById(R.id.input_recurrence_interval);

        autoCompile = true;
        compileTransaction();
        autoCompile = false;

        if (dateField.getText().toString().trim().isEmpty()) {
            dateField.setText(Utils.dateToString(new Date()));
        }

        bindInputValidation();
    }

    private final TextWatcher amountFieldTextWatcher = new TextWatcher() {
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

    public void onSaveButtonClick() {
        if (validateFields()) {
            saveTransaction();
            navigateBack();
        }
    }

    private void bindInputValidation() {
        TextInputHelper.addValidationWatcher(nameFieldLayout, nameField, getString(R.string.error_field_required), getString(R.string.invalid_name_error), TextInputHelper.ENTITY_NAME_REGEX);
        TextInputHelper.addValidationWatcher(amountFieldLayout, amountField, getString(R.string.empty_amount_error), null, null);
        TextInputHelper.addValidationWatcher(categoryFieldLayout, categoryDropdown, getString(R.string.empty_category_error), null, null);
        TextInputHelper.addValidationWatcher(recurrenceLayout, recurrenceTypeDropdown, getString(R.string.empty_recurrence_error), null, null);
        TextInputHelper.addValidationWatcher(recurrenceIntervalLayout, recurrenceInterval, getString(R.string.empty_recurrence_interval_error), null, null);
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

        if (recurringCheckbox.isChecked()) {
            if (!TextInputHelper.validateField(recurrenceLayout, selectedRecurrence == null ? "" : "recurrence", getString(R.string.empty_recurrence_error), null, null)) {
                return false;
            }

            if (!TextInputHelper.validateField(recurrenceIntervalLayout, recurrenceInterval, getString(R.string.empty_recurrence_interval_error), null, null)) {
                return false;
            }
        }

        return true;
    }

    private void setupCurrencyDropdown(View view) {
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
            if (currencyDropdown.getText().toString().isEmpty()) {
                currencyDropdown.setText(adapter.getItem(0), false);
            }

            selectedCurrency = getCurrencyFromDropdownValue();
            triggerConvertedAmount();
        });

        currencyDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            selectedCurrency = getCurrencyFromDropdownValue();
            triggerConvertedAmount();
        });
    }

    private void setupTransactionTypeDropdown(View view) {
        typeDropdown = view.findViewById(R.id.edit_transaction_type);
        TransactionTypeAdapter transactionTypeAdapter = new TransactionTypeAdapter(requireContext(), List.of(MovementTypeEnum.values()));
        typeDropdown.setAdapter(transactionTypeAdapter);

        typeDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            selectedType = (MovementTypeEnum) parent.getItemAtPosition(position);
            if (selectedType != null) {
                switch (selectedType) {
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
    }

    private void setupCategoryDropdown(View view) {
        categoryDropdown = view.findViewById(R.id.edit_category);
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            CategoryAdapter adapter = new CategoryAdapter(requireContext(), categories);
            categoryDropdown.setAdapter(adapter);

            if (currentTransaction != null) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    CategoryEntity category = adapter.getItem(i);
                    if (category != null && category.getFirestoreId().equals(currentTransaction.getCategoryId())) {
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
    }

    private String getCurrencyFromDropdownValue() {
        String currencyStr = currencyDropdown.getText().toString();
        return currencyStr.contains(" - ") ? currencyStr.split(" - ")[0] : currencyStr;
    }

    private void triggerConvertedAmount() {
        if (autoCompile) return;
        if (selectedCurrency == null) return;

        if (oneTime && !selectedCurrency.equals("EUR")) {
            oneTime = false;
            BigDecimal convertedEur = viewModel.reverseConversion(
                    Utils.safeParseBigDecimal(convertedAmountField.getText().toString(), BigDecimal.ZERO),
                    selectedCurrency
            );
            amountField.removeTextChangedListener(amountFieldTextWatcher);
            amountField.setText(Utils.formatConvertedAmount(convertedEur));
            amountField.addTextChangedListener(amountFieldTextWatcher);
            return;
        }

        oneTime = false;
        viewModel.calculateConversion(
                Utils.safeParseBigDecimal(amountField.getText().toString(), BigDecimal.ZERO),
                selectedCurrency
        );
    }

    private void navigateBack() {
        selectionModeListener.onExitEditMode();
    }

    private void compileTransaction() {
        toggleRecurringSection(false);

        if (currentTransaction == null) return;

        nameField.setText(currentTransaction.getName());

        if (currentTransaction.getCurrency().equals("EUR")) {
            amountField.setText(currentTransaction.getAmount().toString());
        } else {
            convertedAmountField.setEnabled(true);
            convertedAmountField.setText(currentTransaction.getAmount().toString());
            convertedAmountField.setEnabled(false);
        }

        dateField.setText(Utils.dateToString(currentTransaction.getDate()));
        notesField.setText(currentTransaction.getNotes());

        if (currentTransaction instanceof RecurringTransactionEntity) {
            RecurringTransactionEntity recurringTransaction = (RecurringTransactionEntity) currentTransaction;
            recurringCheckbox.setChecked(true);

            String label = "";
            switch (recurringTransaction.getRecurrenceType()) {
                case DAILY:
                    label = getString(R.string.daily);
                    selectedRecurrence = RecurrenceTypeEnum.DAILY;
                    break;
                case WEEKLY:
                    label = getString(R.string.weekly);
                    selectedRecurrence = RecurrenceTypeEnum.WEEKLY;
                    break;
                case MONTHLY:
                    label = getString(R.string.monthly);
                    selectedRecurrence = RecurrenceTypeEnum.MONTHLY;
                    break;
                case YEARLY:
                    label = getString(R.string.yearly);
                    selectedRecurrence = RecurrenceTypeEnum.YEARLY;
                    break;
            }

            recurrenceTypeDropdown.setText(label, false);
            recurrenceInterval.setText(String.valueOf(recurringTransaction.getRecurrenceInterval()));
            endDateField.setText(Utils.dateToString(recurringTransaction.getRecurrenceEndDate()));
        }
    }

    private void toggleRecurringSection(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        recurrenceLayout.setVisibility(visibility);
        recurrenceTypeDropdown.setVisibility(visibility);
        recurrenceInterval.setVisibility(visibility);
        endDateField.setVisibility(visibility);
    }

    private TransactionEntity buildTransaction() {
        TransactionEntity transaction = new TransactionEntity(
                nameField.getText().toString(),
                selectedType,
                Utils.safeParseBigDecimal(convertedAmountField.getText().toString(), BigDecimal.ZERO),
                selectedCurrency,
                Utils.stringToDate(dateField.getText().toString()),
                selectedCategory.getFirestoreId(),
                notesField.getText().toString().isEmpty() ? null : notesField.getText().toString(),
                FirebaseHelper.getInstance().getCurrentUser().getUid()
        );

        if (recurringCheckbox.isChecked()) {
            int interval = Integer.parseInt(recurrenceInterval.getText().toString());
            Date recurrenceEndDate = Utils.stringToDate(endDateField.getText() != null ? endDateField.getText().toString() : null);
            transaction = new RecurringTransactionEntity(transaction, selectedRecurrence, interval, recurrenceEndDate);
        }

        if (currentTransaction != null) {
            currentTransaction.setName(nameField.getText().toString());
            currentTransaction.setAmount(Utils.safeParseBigDecimal(convertedAmountField.getText().toString(), BigDecimal.ZERO));
            currentTransaction.setDate(Utils.stringToDate(dateField.getText().toString()));
            currentTransaction.setCurrency(selectedCurrency);
            currentTransaction.setType(selectedType);
            currentTransaction.setCategoryId(selectedCategory.getFirestoreId());
            currentTransaction.setNotes(notesField.getText().toString());
            currentTransaction.setUpdatedAt(Timestamp.now());
            currentTransaction.setSynced(false);

            if (recurringCheckbox.isChecked() && currentTransaction instanceof RecurringTransactionEntity) {
                RecurringTransactionEntity recurringTransaction = (RecurringTransactionEntity) currentTransaction;
                recurringTransaction.setRecurrenceType(selectedRecurrence);
                recurringTransaction.setRecurrenceInterval(Integer.parseInt(recurrenceInterval.getText().toString()));
                recurringTransaction.setRecurrenceEndDate(Utils.stringToDate(endDateField.getText() != null ? endDateField.getText().toString() : null));
                transaction = recurringTransaction;
            } else {
                transaction = currentTransaction;
            }
        }

        return transaction;
    }

    private void saveTransaction() {
        TransactionEntity transaction = buildTransaction();

        if (!recurringCheckbox.isChecked()) {
            viewModel.insertTransaction(transaction, new GenericCallback<>() {

                @Override
                public void onSuccess(Void result) {
                    navigateBack();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Utils.makeSnackBar(thisView, errorMessage);
                }
            });
        } else {
            viewModel.insertRecurringTransaction((RecurringTransactionEntity) transaction, requireContext(), new GenericCallback<>() {

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

}