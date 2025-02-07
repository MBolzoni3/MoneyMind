package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.io.IOException;
import java.util.HashMap;

import it.unimib.devtrinity.moneymind.data.remote.ExchangeAPIResponse;
import it.unimib.devtrinity.moneymind.utils.api.ExchangeRateService;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class ExchangeViewModel extends ViewModel {
    private static final String BASE_URL = "https://www.ecb.europa.eu/";
    private final MutableLiveData<HashMap<String, Double>> exchangeRates = new MutableLiveData<>();

    public MutableLiveData<HashMap<String, Double>> callAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        ExchangeRateService service = retrofit.create(ExchangeRateService.class);

        Call<ExchangeAPIResponse> call = service.getExchangeRates();

        HashMap<String, Double> exchangeRatesHashMap = new HashMap<>();

        try {

            Response<ExchangeAPIResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {

                ExchangeAPIResponse exchangeRateResponse = response.body();

                String date = exchangeRateResponse.getCube().getTime();

                for (ExchangeAPIResponse.CurrencyRate rate : exchangeRateResponse.getCube().getCurrencyRates()) {
                    exchangeRatesHashMap.put(rate.getCurrency(), rate.getRate());
                }

            } else {
                exchangeRates.setValue(null);
            }
        } catch (IOException e) {
            exchangeRates.setValue(null);
        }

        exchangeRates.setValue(exchangeRatesHashMap);

        return exchangeRates;
    }
}
