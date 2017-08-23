package sensor.data;

/**
 * @author Created by CM-WANGMIN on 2017/8/18.
 */

public class GpsData {
    long timestamp;
    double latitude;
    double longitude;

    public GpsData(long timestamp,double latitude, double longitude) {
        this.timestamp=timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "GpsData{" +
                "timestamp="+timestamp+
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
