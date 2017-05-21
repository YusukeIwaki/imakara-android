package io.github.yusukeiwaki.imakara.sender;

import android.content.Context;
import android.text.TextUtils;

import io.github.yusukeiwaki.imakara.api.ImakaraAPI;
import io.github.yusukeiwaki.imakara.etc.LocationLogCache;

public class LocationUpdater {
    private final Context context;
    private LocationCacheObserver locationCacheObserver;
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
            locationCacheObserver = new LocationCacheObserver(context);
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
