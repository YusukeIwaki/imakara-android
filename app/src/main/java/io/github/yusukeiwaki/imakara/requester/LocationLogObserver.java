package io.github.yusukeiwaki.imakara.requester;

import io.github.yusukeiwaki.imakara.etc.realm.RealmObjectObserver;
import io.github.yusukeiwaki.imakara.model.LocationLog;
import io.github.yusukeiwaki.imakara.model.Tracking;
import io.realm.Realm;
import io.realm.RealmQuery;

public class LocationLogObserver extends RealmObjectObserver<Tracking> {
    private final String trackingId;

    public interface OnChangeListener {
        void onChange(LocationLog locationLog);
    }
    private OnChangeListener onChangeListener;
    private String prevLocationLogString;

    public LocationLogObserver(String trackingId) {
        this.trackingId = trackingId;
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    @Override
    protected RealmQuery<Tracking> query(Realm realm) {
        return realm.where(Tracking.class).equalTo("id", trackingId);
    }

    @Override
    protected final void onChange(Tracking tracking) {
        LocationLog locationLog = tracking == null ? null : tracking.location_log;
        String locationLogString = locationLog == null ? null : locationLog.toString();

        if (onChangeListener != null) {
            if (prevLocationLogString == null) {
                if (locationLogString != null) {
                    onChangeListener.onChange(locationLog);
                }
            } else if(!prevLocationLogString.equals(locationLogString)) {
                onChangeListener.onChange(locationLog);
            }
        }
        prevLocationLogString = locationLogString;
    }

    @Override
    public void sub() {
        super.sub();
        prevLocationLogString = null;
    }
}
