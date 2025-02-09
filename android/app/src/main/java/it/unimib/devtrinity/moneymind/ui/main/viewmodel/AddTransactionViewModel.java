package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.data.local.entity.ExchangeEntity;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.data.repository.ExchangeRepository;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class AddTransactionViewModel extends ViewModel {

    private final ExchangeRepository exchangeRepository;
    private final LiveData<List<CategoryEntity>> categories;
    private final MutableLiveData<List<ExchangeEntity>> exchangeRates = new MutableLiveData<>();
    private final MutableLiveData<List<String>> currencies = new MutableLiveData<>();
    private final MutableLiveData<BigDecimal> convertedAmount = new MutableLiveData<>();

    public AddTransactionViewModel(CategoryRepository categoryRepository, ExchangeRepository exchangeRepository) {
        this.exchangeRepository = exchangeRepository;
        this.categories = categoryRepository.getAllCategories();
    }

    public LiveData<List<CategoryEntity>> getCategories() {
        return categories;
    }

    public void fetchExchangeRates(Date date, GenericCallback<Void> callback) {
        LiveData<List<ExchangeEntity>> liveData = exchangeRepository.getExchangeRates(date);

        liveData.observeForever(rates -> {
            if (rates != null && !rates.isEmpty()) {
                exchangeRates.setValue(rates);

                List<String> newCurrencies = new ArrayList<>();
                newCurrencies.add("EUR");

                for (ExchangeEntity entity : rates) {
                    if (!newCurrencies.contains(entity.currency)) {
                        newCurrencies.add(entity.currency);
                    }
                }

                currencies.setValue(Utils.getCurrencyDropdownItems(newCurrencies));

                callback.onSuccess(null);
            }
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

    public LiveData<List<String>> getCurrencies() {
        return currencies;
    }

    public LiveData<BigDecimal> getConvertedAmount() {
        return convertedAmount;
    }

}
