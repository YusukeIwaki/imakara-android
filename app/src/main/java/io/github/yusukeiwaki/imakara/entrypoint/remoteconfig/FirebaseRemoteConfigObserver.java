package io.github.yusukeiwaki.imakara.entrypoint.remoteconfig;

import android.content.Context;
import android.content.SharedPreferences;

import io.github.yusukeiwaki.imakara.etc.FirebaseRemoteConfigCache;
import io.github.yusukeiwaki.imakara.etc.ReactiveSharedPref;

public class FirebaseRemoteConfigObserver extends ReactiveSharedPref<String> {
    public FirebaseRemoteConfigObserver(Context context) {
        super(FirebaseRemoteConfigCache.get(context), new ObservationPolicy<String>() {
            @Override
            public boolean isTargetKey(String key) {
                return FirebaseRemoteConfigCache.KEY_API_HOSTNAME.equals(key);
            }

            @Override
            public String getValueFromSharedPreference(SharedPreferences prefs) {
                return prefs.getString(FirebaseRemoteConfigCache.KEY_API_HOSTNAME, null);
            }
        });
    }
}
