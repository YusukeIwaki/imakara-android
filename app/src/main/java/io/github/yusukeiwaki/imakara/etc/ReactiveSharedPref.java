package io.github.yusukeiwaki.imakara.etc;

import android.content.SharedPreferences;

public class ReactiveSharedPref<T> {
    private final SharedPreferences prefs;

    public interface ObservationPolicy<T> {
        boolean isTargetKey(String key);
        T getValueFromSharedPreference(SharedPreferences prefs);
    }

    public interface OnUpdateListener<T> {
        void onPreferenceValueUpdated(T value);
    }

    private final ObservationPolicy<T> observationPolicy;
    private OnUpdateListener<T> onUpdateListener;
    private T prevValue;

    public ReactiveSharedPref(SharedPreferences prefs, ObservationPolicy<T> observationPolicy) {
        this.prefs = prefs;
        this.observationPolicy = observationPolicy;
    }

    public void setOnUpdateListener(OnUpdateListener<T> onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public void sub() {
        prevValue = observationPolicy.getValueFromSharedPreference(prefs);
        if (onUpdateListener != null) {
            onUpdateListener.onPreferenceValueUpdated(prevValue);
        }

        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unsub() {
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (!observationPolicy.isTargetKey(key) || onUpdateListener == null) return;

            T newValue = observationPolicy.getValueFromSharedPreference(sharedPreferences);
            if (prevValue == null) {
                if (newValue == null) return;

                onUpdateListener.onPreferenceValueUpdated(newValue);
            } else if (prevValue.equals(newValue)) {
                onUpdateListener.onPreferenceValueUpdated(newValue);
            }
        }
    };
}
