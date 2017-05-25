package io.github.yusukeiwaki.imakara.sender;

import android.content.Context;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import io.github.yusukeiwaki.imakara.etc.LocationLogCache;

public class NotificationUpdater {
    private final Context context;
    private final int notificationId;
    private LocationCacheObserver locationCacheObserver;

    public NotificationUpdater(Context context, int notificationId) {
        this.context = context;
        this.notificationId = notificationId;
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

                updateNotification(locationCacheItem);
            });
        }
    }

    private void updateNotification(LocationCacheItem locationCacheItem) {
        new NotificationBuilder(context).buildLocationNotification(locationCacheItem)
                .onSuccess(task -> {
                    NotificationManagerCompat.from(context)
                            .notify(notificationId, task.getResult());
                    return null;
                });

    }
}
