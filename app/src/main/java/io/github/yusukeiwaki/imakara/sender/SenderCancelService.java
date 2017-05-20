package io.github.yusukeiwaki.imakara.sender;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * {@link SenderService} を止めるサービス
 */
public class SenderCancelService extends IntentService {
    private static final String TAG = SenderCancelService.class.getSimpleName();

    public static Intent newIntent(Context context) {
        return new Intent(context, SenderCancelService.class);
    }

    public SenderCancelService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SenderService.stop(this);
    }
}
