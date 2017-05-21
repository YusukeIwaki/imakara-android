package io.github.yusukeiwaki.imakara.fcm;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import io.github.yusukeiwaki.imakara.etc.LocationLogCache;
import io.github.yusukeiwaki.imakara.sender.PositioningRequestReceiver;

public class PushDataHandler {
    private static final String TAG = PushDataHandler.class.getSimpleName();
    private final Context context;
    private final PushData pushData;

    public PushDataHandler(Context context, PushData pushData) {
        this.context = context;
        this.pushData = pushData;
    }

    public void handle() {
        if (PushData.TYPE_UPDATE_LOCATION_LOG.equals(pushData.pushType())) {
            boolean shouldUpdateLocationLog = false;
            String cachedTrackingId = LocationLogCache.get(context).getString(LocationLogCache.KEY_TRACKING_ID, null);
            try {
                shouldUpdateLocationLog = pushData.tracking().getString("id").equals(cachedTrackingId);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }

            if (shouldUpdateLocationLog) {
                // PositioningService.start(context); を直叩きだといつでも測位してしまうので、
                // ここではブロードキャストを投げるだけにして、
                // PositioningRequestReceiverNotificationServiceが動いているときだけ測位するようにする。
                context.sendBroadcast(PositioningRequestReceiver.newIntent());
            }
        }
        else if (PushData.TYPE_NEW_LOCATION_LOG.equals(pushData.pushType())) {

        }
    }
}
