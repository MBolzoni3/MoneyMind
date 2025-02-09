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
import java.util.Map;

import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.data.repository.ExchangeRepository;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class AddTransactionViewModel extends ViewModel {

    private final ExchangeRepository exchangeRepository;
    private final LiveData<List<CategoryEntity>> categories;
    private final MutableLiveData<Map<String, Double>> exchangeRates = new MutableLiveData<>();
    private final MutableLiveData<List<String>> currencies = new MutableLiveData<>();
    private final MutableLiveData<BigDecimal> convertedAmount = new MutableLiveData<>();

    public AddTransactionViewModel(CategoryRepository categoryRepository, ExchangeRepository exchangeRepository) {
        this.exchangeRepository = exchangeRepository;
        this.categories = categoryRepository.getAllCategories();
    }

    public LiveData<List<CategoryEntity>> getCategories() {
        return categories;
    }

    public void fetchExchangeRates(Date date) {
        LiveData<Map<String, Double>> liveData = exchangeRepository.getExchangeRates(date);

        liveData.observeForever(rates -> {
            if (rates != null) {
                exchangeRates.setValue(rates);
                currencies.setValue(Utils.getCurrencyDropdownItems(new ArrayList<>(rates.keySet())));
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

        Map<String, Double> rates = exchangeRates.getValue();
        if (rates != null && rates.containsKey(selectedCurrency)) {
            BigDecimal rate = BigDecimal.valueOf(rates.get(selectedCurrency));
            if (rate.compareTo(BigDecimal.ZERO) == 0) {
                convertedAmount.setValue(BigDecimal.ZERO);
            }

            BigDecimal converted = amount.divide(rate, MathContext.DECIMAL128);
            convertedAmount.setValue(converted.setScale(4, RoundingMode.HALF_EVEN));
        } else {
            convertedAmount.setValue(amount);
        }
    }

    public LiveData<List<String>> getCurrencies() {
        return currencies;
    }

    public LiveData<BigDecimal> getConvertedAmount() {
        return convertedAmount;
    }

}
