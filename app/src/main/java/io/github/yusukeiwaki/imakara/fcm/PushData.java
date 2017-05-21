package io.github.yusukeiwaki.imakara.fcm;

import org.immutables.value.Value;
import org.json.JSONObject;

@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE)
public interface PushData {
    String TYPE_NEW_LOCATION_LOG = "new_location_log";
    String TYPE_UPDATE_LOCATION_LOG = "update_location_log";

    String pushType();
    JSONObject tracking();

    class Builder extends ImmutablePushData.Builder {}
}
