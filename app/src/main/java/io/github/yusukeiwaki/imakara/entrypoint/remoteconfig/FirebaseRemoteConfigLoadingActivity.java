package io.github.yusukeiwaki.imakara.entrypoint.remoteconfig;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

public class FirebaseRemoteConfigLoadingActivity extends AppCompatActivity {

    public static Intent newIntent(Context context, Intent nextIntent) {
        Intent intent = new Intent(context, FirebaseRemoteConfigLoadingActivity.class);
        intent.putExtra(Intent.EXTRA_INTENT, nextIntent);
        return intent;
    }

    private Intent nextIntent;
    private FirebaseRemoteConfigObserver firebaseRemoteConfigObserver;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleIntent();
        firebaseRemoteConfigObserver = new FirebaseRemoteConfigObserver(this);
        firebaseRemoteConfigObserver.setOnUpdateListener(hostname -> {
            if (!TextUtils.isEmpty(hostname)) {
                startActivity(nextIntent);
                finish();
            }
        });

        progressDialog = new ProgressDialog(this);

        if (savedInstanceState == null) {
            FirebaseRemoteConfigService.start(this);
        }
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            Intent nextIntent = intent.getParcelableExtra(Intent.EXTRA_INTENT);
            if (nextIntent != null) {
                this.nextIntent = nextIntent;
                return;
            }
        }
        throw new IllegalArgumentException("no nextIntent");
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRemoteConfigObserver.sub();
        progressDialog.show();
    }

    @Override
    protected void onStop() {
        progressDialog.cancel();
        firebaseRemoteConfigObserver.unsub();
        super.onStop();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
