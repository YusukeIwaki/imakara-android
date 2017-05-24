package io.github.yusukeiwaki.imakara;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.facebook.stetho.Stetho;

import io.github.yusukeiwaki.imakara.etc.OkHttpHelper;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ImakaraApplication extends Application {
    public static class ENV {
        public static String API_HOSTNAME;
        public static String FIREBASE_DYNAMIC_LINK_DOMAIN;
        public static String URL_SHORTENER_API_KEY;

        public static void initializeWith(Context context) {
            Resources res = context.getResources();
            API_HOSTNAME = res.getString(R.string.api_hostname);
            FIREBASE_DYNAMIC_LINK_DOMAIN = res.getString(R.string.firebase_dynamic_link_domain);
            URL_SHORTENER_API_KEY = res.getString(R.string.url_shortener_api_key);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

        ENV.initializeWith(this);
        OkHttpHelper.initializeHttpClient();
        Stetho.initializeWithDefaults(this);

        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build());
    }
}
