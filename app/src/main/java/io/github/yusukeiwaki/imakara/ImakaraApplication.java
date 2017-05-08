package io.github.yusukeiwaki.imakara;

import android.app.Application;

import com.facebook.stetho.Stetho;

import io.github.yusukeiwaki.imakara.etc.OkHttpHelper;

public class ImakaraApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        OkHttpHelper.initializeHttpClient();
        Stetho.initializeWithDefaults(this);
    }
}
