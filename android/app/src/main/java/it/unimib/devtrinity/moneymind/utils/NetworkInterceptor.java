package it.unimib.devtrinity.moneymind.utils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.function.Supplier;

import okhttp3.Interceptor;
import okhttp3.Response;

public class NetworkInterceptor implements Interceptor {
    private final Supplier<Boolean> isInternetAvailableSupplier;

    public NetworkInterceptor(Supplier<Boolean> isInternetAvailableSupplier) {
        this.isInternetAvailableSupplier = isInternetAvailableSupplier;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (!isInternetAvailableSupplier.get()) {
            throw new IOException("No Internet Connection");
        }
        return chain.proceed(chain.request());
    }

}


