package io.github.yusukeiwaki.imakara.entrypoint.remoteconfig;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

import io.github.yusukeiwaki.imakara.BuildConfig;
import io.github.yusukeiwaki.imakara.etc.FirebaseRemoteConfigCache;

public class FirebaseRemoteConfigService extends Service {
    private static final String TAG = FirebaseRemoteConfigService.class.getSimpleName();

    public static void start(Context context) {
        Intent intent = new Intent(context, FirebaseRemoteConfigService.class);
        context.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fetch();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    private void fetch() {
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        if (BuildConfig.DEBUG) {
            remoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(true)
                    .build());
        }

        remoteConfig.setDefaults(new HashMap<String, Object>(){
            {
                put("api_hostname", "localhost");
            }
        });
        remoteConfig.fetch(FirebaseRemoteConfigCache.CACHE_EXPIRATION)
                .addOnSuccessListener(xx -> {
                    remoteConfig.activateFetched();
                    FirebaseRemoteConfigCache.get(getBaseContext()).edit()
                            .putString(FirebaseRemoteConfigCache.KEY_API_HOSTNAME, remoteConfig.getString("api_hostname"))
                            .putLong(FirebaseRemoteConfigCache.KEY_UPDATED_AT, System.currentTimeMillis())
                            .apply();
                })
                .addOnCompleteListener(task -> {
                    stopSelf();
                });
    }
}
