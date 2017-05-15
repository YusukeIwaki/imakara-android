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
            startPositioning();
        });
        setFabState(false);

        reactiveSharedPref = new ReactiveSharedPref<>(LocationLogCache.get(this), new ReactiveSharedPref.ObservationPolicy<LocationCacheItem>() {
            @Override
            public boolean isTargetKey(String key) {
                return true;
            }

            @Override
            public LocationCacheItem getValueFromSharedPreference(SharedPreferences prefs) {
                return new LocationCacheItem() {
                    @Override
                    public float lat() {
                        return prefs.getFloat(LocationLogCache.KEY_LATITUDE, 0);
                    }

                    @Override
                    public float lon() {
                        return prefs.getFloat(LocationLogCache.KEY_LONGITUDE, 0);
                    }

                    @Override
                    public float accuracy() {
                        return prefs.getFloat(LocationLogCache.KEY_ACCURACY, 0);
                    }

                    @Override
                    public long timestamp() {
                        return prefs.getLong(LocationLogCache.KEY_LAST_UPDATED_AT, 0);
                    }
                };
            }
        });
        reactiveSharedPref.setOnUpdateListener(locationCacheItem -> {
            TextView text = findViewById2(R.id.debug_text);
            text.setText(locationCacheItem.toString());
        });
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

            fab.setVisibility(View.VISIBLE);
            fab.show();
        } else {
            if (fab.getVisibility() == View.GONE) return;

            fab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onHidden(FloatingActionButton fab) {
                    fab.setVisibility(View.GONE);
                }
            });
        }
    }
}
