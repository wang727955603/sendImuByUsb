package sensor.data;

/**
 * @author Created by CM-WANGMIN on 2017/8/18.
 */

public class MessageToPC {
    ImuData acc;
    ImuData gyr;
    ImuData mag;
    GpsData gps;

    public  MessageToPC(){
        
    }
    public MessageToPC(ImuData acc,ImuData gyr,ImuData mag){
        this(acc,gyr,mag,null);
    }

    public MessageToPC(ImuData acc, ImuData gyr,ImuData mag, GpsData gps) {
        this.acc = acc;
        this.gyr = gyr;
        this.mag = mag;
        this.gps = gps;
    }

    public ImuData getAcc() {
        return acc;
    }

    public void setAcc(ImuData acc) {
        this.acc = acc;
    }

    public ImuData getGyr() {
        return gyr;
    }

    public void setGyr(ImuData gyr) {
        this.gyr = gyr;
    }

    public ImuData getMag() {
        return mag;
    }

    public void setMag(ImuData mag) {
        this.mag = mag;
    }

    public GpsData getGps() {
        return gps;
    }

    public void setGps(GpsData gps) {
        this.gps = gps;
    }

    @Override
    public String toString() {
        return "MessageToPC{" +
                "acc=" + acc.toString() +
                ", gyr=" + gyr.toString() +
                ", mag=" + mag.toString() +
                ", gps=" + gps +
                '}';
    }
}
