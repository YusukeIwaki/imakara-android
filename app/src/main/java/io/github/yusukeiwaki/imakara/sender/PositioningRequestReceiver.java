package io.github.yusukeiwaki.imakara.sender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import io.github.yusukeiwaki.imakara.BuildConfig;

public class PositioningRequestReceiver extends BroadcastReceiver {

    private static final String POSITIONING_REQUEST_RECEIVED = BuildConfig.APPLICATION_ID + ".action.POSITIONING_REQUEST_RECEIVED";

    public static Intent newIntent() {
        return new Intent(POSITIONING_REQUEST_RECEIVED);
    }

    public static IntentFilter newIntentFilter() {
        return new IntentFilter(POSITIONING_REQUEST_RECEIVED);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (POSITIONING_REQUEST_RECEIVED.equals(intent.getAction())) {
            PositioningService.start(context);
        }
    }
}
