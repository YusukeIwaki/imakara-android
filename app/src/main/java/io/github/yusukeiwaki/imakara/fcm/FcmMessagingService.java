package io.github.yusukeiwaki.imakara.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class FcmMessagingService extends FirebaseMessagingService {
    private static final String TAG = FcmMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        final Map<String, String> data = remoteMessage.getData();

        try {
            PushData pushData = new PushData.Builder()
                    .pushType(data.get("push_type"))
                    .tracking(new JSONObject(data.get("tracking")))
                    .build();

            new PushDataHandler(this, pushData).handle();
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
