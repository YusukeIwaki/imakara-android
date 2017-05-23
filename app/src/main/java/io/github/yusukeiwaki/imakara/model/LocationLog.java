package io.github.yusukeiwaki.imakara.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LocationLog extends RealmObject {
    @PrimaryKey
    public long id;
    public double lat;
    public double lon;
    public double accuracy;
    public long created_at;

    @Override
    public String toString() {
        return "LocationLog{" +
                "id=" + id +
                ", lat=" + lat +
                ", lon=" + lon +
                ", accuracy=" + accuracy +
                ", created_at=" + created_at +
                '}';
    }
}
