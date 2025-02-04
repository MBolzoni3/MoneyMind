package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unimib.devtrinity.moneymind.data.local.entity.CategoryEntity;
import it.unimib.devtrinity.moneymind.data.repository.CategoryRepository;
import it.unimib.devtrinity.moneymind.utils.Utils;

public class AddTransactionViewModel extends ViewModel {
    private final LiveData<List<CategoryEntity>> categories;
    private final MutableLiveData<List<String>> currencies = new MutableLiveData<>();

   public AddTransactionViewModel(CategoryRepository repository) {
       this.categories = repository.getAllCategories();
   }

   public LiveData<List<CategoryEntity>> getCategories() {
      return categories;
   }

   public LiveData<List<String>> getCurrencies() {
       return currencies;
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

}
