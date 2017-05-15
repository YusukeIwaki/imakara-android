package io.github.yusukeiwaki.imakara.sender;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

public class PositioningReceiverStopService extends IntentService {
    private static final String TAG = PositioningReceiverStopService.class.getSimpleName();

    public static Intent newIntent(Context context) {
        return new Intent(context, PositioningReceiverStopService.class);
    }

    public PositioningReceiverStopService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        PositioningRequestReceiverNotificationService.stop(this);
    }
}
