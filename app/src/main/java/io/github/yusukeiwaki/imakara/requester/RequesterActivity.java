package io.github.yusukeiwaki.imakara.requester;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;

import io.github.yusukeiwaki.imakara.R;
import io.github.yusukeiwaki.imakara.base.BaseActivity;

public class RequesterActivity extends BaseActivity implements OnMapReadyCallback {
    private static final String KEY_TRACKING_ID = "trackingId";

    public static Intent newIntent(Context context, String trackingId) {
        Intent intent = new Intent(context, RequesterActivity.class);
        intent.putExtra(KEY_TRACKING_ID, trackingId);
        return intent;
    }

    private String trackingId;
    private LocationLogObserver locationLogObserver;
    private MapView googleMapView;
    private GoogleMap googleMap;
    private Circle mapCircle;
    private Marker mapMarker;

    private Timer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            trackingId = intent.getStringExtra(KEY_TRACKING_ID);
        }

        if (TextUtils.isEmpty(trackingId)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_requester);
        googleMapView = findViewById2(R.id.google_map_view);
        googleMapView.onCreate(savedInstanceState);
        googleMapView.getMapAsync(this);

        locationLogObserver = new LocationLogObserver(trackingId);
        locationLogObserver.setOnChangeListener(locationLog ->  {
            if (locationLog == null || googleMap == null) return;

            LatLng position = new LatLng(locationLog.lat, locationLog.lon);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));

            if (mapCircle == null) {
                mapCircle = googleMap.addCircle(new CircleOptions()
                        .center(position)
                        .radius(locationLog.accuracy)
                        .fillColor(Color.argb(16, 0, 64, 255))
                        .strokeColor(Color.argb(56, 0, 64, 255))
                        .strokeWidth(2));
            } else {
                mapCircle.setCenter(new LatLng(locationLog.lat, locationLog.lon));
                mapCircle.setRadius(locationLog.accuracy);
            }

            if (mapMarker == null) {
                mapMarker = googleMap.addMarker(new MarkerOptions()
                        .position(position));
            } else {
                mapMarker.setPosition(position);
            }
        });

        if (savedInstanceState == null) {
            LocationLogUpdateService.start(this, trackingId);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        locationLogObserver.sub();
    }


    @Override
    protected void onStart() {
        super.onStart();
        googleMapView.onStart();
        locationLogObserver.sub();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LocationLogUpdateService.start(RequesterActivity.this, trackingId);
            }
        }, 5000, 7500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        googleMapView.onResume();
    }

    @Override
    protected void onPause() {
        googleMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        timer.cancel();
        locationLogObserver.unsub();
        googleMapView.onStop();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        googleMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        googleMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        googleMapView.onLowMemory();
    }
}
