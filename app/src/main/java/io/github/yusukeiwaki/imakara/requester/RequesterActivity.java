package io.github.yusukeiwaki.imakara.requester;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import io.github.yusukeiwaki.imakara.R;
import io.github.yusukeiwaki.imakara.base.BaseActivity;

public class RequesterActivity extends BaseActivity {
    private static final String KEY_TRACKING_ID = "trackingId";

    public static Intent newIntent(Context context, String trackingId) {
        Intent intent = new Intent(context, RequesterActivity.class);
        intent.putExtra(KEY_TRACKING_ID, trackingId);
        return intent;
    }

    private String trackingId;

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
        TextView debugText = findViewById2(R.id.text);
        debugText.setText(trackingId);
    }
}
