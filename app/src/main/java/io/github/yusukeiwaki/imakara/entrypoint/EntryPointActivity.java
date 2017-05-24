package io.github.yusukeiwaki.imakara.entrypoint;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.yusukeiwaki.imakara.entrypoint.remoteconfig.FirebaseRemoteConfigLoadingActivity;
import io.github.yusukeiwaki.imakara.entrypoint.remoteconfig.FirebaseRemoteConfigService;
import io.github.yusukeiwaki.imakara.etc.CurrentUserCache;
import io.github.yusukeiwaki.imakara.etc.FirebaseRemoteConfigCache;
import io.github.yusukeiwaki.imakara.fcm.FcmRegistrationService;
import io.github.yusukeiwaki.imakara.requester.RequesterActivity;
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
        Intent intent = getMainActivityIntent();

        final String username = CurrentUserCache.get(this).getString(CurrentUserCache.KEY_USERNAME, null);
        Intent nextIntent = TextUtils.isEmpty(username) ?
                SetupActivity.newIntent(this, intent) : intent;

        switch (FirebaseRemoteConfigCache.getCurrentState(this)) {
            case NO_CONFIG:
                startActivity(FirebaseRemoteConfigLoadingActivity.newIntent(this, nextIntent));
                break;
            case OLD_CONFIG:
                FirebaseRemoteConfigService.start(this);
                startActivity(nextIntent);
                break;
            case OK:
                startActivity(nextIntent);
                break;
        }
    }

    private Intent getMainActivityIntent() {
        Uri uri = null;
        Intent intent = getIntent();
        if (intent != null) {
            uri = intent.getData();
        }

        if (uri != null) {
            Matcher m = Pattern.compile(String.format("^https://%s/trackings/([^/]+)/location\\.png", FirebaseRemoteConfigCache.getCachedHostname(this)))
                    .matcher(uri.toString());
            if (m.find()) {
                String trackingId = m.group(1);
                return RequesterActivity.newIntent(this, trackingId);
            }
        }

        return SenderActivity.newIntent(this);
    }
}
