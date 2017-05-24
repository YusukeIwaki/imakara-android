package io.github.yusukeiwaki.imakara.etc;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import io.github.yusukeiwaki.imakara.BuildConfig;

import static io.github.yusukeiwaki.imakara.etc.FirebaseRemoteConfigCache.State.OK;
import static io.github.yusukeiwaki.imakara.etc.FirebaseRemoteConfigCache.State.OLD_CONFIG;

public class FirebaseRemoteConfigCache {
    private static final String PREF_NAME = "firebase_remote_config";
    public static final String KEY_API_HOSTNAME = "api_hostname";
    public static final String KEY_UPDATED_AT = "updated_at";
    public static final long CACHE_EXPIRATION = BuildConfig.DEBUG ? 0 : 3600;

    public static SharedPreferences get(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public enum State {
        NO_CONFIG,
        OLD_CONFIG,
        OK
    }

    public static State getCurrentState(Context context) {
        SharedPreferences prefs = get(context);
        if (TextUtils.isEmpty(prefs.getString(KEY_API_HOSTNAME, null))) return State.NO_CONFIG;

        long updatedAt = prefs.getLong(KEY_UPDATED_AT, 0);
        if (updatedAt < System.currentTimeMillis() - CACHE_EXPIRATION) return OLD_CONFIG;

        return OK;
    }

    public static String getCachedHostname(Context context) {
        return get(context).getString(KEY_API_HOSTNAME, "localhost");
    }
}
