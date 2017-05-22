package io.github.yusukeiwaki.imakara.sender;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import io.github.yusukeiwaki.imakara.R;
import io.github.yusukeiwaki.imakara.base.BaseActivity;

public class SenderActivity extends BaseActivity {
    private SenderServiceBindingManager bindingManager;
    private ShortUrlObserver shortUrlObserver;

    public static Intent newIntent(Context context) {
        return new Intent(context, SenderActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);

        if (savedInstanceState == null) {
            TrackingIdUpdateService.start(this);
        }

        findViewById(R.id.btn_start).setOnClickListener(v -> {
            bindingManager.start();
            SenderService.start(this);
            startPositioning();
        });
        findViewById(R.id.btn_stop).setOnClickListener(v -> {
            SenderService.stop(this);
        });

        bindingManager = new SenderServiceBindingManager(this);
        bindingManager.setOnStateChangedListener(activated -> {
            setFabState(activated);
            setIntroductionVisibility(activated);
        });

        shortUrlObserver = new ShortUrlObserver(this);
        shortUrlObserver.setOnUpdateListener(shortUrl -> {
            TextView urlText = findViewById2(R.id.url_text);
            urlText.setText(TextUtils.isEmpty(shortUrl) ? "" : shortUrl);
        });

        findViewById(R.id.btn_copy_to_clipboard).setOnClickListener(v -> {
            TextView urlText = findViewById2(R.id.url_text);

            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, urlText.getText()));
            Toast.makeText(v.getContext(), R.string.sender_caption_copied_to_clipboard, Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_share).setOnClickListener(v -> {
            TextView urlText = findViewById2(R.id.url_text);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, urlText.getText().toString());

            Intent chooserIntent = Intent.createChooser(intent, getString(R.string.share));
            startActivity(chooserIntent);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        bindingManager.start();
        shortUrlObserver.sub();
    }

    @Override
    protected void onStop() {
        shortUrlObserver.unsub();
        bindingManager.stop();
        super.onStop();
    }

    private void startPositioning() {
        Intent intent = PositioningRequirementCheckAndStartPositioningActivity.newIntent(this);
        startActivity(intent);
    }

    private void setFabState(boolean isActivated) {
        FloatingActionButton fabStart = findViewById2(R.id.btn_start);
        FloatingActionButton fabStop = findViewById2(R.id.btn_stop);

        setFabVisibility(fabStart, !isActivated);
        setFabVisibility(fabStop, isActivated);
    }

    private void setFabVisibility(FloatingActionButton fab, boolean show) {
        if (show) {
            if (fab.getVisibility() == View.VISIBLE) return;
            fab.setScaleX(0.0f);
            fab.setScaleY(0.0f);
            fab.setVisibility(View.VISIBLE);
            fab.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .start();
        } else {
            if (fab.getVisibility() == View.GONE) return;
            fab.setVisibility(View.GONE);
        }
    }

    private void setIntroductionVisibility(boolean isActivated) {
        View introForStart = findViewById(R.id.introduction_inactive);
        View introForStop = findViewById(R.id.introduction_activated);

        if (isActivated) {
            introForStart.setVisibility(View.GONE);
            introForStop.setVisibility(View.VISIBLE);
        } else {
            introForStart.setVisibility(View.VISIBLE);
            introForStop.setVisibility(View.GONE);
        }
    }
}
