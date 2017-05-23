package io.github.yusukeiwaki.imakara.requester;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.github.yusukeiwaki.imakara.api.ImakaraAPI;
import io.github.yusukeiwaki.imakara.etc.realm.RealmHelper;
import io.github.yusukeiwaki.imakara.model.Tracking;

public class LocationLogUpdateService extends IntentService {
    private static final String TAG = LocationLogUpdateService.class.getSimpleName();
    private static final int API_TIMEOUT_MS = 4500;

    private static final String KEY_TRACKING_ID = "trackingId";
    private final ImakaraAPI imakaraAPI;

    public static void start(Context context, String trackingId) {
        Intent intent = new Intent(context, LocationLogUpdateService.class);
        intent.putExtra(KEY_TRACKING_ID, trackingId);
        context.startService(intent);
    }

    public LocationLogUpdateService() {
        super(TAG);
        imakaraAPI = new ImakaraAPI();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String trackingId = intent.getStringExtra(KEY_TRACKING_ID);
        if (!TextUtils.isEmpty(trackingId)) {
            getLocationLogFromAPI(trackingId);
        }
    }

    private void getLocationLogFromAPI(String trackingId) {
        try {
            imakaraAPI.getTracking(trackingId).onSuccessTask(task -> {
                final JSONObject response = task.getResult();
                return RealmHelper.executeTransaction(realm -> {
                    realm.createOrUpdateObjectFromJson(Tracking.class, response);
                    return null;
                });
            }).waitForCompletion(API_TIMEOUT_MS, TimeUnit.MILLISECONDS); //IntentServiceなので、ブロックしておかないとstopSelfされてしまう
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
