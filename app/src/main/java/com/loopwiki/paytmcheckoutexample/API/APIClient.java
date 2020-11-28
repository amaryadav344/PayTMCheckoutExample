package com.loopwiki.paytmcheckoutexample.API;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static Retrofit retrofit = null;
    static APIInterface apiInterface;
    private static final String BASE_URL = "http://192.168.43.195:8080";

    public static Retrofit getClient(String username, String password) {

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new BasicAuthInterceptor(username, password)).build();


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(APIInterface.class);
        return retrofit;
    }

    public static Retrofit getClient() {
        return retrofit;
    }

    public static Retrofit getFreshClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    public static APIInterface getApiInterface() {
        return apiInterface;
    }
}

