package io.github.yusukeiwaki.imakara.sender;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;

import io.github.yusukeiwaki.imakara.R;
import io.github.yusukeiwaki.imakara.base.BaseActivity;
import io.github.yusukeiwaki.imakara.etc.LocationLogCache;
import io.github.yusukeiwaki.imakara.etc.ReactiveSharedPref;

public class SenderActivity extends BaseActivity {
    private ReactiveSharedPref<LocationCacheItem> reactiveSharedPref;
    private PositioningReceiverBindingManager bindingManager;

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
            PositioningRequestReceiverNotificationService.start(this);
            startPositioning();
        });
        findViewById(R.id.btn_stop).setOnClickListener(v -> {
            PositioningRequestReceiverNotificationService.stop(this);
        });

        reactiveSharedPref = new ReactiveSharedPref<>(LocationLogCache.get(this), new ReactiveSharedPref.ObservationPolicy<LocationCacheItem>() {
            @Override
            public boolean isTargetKey(String key) {
                return true;
            }

            @Override
            public LocationCacheItem getValueFromSharedPreference(SharedPreferences prefs) {
                return new LocationCacheItem.Builder()
                        .lat(prefs.getFloat(LocationLogCache.KEY_LATITUDE, 0))
                        .lon(prefs.getFloat(LocationLogCache.KEY_LONGITUDE, 0))
                        .accuracy(prefs.getFloat(LocationLogCache.KEY_ACCURACY, 0))
                        .timestamp(prefs.getLong(LocationLogCache.KEY_LAST_UPDATED_AT, 0))
                        .build();
            }
        });
        reactiveSharedPref.setOnUpdateListener(locationCacheItem -> {
            TextView text = findViewById2(R.id.debug_text);
            text.setText(locationCacheItem.toString());
        });

        bindingManager = new PositioningReceiverBindingManager(this);
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
        reactiveSharedPref.sub();
    }

    @Override
    protected void onPause() {
        reactiveSharedPref.unsub();
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
