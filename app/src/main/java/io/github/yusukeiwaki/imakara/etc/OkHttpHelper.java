package io.github.yusukeiwaki.imakara.etc;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

public class OkHttpHelper {
    private static OkHttpClient sClient;

    public static void initializeHttpClient() {
        sClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
    }

    public static OkHttpClient getHttpClient() {
        if (sClient == null) throw new IllegalStateException("not initialized!");
        return sClient;
    }
}
