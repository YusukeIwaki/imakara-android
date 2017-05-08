package io.github.yusukeiwaki.imakara.etc;

import android.content.Context;
import android.content.SharedPreferences;

public class CurrentUserCache {
    private static final String PREF_NAME = "current_user";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_GCM_TOKEN = "gcm_token";

    public static SharedPreferences get(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
