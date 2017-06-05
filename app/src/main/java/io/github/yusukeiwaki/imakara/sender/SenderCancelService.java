package io.github.yusukeiwaki.imakara.sender;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * {@link SenderService} を明示的に（ユーザの意志で）止めるサービス
 * PATCH /trackings/:id を叩く
 */
public class SenderCancelService extends Service {
    public static Intent newIntent(Context context) {
        return new Intent(context, SenderCancelService.class);
    }

    public static void start(Context context) {
        context.startService(SenderCancelService.newIntent(context));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SenderService.stop(this);
        TrackingIdRefreshService.start(this);
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
