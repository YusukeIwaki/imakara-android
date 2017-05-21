package io.github.yusukeiwaki.imakara.sender;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import io.github.yusukeiwaki.imakara.api.ImakaraAPI;
import io.github.yusukeiwaki.imakara.etc.LocationLogCache;
import io.github.yusukeiwaki.imakara.etc.ReactiveSharedPref;

public class LocationUpdater {
    private final Context context;
    private ReactiveSharedPref<LocationCacheItem> locationCacheObserver;
    private ImakaraAPI imakaraAPI;

    public LocationUpdater(Context context) {
        this.context = context;
        imakaraAPI = new ImakaraAPI();
    }

    public void enable() {
        prepareLocationCacheObserver();
        locationCacheObserver.sub();
    }

    public void disable() {
        locationCacheObserver.unsub();
    }

    private void prepareLocationCacheObserver() {
        if (locationCacheObserver == null) {
            locationCacheObserver = new ReactiveSharedPref<>(LocationLogCache.get(context), new ReactiveSharedPref.ObservationPolicy<LocationCacheItem>() {
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
            locationCacheObserver.setOnUpdateListener(locationCacheItem -> {
                String trackingId = LocationLogCache.get(context).getString(LocationLogCache.KEY_TRACKING_ID, null);
                if (TextUtils.isEmpty(trackingId)) return;

                // 20秒以上前の位置情報は通知しない。
                if (locationCacheItem.timestamp() + 20_000 < System.currentTimeMillis()) return;

                imakaraAPI.updateLocationLog(trackingId, locationCacheItem.lat(), locationCacheItem.lon(), locationCacheItem.accuracy());
            });
        }
    }
}
