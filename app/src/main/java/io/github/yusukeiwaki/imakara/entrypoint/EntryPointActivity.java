package io.github.yusukeiwaki.imakara.entrypoint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import io.github.yusukeiwaki.imakara.etc.CurrentUserCache;
import io.github.yusukeiwaki.imakara.fcm.FcmRegistrationService;
import io.github.yusukeiwaki.imakara.sender.SenderActivity;
import io.github.yusukeiwaki.imakara.setup.SetupActivity;

public class EntryPointActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(FcmRegistrationService.newIntent(this));

        launchProperActivity();
        finish();
    }

    private void launchProperActivity() {
        final String username = CurrentUserCache.get(this).getString(CurrentUserCache.KEY_USERNAME, null);
        if (TextUtils.isEmpty(username)) {
            Intent intent = SetupActivity.newIntent(this);
            startActivity(intent);
        } else {
            Intent intent = SenderActivity.newIntent(this);
            startActivity(intent);
        }
    }
}
