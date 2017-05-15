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

import io.github.yusukeiwaki.imakara.api.ImakaraAPI;
import io.github.yusukeiwaki.imakara.etc.CurrentUserCache;
import io.github.yusukeiwaki.imakara.etc.LocationLogCache;

public class TrackingIdUpdateService extends IntentService {
    private static final String TAG = TrackingIdUpdateService.class.getSimpleName();
    private static final int API_TIMEOUT_MS = 4500;

    public static void start(Context context) {
        Intent intent = new Intent(context, TrackingIdUpdateService.class);
        context.startService(intent);
    }

    public TrackingIdUpdateService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SharedPreferences prefs = CurrentUserCache.get(this);
        String username = prefs.getString(CurrentUserCache.KEY_USERNAME, null);
        String gcmToken = prefs.getString(CurrentUserCache.KEY_GCM_TOKEN, null);
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(gcmToken)) {
            getTrackingIdFromAPI(username, gcmToken);
        }
    }

    private void getTrackingIdFromAPI(String username, String gcmToken) {
        try {
            new ImakaraAPI().createOrUpdateTracking(username, gcmToken).onSuccess(task -> {
                JSONObject response = task.getResult();
                String trackingId = response.getString("id");
                LocationLogCache.get(this).edit()
                        .putString(LocationLogCache.KEY_TRACKING_ID, trackingId)
                        .apply();
                return null;
            }).waitForCompletion(API_TIMEOUT_MS, TimeUnit.MILLISECONDS); //IntentServiceなので、ブロックしておかないとstopSelfされてしまう
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
