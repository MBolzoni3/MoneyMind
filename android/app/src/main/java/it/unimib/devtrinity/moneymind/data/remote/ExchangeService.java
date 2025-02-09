package it.unimib.devtrinity.moneymind.data.remote;

import it.unimib.devtrinity.moneymind.constant.Constants;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ExchangeService {
    @GET(Constants.SPECIFIC_URL)
    Call<ExchangeResponse> getExchangeRates(
            @Query("startPeriod") String startDate,
            @Query("endPeriod") String endDate,
            @Query("format") String format
    );
}
