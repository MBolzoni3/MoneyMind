package it.unimib.devtrinity.moneymind.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Date;
import java.util.Map;

import it.unimib.devtrinity.moneymind.data.remote.ExchangeCacheManager;
import it.unimib.devtrinity.moneymind.data.remote.ExchangeDataSource;
import it.unimib.devtrinity.moneymind.data.remote.ExchangeResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExchangeRepository {

    private final ExchangeDataSource exchangeDataSource;
    private final ExchangeCacheManager cacheManager;

    public ExchangeRepository(ExchangeDataSource exchangeDataSource) {
        this.exchangeDataSource = exchangeDataSource;
        this.cacheManager = ExchangeCacheManager.getInstance();
    }

    public LiveData<Map<String, Double>> getExchangeRates(Date date) {
        MutableLiveData<Map<String, Double>> liveData = new MutableLiveData<>();

        if (cacheManager.isCacheAvailable()) {
            liveData.postValue(cacheManager.getExchangeRatesCache(date));
            return liveData;
        }

        exchangeDataSource.fetchExchangeRates(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ExchangeResponse> call, @NonNull Response<ExchangeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cacheManager.updateExchangeRates(response.body());
                    liveData.postValue(cacheManager.getExchangeRatesCache(date));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExchangeResponse> call, @NonNull Throwable t) {
                liveData.postValue(null);
            }
        });

        return liveData;
    }
}
