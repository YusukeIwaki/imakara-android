package io.github.yusukeiwaki.imakara.requester;

import android.content.Context;
import android.content.Intent;

import io.github.yusukeiwaki.imakara.base.BaseActivity;

public class RequesterActivity extends BaseActivity {
    public static Intent newIntent(Context context) {
        return new Intent(context, RequesterActivity.class);
    }
}
