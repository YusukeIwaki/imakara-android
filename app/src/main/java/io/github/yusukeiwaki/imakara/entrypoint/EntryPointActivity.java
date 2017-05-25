package io.github.yusukeiwaki.imakara.entrypoint;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.yusukeiwaki.imakara.ImakaraApplication;
import io.github.yusukeiwaki.imakara.etc.CurrentUserCache;
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
        startActivity(getNextActivityIntent());
    }

    private Intent getNextActivityIntent() {
        Uri uri = null;
        Intent intent = getIntent();
        if (intent != null) {
            uri = intent.getData();
        }

        if (uri != null) {
            Matcher m = Pattern.compile(String.format("^https://%s/trackings/([^/]+)/location\\.png", ImakaraApplication.ENV.API_HOSTNAME))
                    .matcher(uri.toString());
            if (m.find()) {
                String trackingId = m.group(1);
                return RequesterActivity.newIntent(this, trackingId);
            }
        }

        final String username = CurrentUserCache.get(this).getString(CurrentUserCache.KEY_USERNAME, null);
        if (TextUtils.isEmpty(username)) {
            return SetupActivity.newIntent(this, SenderActivity.newIntent(this));
        }

        return SenderActivity.newIntent(this);
    }
}
