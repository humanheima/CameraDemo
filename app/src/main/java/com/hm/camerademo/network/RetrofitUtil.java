package com.hm.camerademo.network;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {

    private static final Retrofit RETROFIT;

    static {

        OkHttpClient.Builder okHttpBuild = new OkHttpClient.Builder();

        okHttpBuild.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                Buffer buffer = new Buffer();
                request.body().writeTo(buffer);
                BufferedSource source = response.body().source();
                source.request(Long.MAX_VALUE);
                Log.d("Retrofit", request.method() + "-->" + request.url());
                Log.d("Retrofit", buffer.readUtf8());
                Log.d("Retrofit", source.buffer().clone().readUtf8());
                return response;
            }
        });

        OkHttpClient client = okHttpBuild.readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        RETROFIT = new Retrofit.Builder()
                .client(client)
                .baseUrl("BASE_URL")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    private RetrofitUtil() {
        throw new AssertionError("No instances");
    }

    public static <T> T create(Class<T> service) {
        return RETROFIT.create(service);
    }
}
