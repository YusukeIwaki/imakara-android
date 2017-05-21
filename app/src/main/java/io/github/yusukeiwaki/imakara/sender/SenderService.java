package io.github.yusukeiwaki.imakara.sender;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import io.github.yusukeiwaki.imakara.R;

public class SenderService extends Service {

    private static final int NOTIFICATION_ID = 12;
    private static final int RC_POSITIONING_MANUALLY = 13;
    private static final int RC_STOP_SERVICE = 14;

    private static final String KEY_START = "start";

    public static Intent newIntent(Context context, boolean start) {
        Intent intent = new Intent(context, SenderService.class);
        intent.putExtra(KEY_START, start);
        return intent;
    }

    private PositioningRequestReceiver positioningRequestReceiver;
    private LocationUpdater locationUpdater;

    public static void start(Context context) {
        context.startService(newIntent(context, true));
    }

    public static void stop(Context context) {
        // stopServiceだと、Foreground Serviceの通知が消えない。
        context.startService(newIntent(context, false));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        positioningRequestReceiver = new PositioningRequestReceiver();
        registerReceiver(positioningRequestReceiver, PositioningRequestReceiver.newIntentFilter());

        locationUpdater = new LocationUpdater(this);
        locationUpdater.enable();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent.getBooleanExtra(KEY_START, true)) {
            startForeground(NOTIFICATION_ID, buildNotification());
        } else {
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        locationUpdater.disable();
        unregisterReceiver(positioningRequestReceiver);
        super.onDestroy();
    }

    private Notification buildNotification() {
        return new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.request_receiver_notification_title))
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .addAction(R.drawable.ic_vector_black_place, getString(R.string.request_receiver_notification_action_update_location_manually), buildPendingIntentForUpdateLocationManually())
                .addAction(R.drawable.ic_vector_black_close, getString(R.string.request_receiver_notification_action_stop), buildPendingIntentForStopService())
                .build();
    }

    private PendingIntent buildPendingIntentForUpdateLocationManually() {
        return PendingIntent.getActivity(this, RC_POSITIONING_MANUALLY, PositioningRequirementCheckAndStartPositioningActivity.newIntent(this), 0);
    }

    private PendingIntent buildPendingIntentForStopService() {
        return PendingIntent.getService(this, RC_STOP_SERVICE, SenderCancelService.newIntent(this) ,0);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final IBinder binder = new Binder();
}
