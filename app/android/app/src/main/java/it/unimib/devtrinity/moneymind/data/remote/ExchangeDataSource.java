package it.unimib.devtrinity.moneymind.data.remote;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Date;

import it.unimib.devtrinity.moneymind.data.remote.response.ExchangeResponse;
import it.unimib.devtrinity.moneymind.data.remote.service.ExchangeService;
import it.unimib.devtrinity.moneymind.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExchangeDataSource {
    private final ExchangeService apiService;

    public ExchangeDataSource(ExchangeService apiService) {
        this.apiService = apiService;
    }

    public void fetchExchangeRates(Date date, Callback<ExchangeResponse> callback) {
        if (date == null) {
            date = new Date();
        }

        String startDate = Utils.dateToStringApi(Utils.getDateNDaysAgo(date, 5));
        String endDate = Utils.dateToStringApi(date);

        Call<ExchangeResponse> call = apiService.getExchangeRates(startDate, endDate, "jsondata");
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

    public ExchangeResponse fetchExchangeRatesSync(Date date) throws IOException {
        if (date == null) {
            date = new Date();
        }

        String startDate = Utils.dateToStringApi(Utils.getDateNDaysAgo(date, 5));
        String endDate = Utils.dateToStringApi(date);

        Call<ExchangeResponse> call = apiService.getExchangeRates(startDate, endDate, "jsondata");
        Response<ExchangeResponse> response = call.execute();

        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            throw new IOException("Errore nella risposta API");
        }
    }

}

