package io.github.yusukeiwaki.imakara.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import io.github.yusukeiwaki.imakara.etc.CurrentUserCache;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIdService.class.getName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        CurrentUserCache.get(getApplicationContext()).edit()
                .putString(CurrentUserCache.KEY_GCM_TOKEN, refreshedToken)
                .apply();
    }
}
