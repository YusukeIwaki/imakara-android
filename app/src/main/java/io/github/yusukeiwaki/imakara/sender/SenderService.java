package io.github.yusukeiwaki.imakara.sender;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class SenderService extends Service {

    private static final int NOTIFICATION_ID = 12;

    private static final String KEY_START = "start";

    public static Intent newIntent(Context context, boolean start) {
        Intent intent = new Intent(context, SenderService.class);
        intent.putExtra(KEY_START, start);
        return intent;
    }

    private PositioningRequestReceiver positioningRequestReceiver;
    private LocationUpdater locationUpdater;
    private NotificationUpdater notificationUpdater;

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
            startForeground(NOTIFICATION_ID, new NotificationBuilder(this).buildBaseNotification());
            notificationUpdater = new NotificationUpdater(this, NOTIFICATION_ID);
            notificationUpdater.enable();
        } else {
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (notificationUpdater != null) {
            notificationUpdater.disable();
        }
        locationUpdater.disable();
        unregisterReceiver(positioningRequestReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final IBinder binder = new Binder();
}
