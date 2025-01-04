package it.unimib.devtrinity.moneymind.ui.main.viewmodel;

import android.util.Pair;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import it.unimib.devtrinity.moneymind.domain.model.ExchangeAPIResponse;
import it.unimib.devtrinity.moneymind.utils.ExchangeRateService;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class ExchangeViewModel extends ViewModel {
    private static final String BASE_URL = "https://www.ecb.europa.eu/";
    private final MutableLiveData<List<Pair<String, Double>>> exchangeRates = new MutableLiveData<>();

    public MutableLiveData<List<Pair<String, Double>>> callAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        ExchangeRateService service = retrofit.create(ExchangeRateService.class);

        Call<ExchangeAPIResponse> call = service.getExchangeRates();

        List<Pair<String, Double>> exchangeRatesList = new ArrayList<>();

        try {

            Response<ExchangeAPIResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {

                ExchangeAPIResponse exchangeRateResponse = response.body();

                String date = exchangeRateResponse.getCube().getTime();
                System.out.println("Date: " + date);

                for (ExchangeAPIResponse.CurrencyRate rate : exchangeRateResponse.getCube().getCurrencyRates()) {
                    exchangeRatesList.add(new Pair<>(rate.getCurrency(), rate.getRate()));
                }

            } else {
                exchangeRates.setValue(null);
            }
        } catch (IOException e) {
            exchangeRates.setValue(null);
        }

        exchangeRates.setValue(exchangeRatesList);

        return exchangeRates;
    }
}
