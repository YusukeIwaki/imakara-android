package io.github.yusukeiwaki.imakara.api;

import org.json.JSONObject;

import java.io.IOException;

import bolts.Task;
import bolts.TaskCompletionSource;
import io.github.yusukeiwaki.imakara.etc.OkHttpHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

abstract class APIBase {
    protected Task<Response> baseRequest(Request request) {
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

    protected Task<JSONObject> baseJsonRequest(Request request) {
        return baseRequest(request).onSuccess(task -> {
            Response response = task.getResult();
            return new JSONObject(response.body().string());
        });
    }
}
