package io.github.yusukeiwaki.imakara.sender;

import android.content.Context;
import android.content.Intent;

import io.github.yusukeiwaki.imakara.base.BaseActivity;

public class SenderActivity extends BaseActivity {
    public static Intent newIntent(Context context) {
        return new Intent(context, SenderActivity.class);
    }
}
