package io.github.yusukeiwaki.imakara.sender;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.github.yusukeiwaki.imakara.etc.LocationLogCache;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

/**
 * 位置情報を測位して、LocationLogCacheに保存する
 * ワンショットサービス
 */
public class PositioningService extends Service {
    private static final String TAG = PositioningService.class.getSimpleName();

    private Looper positioningThreadLooper;

    private CountDownLatch countDownLatch;
    private Looper serviceThreadLooper;
    private Handler serviceThreadHandler;

    public static void start(Context context) {
        Intent intent = new Intent(context, PositioningService.class);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // GoogleのLocationAPIに指定するためのスレッドを用意する。
        HandlerThread positioningThread = new HandlerThread(TAG + "_Postioning");
        positioningThread.start();
        positioningThreadLooper = positioningThread.getLooper();

        // Locationクライアントを動かすスレッド。
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        serviceThreadLooper = handlerThread.getLooper();
        serviceThreadHandler = new Handler(serviceThreadLooper);
        serviceThreadHandler.post(() -> {
            doPositioning();
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        serviceThreadLooper.quit();
        positioningThreadLooper.quit();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void doPositioning() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        ConnectionResult connectionResult = googleApiClient.blockingConnect();
        if (!connectionResult.isSuccess()) {
            Log.e(TAG, String.format("onConnectionFailed: [%d] %s", connectionResult.getErrorCode(), connectionResult.getErrorMessage()));
            stopSelf();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return;
        }

        countDownLatch = new CountDownLatch(1);
        FusedLocationApi.requestLocationUpdates(googleApiClient, buildLocationRequest(), locationListener, positioningThreadLooper);
        try {
            if (countDownLatch.await(13, TimeUnit.SECONDS)) {
                //位置情報が得られた
            } else {
                //タイムアウト
                Log.i(TAG, "requestLocationUpdates: timeout. Fallback to getLastLocation");
                Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                updateLocationLogCache(location);
            }
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
        serviceThreadHandler.post(() -> {
            stopSelf();
        });
    }

    /*package*/ static LocationRequest buildLocationRequest() {
        return new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(4000)
                .setInterval(12000)
                ;
    }

    private LocationListener locationListener = location -> {
        updateLocationLogCache(location);
        countDownLatch.countDown();
    };

    private void updateLocationLogCache(Location location) {
        LocationLogCache.get(this).edit()
                .putFloat(LocationLogCache.KEY_LATITUDE, (float) location.getLatitude())
                .putFloat(LocationLogCache.KEY_LONGITUDE, (float) location.getLongitude())
                .putFloat(LocationLogCache.KEY_ACCURACY, location.getAccuracy())
                .putLong(LocationLogCache.KEY_LAST_UPDATED_AT, location.getTime())
                .apply();
    }
}
