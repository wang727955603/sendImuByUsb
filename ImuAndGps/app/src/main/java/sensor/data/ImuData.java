package sensor.data;

/**
 * @author Created by CM-WANGMIN on 2017/8/18.
 */

public class ImuData {
    private long timestamp;
    private float x;
    private float y;
    private float z;

    public ImuData(long timestamp, float x, float y, float z) {
        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timeStamp) {
        this.timestamp = timestamp;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "ImuData{" +
                "timeStamp=" + timestamp +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
