package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class AddTransactionViewModel extends ViewModel {
    private final LiveData<List<CategoryEntity>> categories;
    private final MutableLiveData<List<String>> currencies = new MutableLiveData<>();
    private final MutableLiveData<BigDecimal> convertedAmount = new MutableLiveData<>();

    public AddTransactionViewModel(CategoryRepository repository) {
        this.categories = repository.getAllCategories();
    }

    public LiveData<List<CategoryEntity>> getCategories() {
        return categories;
    }

    public LiveData<List<String>> getCurrencies() {
        return currencies;
    }

    public LiveData<BigDecimal> getConvertedAmount() {
        return convertedAmount;
    }

    public void fetchCurrencies() {
        //TODO get currencies list from API
        final List<String> CURRENCIES = Arrays.asList(
                "EUR", "AUD", "BGN", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK",
                "GBP", "HKD", "HRK", "HUF", "IDR", "ILS", "INR", "ISK", "JPY",
                "KRW", "MXN", "MYR", "NOK", "NZD", "PHP", "PLN", "RON", "RUB",
                "SEK", "SGD", "THB", "TRY", "USD", "ZAR"
        );

        currencies.setValue(Utils.getCurrencyDropdownItems(CURRENCIES));
    }

    public void fetchConvertedAmount(BigDecimal amount, Date date, String currency) {
        if(date == null || currency == null) {
            return;
        }

        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            convertedAmount.setValue(amount);
            return;
        }

        if (currency.equals("EUR")) {
            convertedAmount.setValue(amount);
            return;
        }

        //TODO get converted amount from API
        convertedAmount.setValue(amount);
    }

}
