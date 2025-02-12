package it.unimib.devtrinity.moneymind.data.remote;


import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

import java.util.concurrent.TimeUnit;

import it.unimib.devtrinity.moneymind.constant.Constants;
import it.unimib.devtrinity.moneymind.data.remote.service.ExchangeService;
import it.unimib.devtrinity.moneymind.utils.NetworkInterceptor;
import it.unimib.devtrinity.moneymind.utils.Utils;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getInstance(Application application) {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new NetworkInterceptor(() -> Utils.isInternetAvailable(application.getApplicationContext())))
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public static ExchangeService getService(Application application) {
        return getInstance(application).create(ExchangeService.class);
    }

}
