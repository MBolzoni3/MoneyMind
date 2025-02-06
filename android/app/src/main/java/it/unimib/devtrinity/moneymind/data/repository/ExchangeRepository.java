package it.unimib.devtrinity.moneymind.data.repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimib.devtrinity.moneymind.data.remote.ExchangeAPIResponse;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.api.CallAPIService;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class ExchangeRepository {

    final private ExecutorService executorService;
    private static final String BASE_URL = "https://www.ecb.europa.eu/";
    private final HashMap<String, Double> exchangeRates = new HashMap<>();


    public ExchangeRepository(){
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void callAPI(GenericCallback<HashMap<String, Double>> callback) {
        executorService.execute(() -> {
            try {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(SimpleXmlConverterFactory.create())
                        .build();

                CallAPIService service = retrofit.create(CallAPIService.class);

                Call<ExchangeAPIResponse> call = service.getExchangeRates();

                Response<ExchangeAPIResponse> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    ExchangeAPIResponse exchangeRateResponse = response.body();

                    HashMap<String, Double> exchangeRatesHashMap = new HashMap<>();

                    String date = exchangeRateResponse.getCubeContainer().getCube().getTime();

                    for (ExchangeAPIResponse.CurrencyRate rate : exchangeRateResponse.getCubeContainer().getCube().getCurrencyRates()) {
                        exchangeRatesHashMap.put(rate.getCurrency(), rate.getRate());
                    }

                    callback.onSuccess(exchangeRatesHashMap);
                } else {
                    callback.onFailure("Error on API call");
                }
            } catch (IOException e) {
                callback.onFailure("Error on API call");
            }
        });


    }
}
