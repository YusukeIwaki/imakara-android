package io.github.yusukeiwaki.imakara.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import bolts.Task;
import io.github.yusukeiwaki.imakara.ImakaraApplication;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class GoogleAPI extends APIBase {
    private static final String TAG = GoogleAPI.class.getSimpleName();

    private String getApiKey() {
        return ImakaraApplication.ENV.URL_SHORTENER_API_KEY;
    }

    public Task<String> shortenUrl(String longUrl) {
        String url = "https://www.googleapis.com/urlshortener/v1/url?key=" + getApiKey();

        JSONObject json = new JSONObject();
        try {
            json.put("longUrl", longUrl);
        } catch (JSONException e) {
            Log.wtf(TAG, "failed to build JSON.", e);
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

        Request req = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return baseJsonRequest(req).onSuccess(task -> {
            JSONObject responseJson = task.getResult();
            return responseJson.getString("id");
        });
    }
}
