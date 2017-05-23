package io.github.yusukeiwaki.imakara.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Tracking extends RealmObject {
    @PrimaryKey
    public String id;
    public LocationLog location_log;
    public long updated_at;

    @Override
    public String toString() {
        return "Tracking{" +
                "id='" + id + '\'' +
                ", location_log=" + location_log +
                ", updated_at=" + updated_at +
                '}';
    }
}
