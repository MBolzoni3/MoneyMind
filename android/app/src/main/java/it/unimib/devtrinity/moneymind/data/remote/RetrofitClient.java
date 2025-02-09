package it.unimib.devtrinity.moneymind.data.remote;

import android.content.Context;

import it.unimib.devtrinity.moneymind.constant.Constants;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getInstance(Context context) {
        if (retrofit == null) {
            /*int cacheSize = 50 * 1024 * 1024;
            File cacheDir = new File(context.getCacheDir(), "http_cache");
            Cache cache = new Cache(cacheDir, cacheSize);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(new Interceptor() {
                        @NonNull
                        @Override
                        public Response intercept(@NonNull Chain chain) throws IOException {
                            Request request = chain.request();

                            request = request.newBuilder()
                                    .header("Cache-Control", "public, only-if-cached, max-stale=604800")
                                    .build();

                            return chain.proceed(request);
                        }
                    })
                    .addNetworkInterceptor(new Interceptor() {
                        @NonNull
                        @Override
                        public Response intercept(@NonNull Chain chain) throws IOException {
                            Response response = chain.proceed(chain.request());

                            return response.newBuilder()
                                    .header("Cache-Control", "public, max-age=604800")
                                    .build();
                        }
                    })
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .build();*/

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                    .client(new OkHttpClient().newBuilder().build())
                    .build();
        }

        return retrofit;
    }

    public static ExchangeService getService(Context context) {
        return getInstance(context).create(ExchangeService.class);
    }
}
