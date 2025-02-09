package it.unimib.devtrinity.moneymind.data.remote;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Date;

import it.unimib.devtrinity.moneymind.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExchangeDataSource {
    private final ExchangeService apiService;

    public ExchangeDataSource(ExchangeService apiService) {
        this.apiService = apiService;
    }

    public void fetchExchangeRates(Callback<ExchangeResponse> callback) {
        String fakeDate = Utils.dateToStringApi(new Date());

        Call<ExchangeResponse> call = apiService.getExchangeRates(fakeDate);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ExchangeResponse> call, @NonNull Response<ExchangeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResponse(call, response);
                } else {
                    callback.onFailure(call, new IOException("Errore nella risposta API"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExchangeResponse> call, @NonNull Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }
}

