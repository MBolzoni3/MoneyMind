package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.ExchangeEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.RecurringTransactionEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.TransactionEntity;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.data.repository.ExchangeRepository;
import it.unimib.devtrinity.moneymind.data.repository.RecurringTransactionRepository;
import it.unimib.devtrinity.moneymind.data.repository.TransactionRepository;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.Utils;
import it.unimib.devtrinity.moneymind.utils.WorkerHelper;

public class AddTransactionViewModel extends ViewModel {

    private final TransactionRepository transactionRepository;
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final ExchangeRepository exchangeRepository;
    private final CategoryRepository categoryRepository;
    private final MutableLiveData<List<ExchangeEntity>> exchangeRates = new MutableLiveData<>();
    private final MutableLiveData<List<String>> currencies = new MutableLiveData<>();
    private final MutableLiveData<BigDecimal> convertedAmount = new MutableLiveData<>();

    public AddTransactionViewModel(TransactionRepository transactionRepository,
                                   RecurringTransactionRepository recurringTransactionRepository,
                                   CategoryRepository categoryRepository,
                                   ExchangeRepository exchangeRepository) {
        this.transactionRepository = transactionRepository;
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.categoryRepository = categoryRepository;
        this.exchangeRepository = exchangeRepository;
    }

    public LiveData<List<CategoryEntity>> getCategories() {
        return categoryRepository.getAllCategories();
    }

    public LiveData<List<String>> getCurrencies() {
        return currencies;
    }

    public LiveData<BigDecimal> getConvertedAmount() {
        return convertedAmount;
    }

    public void insertTransaction(TransactionEntity transaction, GenericCallback<Void> callback) {
        transactionRepository.insertTransaction(
                transaction,
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

    public void insertRecurringTransaction(RecurringTransactionEntity transaction, Context context, GenericCallback<Void> callback) {
        recurringTransactionRepository.insertTransaction(
                transaction,
                new GenericCallback<>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        WorkerHelper.triggerManualRecurring(context).thenRun(() -> callback.onSuccess(null));
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        callback.onFailure(errorMessage);
                    }
                }
        );
    }

    public void fetchExchangeRates(Date date, GenericCallback<Void> callback) {
        LiveData<List<ExchangeEntity>> liveData = exchangeRepository.getExchangeRates(date);

        liveData.observeForever(rates -> {
            List<String> newCurrencies = new ArrayList<>();
            newCurrencies.add("EUR");

            if (rates != null && !rates.isEmpty()) {
                exchangeRates.setValue(rates);

                for (ExchangeEntity entity : rates) {
                    if (!newCurrencies.contains(entity.currency)) {
                        newCurrencies.add(entity.currency);
                    }
                }

                callback.onSuccess(null);
            } else {
                exchangeRates.setValue(Collections.emptyList());
                callback.onFailure("No exchange rates found for the selected date."); // TODO move this to strings
            }

            currencies.setValue(Utils.getCurrencyDropdownItems(newCurrencies));
        });
    }

    public void calculateConversion(BigDecimal amount, String selectedCurrency) {
        if (amount == null || selectedCurrency == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            convertedAmount.setValue(BigDecimal.ZERO);
            return;
        }

        if (selectedCurrency.equals("EUR")) {
            convertedAmount.setValue(amount);
            return;
        }

        List<ExchangeEntity> rates = exchangeRates.getValue();
        if (rates != null) {
            for (ExchangeEntity entity : rates) {
                if (entity.currency.equals(selectedCurrency)) {
                    BigDecimal rate = entity.rate;
                    if (rate.compareTo(BigDecimal.ZERO) == 0) {
                        convertedAmount.setValue(BigDecimal.ZERO);
                        return;
                    }

                    BigDecimal converted = amount.divide(rate, MathContext.DECIMAL128);
                    convertedAmount.setValue(converted.setScale(4, RoundingMode.HALF_EVEN));
                    return;
                }
            }
        }

        convertedAmount.setValue(amount);
    }

    public BigDecimal reverseConversion(BigDecimal amount, String selectedCurrency){
        if (amount == null || selectedCurrency == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (selectedCurrency.equals("EUR")) {
            return amount;
        }

        List<ExchangeEntity> rates = exchangeRates.getValue();
        if (rates != null) {
            for (ExchangeEntity entity : rates) {
                if (entity.currency.equals(selectedCurrency)) {
                    BigDecimal rate = entity.rate;
                    if (rate.compareTo(BigDecimal.ZERO) == 0) {
                        return BigDecimal.ZERO;
                    }

                    BigDecimal converted = amount.multiply(rate, MathContext.DECIMAL128);
                    return converted.setScale(4, RoundingMode.HALF_EVEN);
                }
            }
        }

        return amount;
    }

}
