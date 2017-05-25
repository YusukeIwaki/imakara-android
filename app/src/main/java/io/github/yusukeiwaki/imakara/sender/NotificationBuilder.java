package io.github.yusukeiwaki.imakara.sender;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.mypopsy.maps.StaticMap;

import java.util.concurrent.TimeUnit;

import bolts.Task;
import bolts.TaskCompletionSource;
import io.github.yusukeiwaki.imakara.R;
import io.github.yusukeiwaki.imakara.entrypoint.EntryPointActivity;

public class NotificationBuilder {
    private static final int RC_OPEN_APP = 100;
    private static final int RC_POSITIONING_MANUALLY = 13;
    private static final int RC_STOP_SERVICE = 14;

    private static final int TIMEOUT_MS_FOR_GOOGLE_STATIC_MAP = 4500;

    private final Context context;

    public NotificationBuilder(Context context) {
        this.context = context;
    }

    private PendingIntent buildPendingIntentToOpen() {
        return PendingIntent.getActivity(context, RC_OPEN_APP, EntryPointActivity.newIntent(context), 0);
    }

    private PendingIntent buildPendingIntentForUpdateLocationManually() {
        return PendingIntent.getActivity(context, RC_POSITIONING_MANUALLY, PositioningRequirementCheckAndStartPositioningActivity.newIntent(context), 0);
    }

    private PendingIntent buildPendingIntentForStopService() {
        return PendingIntent.getService(context, RC_STOP_SERVICE, SenderCancelService.newIntent(context) ,0);
    }

    private NotificationCompat.Builder buildBaseNotificationBuilder() {
        Resources res = context.getResources();
        return new NotificationCompat.Builder(context)
                .setContentTitle(res.getString(R.string.request_receiver_notification_title))
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setContentIntent(buildPendingIntentToOpen())
                .addAction(R.drawable.ic_vector_black_place, res.getString(R.string.request_receiver_notification_action_update_location_manually), buildPendingIntentForUpdateLocationManually())
                .addAction(R.drawable.ic_vector_black_close, res.getString(R.string.request_receiver_notification_action_stop), buildPendingIntentForStopService());
    }

    public Notification buildBaseNotification() {
        return buildBaseNotificationBuilder().build();
    }

    public Task<Notification> buildLocationNotification(LocationCacheItem locationCacheItem) {
        return getStaticGoogleMapImage(locationCacheItem)
                .onSuccess(task ->
                        new NotificationCompat.BigPictureStyle(buildBaseNotificationBuilder())
                                .bigPicture(task.getResult())
                                .build());
    }

    private Task<Bitmap> getStaticGoogleMapImage(LocationCacheItem locationCacheItem) {
        final TaskCompletionSource<Bitmap> tcs = new TaskCompletionSource<>();

        final String url = new GoogleStaticMap(600, 256,
                locationCacheItem.lat(),
                locationCacheItem.lon(),
                locationCacheItem.accuracy())
                .buildUrl();
        Log.d("hoge", "url="+url);
        new Thread() {
            @Override
            public void run() {
                try {
                    tcs.setResult(Glide.with(context)
                            .asBitmap()
                            .load(url)
                            .submit()
                            .get(TIMEOUT_MS_FOR_GOOGLE_STATIC_MAP, TimeUnit.MILLISECONDS));
                } catch (Exception e) {
                    tcs.setError(e);
                }
            }
        }.start();
        return tcs.getTask();
    }

    private static class GoogleStaticMap {
        private final int width;
        private final int height;
        private final double lat;
        private final double lon;
        private final double accuracy;

        public GoogleStaticMap(int width, int height, double lat, double lon, double accuracy) {
            this.width = width;
            this.height = height;
            this.lat = lat;
            this.lon = lon;
            this.accuracy = accuracy;
        }

        private int calculateZoom() {
            int screenSize = Math.min(width, height);
            double requiredMpp =  10 * accuracy/screenSize;
            int zoomLevel = (int) (1 + Math.log(40075004 / (256 * requiredMpp)) / Math.log(2));

            if (zoomLevel < 1) return 1;
            if (zoomLevel > 20) return 20;
            return zoomLevel;
        }

        private StaticMap.Path circle(int step) {
            return StaticMap.Path.circle(StaticMap.Path.Style.builder().stroke(1).color(0xCC007AFF).fill(0x44007AFF).build(), lat, lon, (int) accuracy, 360/step);
        }

        public String buildUrl() {
            return new StaticMap()
                    .center(lat, lon)
                    .zoom(calculateZoom())
                    .size(width, height)
                    .marker(lat, lon)
                    .path(circle(6))
                    .toString();
        }
    }
}
