package com.modul.marketplace.restful;

import android.os.Build;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.modul.marketplace.app.ApplicationMarketPlace;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final long TIME_OUT = 30;
    private static Retrofit retrofit = null;
    private static Retrofit retrofitForword = null;

    public static OkHttpClient getOkHttpSCM() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        ConnectionPool pool = new ConnectionPool(4, 1, TimeUnit.SECONDS);
        OkHttpClient.Builder builderOk = new OkHttpClient.Builder()

                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .connectionPool(pool)
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
                        .addHeader("access-token", ApplicationMarketPlace.instance.getSCM_ACCESS_TOKEN())
                        .addHeader("secret-key", ApplicationMarketPlace.instance.getSCM_SECRET_KEY())
                        .build()));

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)

                    .build();
            ConnectionSpec nothttps = new ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT)

                    .build();
            ArrayList<ConnectionSpec> list = new ArrayList<>();
            list.add(spec);
            list.add(nothttps);
            builderOk.connectionSpecs(list);
        }


        return builderOk.build();
    }

    public static OkHttpClient getOkHttpHerrmes() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        ConnectionPool pool = new ConnectionPool(4, 1, TimeUnit.SECONDS);
        OkHttpClient.Builder builderOk = new OkHttpClient.Builder()

                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .connectionPool(pool)
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
                        .addHeader("accessToken", ApplicationMarketPlace.instance.getHERMES_ACCESS_TOKEN())
                        .build()));

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)

                    .build();
            ConnectionSpec nothttps = new ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT)

                    .build();
            ArrayList<ConnectionSpec> list = new ArrayList<>();
            list.add(spec);
            list.add(nothttps);
            builderOk.connectionSpecs(list);
        }


        return builderOk.build();
    }


    public static Retrofit getClientSCM() {


        if (retrofit == null) {
            OkHttpClient client = getOkHttpSCM();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApplicationMarketPlace.instance.getSCM_LINK())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

        }
        return retrofit;
    }


    public static Retrofit getClientHermes() {


        if (retrofit == null) {
            OkHttpClient client = getOkHttpHerrmes();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApplicationMarketPlace.instance.getHERMES_LINK())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

        }
        return retrofit;
    }



}
