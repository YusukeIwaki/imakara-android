package io.github.yusukeiwaki.imakara.sender;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import io.github.yusukeiwaki.imakara.api.ImakaraAPI;
import io.github.yusukeiwaki.imakara.etc.LocationLogCache;
import io.github.yusukeiwaki.imakara.etc.ReactiveSharedPref;

public class ShortUrlObserver extends ReactiveSharedPref<String> {
    public ShortUrlObserver(Context context) {
        super(LocationLogCache.get(context), new ObservationPolicy<String>() {
            @Override
            public boolean isTargetKey(String key) {
                return LocationLogCache.KEY_TRACKING_ID.equals(key) ||
                        LocationLogCache.KEY_SHORT_URL.equals(key);
            }

            @Override
            public String getValueFromSharedPreference(SharedPreferences prefs) {
                String shortUrl = prefs.getString(LocationLogCache.KEY_SHORT_URL, null);
                if (!TextUtils.isEmpty(shortUrl)) return shortUrl;

                String trackingId = prefs.getString(LocationLogCache.KEY_TRACKING_ID, null);
                if (!TextUtils.isEmpty(trackingId)) return new ImakaraAPI().getTrackingURLForShare(trackingId);

                return null;
            }
        });
    }
}
