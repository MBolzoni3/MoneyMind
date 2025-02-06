package it.unimib.devtrinity.moneymind.data.repository;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimib.devtrinity.moneymind.constant.Constants;
import it.unimib.devtrinity.moneymind.data.remote.ExchangeAPIResponse;
import it.unimib.devtrinity.moneymind.utils.GenericCallback;
import it.unimib.devtrinity.moneymind.utils.Utils;
import it.unimib.devtrinity.moneymind.utils.api.CallAPIService;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class ExchangeRepository {

    final private ExecutorService executorService;
    private final Map<String, Double> exchangeRates = new HashMap<>();


    public ExchangeRepository(){
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void callAPI(Date date, GenericCallback<Map<String, Double>> callback) {
        executorService.execute(() -> {
            try {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL)
                        .addConverterFactory(SimpleXmlConverterFactory.create())
                        .build();

                CallAPIService service = retrofit.create(CallAPIService.class);

                Call<ExchangeAPIResponse> call = service.getExchangeRates();

                Response<ExchangeAPIResponse> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    ExchangeAPIResponse exchangeRateResponse = response.body();

                    Map<String, Double> exchangeRatesHashMap = exchangeRateResponse.getCubeContainer().toHashMap().get(Utils.dateToString2(date));

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
