package io.github.yusukeiwaki.imakara.sender;

public abstract class LocationCacheItem {
    public abstract float lat();
    public abstract float lon();
    public abstract float accuracy();
    public abstract long timestamp();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LocationCacheItem) {
            LocationCacheItem another = (LocationCacheItem) obj;
            return lat() == another.lat()
                    && lon() == another.lon()
                    && accuracy() == another.accuracy()
                    && timestamp() == another.timestamp();
        }
        return false;
    }

    public int hashCode() {
        int result = (lat() != +0.0f ? Float.floatToIntBits(lat()) : 0);
        result = 31 * result + (lon() != +0.0f ? Float.floatToIntBits(lon()) : 0);
        result = 31 * result + (accuracy() != +0.0f ? Float.floatToIntBits(accuracy()) : 0);
        result = 31 * result + (int) (timestamp() ^ (timestamp() >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "LocationCacheItem{" +
                "lat=" + lat() +
                ", lon=" + lon() +
                ", accuracy=" + accuracy() +
                ", timestamp=" + timestamp() +
                '}';
    }
}
