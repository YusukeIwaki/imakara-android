package io.github.yusukeiwaki.imakara.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import bolts.Task;
import bolts.TaskCompletionSource;
import io.github.yusukeiwaki.imakara.ImakaraApplication;
import io.github.yusukeiwaki.imakara.etc.OkHttpHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImakaraAPI {
    private static final String TAG = ImakaraAPI.class.getSimpleName();

    private String getHostname() {
        return ImakaraApplication.ENV.API_HOSTNAME;
    }

    private Task<Response> baseRequest(Request request) {
        TaskCompletionSource<Response> tcs = new TaskCompletionSource<>();
        OkHttpHelper.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                tcs.setError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    tcs.setResult(response);
                } else {
                    tcs.setError(new HttpError(response.code(), response.body().string()));
                }
            }
        });
        return tcs.getTask();
    }

    private Task<JSONObject> baseJsonRequest(Request request) {
        return baseRequest(request).onSuccess(task -> {
            Response response = task.getResult();
            return new JSONObject(response.body().string());
        });
    }

    public Task<JSONObject> createOrUpdateTracking(String username, String fcmToken) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(getHostname())
                .addPathSegment("trackings")
                .build();

        JSONObject json = new JSONObject();
        try {
            json.put("user", new JSONObject()
                    .put("name", username)
                    .put("gcm_token", fcmToken));
        } catch (JSONException e) {
            Log.wtf(TAG, "failed to build JSON.", e);
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

        Request req = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return baseJsonRequest(req);
    }

    public Task<JSONObject> getTracking(String trackingId) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(getHostname())
                .addPathSegment("trackings")
                .addPathSegment(trackingId)
                .build();

        Request req = new Request.Builder()
                .url(url)
                .build();

        return baseJsonRequest(req);
    }

    public Task<JSONObject> updateLocationLog(String trackingId, double lat, double lon, double accuracy) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(getHostname())
                .addPathSegment("trackings")
                .addPathSegment(trackingId)
                .addPathSegment("location_logs")
                .build();

        JSONObject json = new JSONObject();
        try {
            json.put("location_log", new JSONObject()
                    .put("lat", lat)
                    .put("lon", lon)
                    .put("accuracy", accuracy > 0 ? accuracy : JSONObject.NULL));
        } catch (JSONException e) {
            Log.wtf(TAG, "failed to build JSON.", e);
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

        Request req = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return baseJsonRequest(req);
    }

    public Task<JSONObject> observeTracking(String trackingId, String username, String fcmToken) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(getHostname())
                .addPathSegment("trackings")
                .addPathSegment(trackingId)
                .addPathSegment("observe")
                .build();

        JSONObject json = new JSONObject();
        try {
            json.put("user", new JSONObject()
                    .put("username", username)
                    .put("gcm_token", fcmToken));
        } catch (JSONException e) {
            Log.wtf(TAG, "failed to build JSON.", e);
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

        Request req = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return baseJsonRequest(req);
    }

    public Task<JSONObject> unobserveTracking(String trackingId, String username, String fcmToken) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(getHostname())
                .addPathSegment("trackings")
                .addPathSegment(trackingId)
                .addPathSegment("observe")
                .build();

        JSONObject json = new JSONObject();
        try {
            json.put("user", new JSONObject()
                    .put("username", username)
                    .put("gcm_token", fcmToken));
        } catch (JSONException e) {
            Log.wtf(TAG, "failed to build JSON.", e);
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

        Request req = new Request.Builder()
                .url(url)
                .delete(body)
                .build();

        return baseJsonRequest(req);
    }
}
