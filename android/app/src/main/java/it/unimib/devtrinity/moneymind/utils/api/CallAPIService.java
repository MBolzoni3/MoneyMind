package it.unimib.devtrinity.moneymind.utils.api;

import it.unimib.devtrinity.moneymind.constant.Constants;
import it.unimib.devtrinity.moneymind.data.remote.ExchangeAPIResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CallAPIService {
    @GET(Constants.SPECIFIC_URL)
    Call<ExchangeAPIResponse> getExchangeRates ();
}
