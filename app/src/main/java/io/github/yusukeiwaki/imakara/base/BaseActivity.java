package io.github.yusukeiwaki.imakara.base;

import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public abstract class BaseActivity extends AppCompatActivity {

    /**
     * findViewByIdついでにキャストする
     */
    protected <T extends View> T findViewById2(@IdRes int viewId) {
        return (T) findViewById(viewId);
    }
}
