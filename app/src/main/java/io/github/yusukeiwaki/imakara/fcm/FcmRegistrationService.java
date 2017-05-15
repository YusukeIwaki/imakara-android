package io.github.yusukeiwaki.imakara.fcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;

import io.github.yusukeiwaki.imakara.etc.CurrentUserCache;

public class FcmRegistrationService extends IntentService {
    public static Intent newIntent(Context context) {
        return new Intent(context, FcmRegistrationService.class);
    }

    private static final String TAG = FcmRegistrationService.class.getSimpleName();

    public FcmRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // 初回起動時はオンラインになるまでは nullが入っているので注意
        String fcmToken = FirebaseInstanceId.getInstance().getToken();

        if (TextUtils.isEmpty(fcmToken)) {
            CurrentUserCache.get(getApplicationContext()).edit()
                    .remove(CurrentUserCache.KEY_GCM_TOKEN)
                    .apply();
        } else {
            CurrentUserCache.get(getApplicationContext()).edit()
                    .putString(CurrentUserCache.KEY_GCM_TOKEN, fcmToken)
                    .apply();
        }
    }
}
