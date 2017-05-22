package io.github.yusukeiwaki.imakara;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.facebook.stetho.Stetho;

import io.github.yusukeiwaki.imakara.etc.OkHttpHelper;

public class ImakaraApplication extends Application {
    public static class ENV {
        public static String API_HOSTNAME;
        public static String URL_SHORTENER_API_KEY;

        public static void initializeWith(Context context) {
            Resources res = context.getResources();
            API_HOSTNAME = res.getString(R.string.api_hostname);
            URL_SHORTENER_API_KEY = res.getString(R.string.url_shortener_api_key);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

        ENV.initializeWith(this);
        OkHttpHelper.initializeHttpClient();
        Stetho.initializeWithDefaults(this);
    }
}
