package io.github.yusukeiwaki.imakara.etc;

import android.content.Context;
import android.content.SharedPreferences;

public class LocationLogCache {
    private static final String PREF_NAME = "location_log";
    public static final String KEY_TRACKING_ID = "tracking_id";
    public static final String KEY_SHORT_URL = "short_url";
    public static final String KEY_TRACKING_STARTED_AT = "tracking_started_at";
    public static final String KEY_LATITUDE = "lat";
    public static final String KEY_LONGITUDE = "lon";
    public static final String KEY_ACCURACY = "acc";
    public static final String KEY_LAST_UPDATED_AT = "updated_at";

    public static SharedPreferences get(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
