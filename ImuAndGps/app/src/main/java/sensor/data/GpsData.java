package sensor.data;

/**
 * @author Created by CM-WANGMIN on 2017/8/18.
 */

public class GpsData {
    double latitude;
    double longitude;

    public GpsData(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
