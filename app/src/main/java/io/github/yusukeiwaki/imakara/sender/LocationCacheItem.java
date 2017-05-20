package io.github.yusukeiwaki.imakara.sender;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE)
public interface LocationCacheItem {
    float lat();
    float lon();
    float accuracy();
    long timestamp();

    class Builder extends ImmutableLocationCacheItem.Builder {}
}
