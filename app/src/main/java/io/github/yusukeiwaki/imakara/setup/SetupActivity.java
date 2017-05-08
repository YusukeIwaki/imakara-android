package io.github.yusukeiwaki.imakara.setup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import io.github.yusukeiwaki.imakara.R;
import io.github.yusukeiwaki.imakara.base.BaseActivity;
import io.github.yusukeiwaki.imakara.etc.CurrentUserCache;
import io.github.yusukeiwaki.imakara.etc.SimpleTextWatcher;
import io.github.yusukeiwaki.imakara.sender.SenderActivity;

public class SetupActivity extends BaseActivity {
    public static Intent newIntent(Context context) {
        return new Intent(context, SetupActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        final EditText editorUsername = findViewById2(R.id.editor_username);
        final Button btnProceed = findViewById2(R.id.btn_proceed);

        editorUsername.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                btnProceed.setEnabled(s.length() >= 4);
            }
        });

        btnProceed.setOnClickListener(v -> {
            CurrentUserCache.get(this).edit()
                    .putString(CurrentUserCache.KEY_USERNAME, editorUsername.getText().toString())
                    .apply();
        });
    }

    private SharedPreferences.OnSharedPreferenceChangeListener usernameListener = (prefs, key) -> {
        if (CurrentUserCache.KEY_USERNAME.equals(key)) {
            String username = prefs.getString(CurrentUserCache.KEY_USERNAME, null);
            if (!TextUtils.isEmpty(username)) {
                proceed();
                finish();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        CurrentUserCache.get(this).registerOnSharedPreferenceChangeListener(usernameListener);
    }

    @Override
    protected void onPause() {
        CurrentUserCache.get(this).unregisterOnSharedPreferenceChangeListener(usernameListener);

        super.onPause();
    }

    private void proceed() {
        Intent intent = SenderActivity.newIntent(this);
        startActivity(intent);
    }
}
