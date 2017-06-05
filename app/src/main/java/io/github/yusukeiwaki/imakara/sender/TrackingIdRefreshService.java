package io.github.yusukeiwaki.imakara.sender;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import bolts.Task;
import io.github.yusukeiwaki.imakara.api.GoogleAPI;
import io.github.yusukeiwaki.imakara.api.ImakaraAPI;
import io.github.yusukeiwaki.imakara.etc.CurrentUserCache;
import io.github.yusukeiwaki.imakara.etc.LocationLogCache;

public class TrackingIdRefreshService extends IntentService {
    private static final String TAG = TrackingIdRefreshService.class.getSimpleName();
    private static final int API_TIMEOUT_MS = 4500;
    private final ImakaraAPI imakaraAPI;
    private final GoogleAPI googleAPI;

    public static void start(Context context) {
        Intent intent = new Intent(context, TrackingIdRefreshService.class);
        context.startService(intent);
    }

    public TrackingIdRefreshService() {
        super(TAG);
        imakaraAPI = new ImakaraAPI();
        googleAPI = new GoogleAPI();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SharedPreferences prefs = CurrentUserCache.get(this);
        String username = prefs.getString(CurrentUserCache.KEY_USERNAME, null);
        String gcmToken = prefs.getString(CurrentUserCache.KEY_GCM_TOKEN, null);
        String trackingId = LocationLogCache.get(this).getString(LocationLogCache.KEY_TRACKING_ID, null);
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(gcmToken) && !TextUtils.isEmpty(trackingId)) {
            refreshTrackingIdFromAPI(username, gcmToken, trackingId);
        }
    }

    private void refreshTrackingIdFromAPI(String username, String gcmToken, String trackingId) {
        try {
            imakaraAPI.refreshTracking(username, gcmToken, trackingId).onSuccessTask(task -> {
                JSONObject response = task.getResult();
                String newTrackingId = response.getString("id");

                String origTrackingId = LocationLogCache.get(this).getString(LocationLogCache.KEY_TRACKING_ID, null);
                if (TextUtils.isEmpty(origTrackingId) || !origTrackingId.equals(newTrackingId)) {
                    LocationLogCache.get(this).edit()
                            .putString(LocationLogCache.KEY_TRACKING_ID, newTrackingId)
                            .apply();

                    return getShortUrl(newTrackingId);
                }

                return Task.forResult(null);
            }).waitForCompletion(API_TIMEOUT_MS, TimeUnit.MILLISECONDS); //IntentServiceなので、ブロックしておかないとstopSelfされてしまう
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private Task<Void> getShortUrl(String trackingId) {
        String trackingUrl = imakaraAPI.getTrackingURLForShare(trackingId);
        return googleAPI.shortenUrl(trackingUrl).onSuccess(task -> {
            String shortUrl = task.getResult();

            LocationLogCache.get(this).edit()
                    .putString(LocationLogCache.KEY_SHORT_URL, shortUrl)
                    .apply();

            return null;
        });
    }
}
