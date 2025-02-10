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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

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
import it.unimib.devtrinity.moneymind.utils.Utils;
import it.unimib.devtrinity.moneymind.utils.google.FirebaseHelper;

public class AddTransactionFragment extends Fragment {

    private final SelectionModeListener selectionModeListener;

    private TransactionEntity currentTransaction;
    private TransactionRepository transactionRepository;
    private RecurringTransactionRepository recurringTransactionRepository;
    private CategoryRepository categoryRepository;
    private ExchangeRepository exchangeRepository;
    private AddTransactionViewModel viewModel;

    private TextInputEditText nameField;
    private TextInputEditText amountField;
    private TextInputEditText convertedAmountField;
    private TextInputEditText dateField;
    private Date selectedDate;
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

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navigateBack();
                    }
                });

        transactionRepository = ServiceLocator.getInstance().getTransactionRepository(requireContext());
        recurringTransactionRepository = ServiceLocator.getInstance().getRecurringTransactionRepository(requireContext());
        categoryRepository = ServiceLocator.getInstance().getCategoryRepository(requireContext());
        exchangeRepository = ServiceLocator.getInstance().getExchangeRepository(requireContext());

        AddTransactionViewModelFactory factory = new AddTransactionViewModelFactory(categoryRepository, exchangeRepository);
        viewModel = new ViewModelProvider(this, factory).get(AddTransactionViewModel.class);

        nameField = view.findViewById(R.id.edit_transaction_name);

        amountField = view.findViewById(R.id.edit_transaction_amount);
        amountField.addTextChangedListener(textWatcher);

        convertedAmountField = view.findViewById(R.id.edit_transaction_converted_amount);
        viewModel.getConvertedAmount().observe(getViewLifecycleOwner(), convertedAmount -> {
            convertedAmountField.setEnabled(true);
            convertedAmountField.setText(Utils.formatConvertedAmount(convertedAmount));
            convertedAmountField.setEnabled(false);
        });

        dateField = view.findViewById(R.id.edit_date);
        dateField.addTextChangedListener(textWatcher);
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
                switch (selectedRecurrence) {
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
        endDateField.setOnClickListener(v -> Utils.showDatePicker(endDateField::setText, this));

        compileTransaction();

        selectedDate = Utils.stringToDate(dateField.getText().toString());
        if (selectedDate == null) {
            selectedDate = new Date();
        }

        viewModel.fetchExchangeRates(selectedDate);
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            triggerConvertedAmount();
        }
    };

    public void onSaveButtonClick() {
        saveTransaction();
        navigateBack();
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
    }

    private String getCurrencyFromDropdownValue() {
        String currencyStr = currencyDropdown.getText().toString();
        return currencyStr.contains(" - ") ? currencyStr.split(" - ")[0] : currencyStr;
    }

    private void triggerConvertedAmount() {
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

        String name = currentTransaction.getName();
        if (!isValidName(name)) {
            nameField.setError("Il nome deve contenere solo lettere");
            return;
        }

        BigDecimal amount = currentTransaction.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            amountField.setError("L'importo deve essere maggiore o uguale a zero");
            return;
        }

        Date transactionDate = currentTransaction.getDate();
        if (transactionDate != null && isFutureDate(transactionDate)) {
            dateField.setText(Utils.dateToString(new Date())); // Set to today's date
            dateField.setError("La data non può essere futura");
        } else {
            dateField.setText(Utils.dateToString(transactionDate));
            dateField.setError(null);
        }
        // dateField.setText(Utils.dateToString(currentTransaction.getDate()));

        notesField.setText(currentTransaction.getNotes());

        if (currentTransaction instanceof RecurringTransactionEntity) {
            RecurringTransactionEntity recurringTransaction = (RecurringTransactionEntity) currentTransaction;
            recurringCheckbox.setChecked(true);

            switch (recurringTransaction.getRecurrenceType()) {
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
            transactionRepository.insertTransaction(transaction, new GenericCallback<>() {
                @Override
                public void onSuccess(Boolean result) {
                    navigateBack();
                }

                @Override
                public void onFailure(String errorMessage) {
                }
            });
        } else {
            recurringTransactionRepository.insertTransaction((RecurringTransactionEntity) transaction, new GenericCallback<>() {
                @Override
                public void onSuccess(Boolean result) {
                    navigateBack();
                }

                @Override
                public void onFailure(String errorMessage) {
                }
            });
        }
    }

    private boolean isValidName(String name) {
        // nome è valido solo se composto da lettere maiuscole/minuscole
        Pattern pattern = Pattern.compile("^[a-zA-Z\\s]+$");
        return pattern.matcher(name).matches();
    }

    private boolean isFutureDate(Date date) {
        // Data di oggi
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        // Data della transazione
        Calendar selected = Calendar.getInstance();
        selected.setTime(date);
        selected.set(Calendar.HOUR_OF_DAY, 0);
        selected.set(Calendar.MINUTE, 0);
        selected.set(Calendar.SECOND, 0);
        selected.set(Calendar.MILLISECOND, 0);

        // controllo se la data della transazione è futura
        return selected.after(today);
    }

}