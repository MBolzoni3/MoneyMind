package it.unimib.devtrinity.moneymind.data.remote;


import it.unimib.devtrinity.moneymind.constant.Constants;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public static ExchangeService getService() {
        return getInstance().create(ExchangeService.class);
    }
}
