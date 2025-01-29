package it.unimib.devtrinity.moneymind.utils;

import it.unimib.devtrinity.moneymind.domain.model.ExchangeAPIResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ExchangeRateService {
    @GET("stats/eurofxref/eurofxref-daily.xml")
    Call<ExchangeAPIResponse> getExchangeRates();
}

