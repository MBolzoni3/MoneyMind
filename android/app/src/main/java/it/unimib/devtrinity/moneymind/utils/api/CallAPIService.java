package it.unimib.devtrinity.moneymind.utils.api;

import it.unimib.devtrinity.moneymind.data.remote.ExchangeAPIResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CallAPIService {
    @GET("stats/eurofxref/eurofxref-daily.xml")
    Call<ExchangeAPIResponse> getExchangeRates ();
}
