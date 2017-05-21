package io.github.yusukeiwaki.imakara.sender;

import android.content.Context;
import android.content.SharedPreferences;

import io.github.yusukeiwaki.imakara.etc.LocationLogCache;
import io.github.yusukeiwaki.imakara.etc.ReactiveSharedPref;

public class LocationCacheObserver extends ReactiveSharedPref<LocationCacheItem> {
    public LocationCacheObserver(Context context) {
        super(LocationLogCache.get(context), new ReactiveSharedPref.ObservationPolicy<LocationCacheItem>() {
            @Override
            public boolean isTargetKey(String key) {
                return LocationLogCache.KEY_LATITUDE.equals(key) ||
                        LocationLogCache.KEY_LONGITUDE.equals(key) ||
                        LocationLogCache.KEY_ACCURACY.equals(key) ||
                        LocationLogCache.KEY_LAST_UPDATED_AT.equals(key);
            }

            @Override
            public LocationCacheItem getValueFromSharedPreference(SharedPreferences prefs) {
                return new LocationCacheItem.Builder()
                        .lat(prefs.getFloat(LocationLogCache.KEY_LATITUDE, 0))
                        .lon(prefs.getFloat(LocationLogCache.KEY_LONGITUDE, 0))
                        .accuracy(prefs.getFloat(LocationLogCache.KEY_ACCURACY, 0))
                        .timestamp(prefs.getLong(LocationLogCache.KEY_LAST_UPDATED_AT, 0))
                        .build();
            }
        });
    }
}
