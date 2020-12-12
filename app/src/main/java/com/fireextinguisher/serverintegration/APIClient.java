package com.fireextinguisher.serverintegration;

import com.fireextinguisher.utils.Constant;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static Retrofit retrofit = null;

    public Retrofit getClient() {
        if (retrofit == null) {
            try {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.level(HttpLoggingInterceptor.Level.BODY);

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(1, TimeUnit.MINUTES)
                        .readTimeout(1, TimeUnit.MINUTES)
                        .writeTimeout(1, TimeUnit.MINUTES)
                        .addInterceptor(interceptor)
                        .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT))
                        .followSslRedirects(true)
                        .retryOnConnectionFailure(true)
                        .cache(null).build();

                retrofit = new Retrofit.Builder().baseUrl(Constant.SERVER_URL).client(client)
                        .addConverterFactory(GsonConverterFactory.create()).build();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return retrofit;
    }
}