package io.github.yusukeiwaki.imakara.sender;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;

import io.github.yusukeiwaki.imakara.R;
import io.github.yusukeiwaki.imakara.base.BaseActivity;

public class SenderActivity extends BaseActivity {
    private LocationCacheObserver locationCacheObserver;
    private SenderServiceBindingManager bindingManager;

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

        locationCacheObserver = new LocationCacheObserver(this);
        locationCacheObserver.setOnUpdateListener(locationCacheItem -> {
            TextView text = findViewById2(R.id.debug_text);
            text.setText(locationCacheItem.toString());
        });

        bindingManager = new SenderServiceBindingManager(this);
        bindingManager.setOnStateChangedListener(activated -> {
            setFabState(activated);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindingManager.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationCacheObserver.sub();
    }

    @Override
    protected void onPause() {
        locationCacheObserver.unsub();
        super.onPause();
    }

    @Override
    protected void onStop() {
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
}
