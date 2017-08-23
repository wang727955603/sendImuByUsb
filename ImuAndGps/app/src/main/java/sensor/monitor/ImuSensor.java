package sensor.monitor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import sensor.data.ImuData;
import sensor.data.MessageToPC;
import thread.utils.Constants;

/**
 * @author Created by CM-WANGMIN on 2017/8/18.
 */

public class ImuSensor implements SensorEventListener {
    private static final String TAG = "ImuSensor";

    private Context mContext;
    private Handler handler;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mMagnetic;
    private Sensor mRotationSensor;

    private long accTimestamp       = 0l;   //  每次onSensorChanged()且Event == ACC时的系统时间
    private long gyroTimestamp      = 0l;   //  每次onSensorChanged()且Event == GYRO时的系统时间
    private long magTimestamp       = 0l;
    private long rotationTimestamp  = 0l;   //  每次onSensorChanged()且Event == RotationSensor的系统时间
    private long logTimestamp       = 0l;   //  每次往TXT文件中写数据的时间

    // 控制传感器帧率定时器
    private Timer timerSensorCapture;

    private int count_acc=0;
    private int count_gyc=0;

    private float[] accOutput                   = new float[3];     // 加速度计变量
    private float[] gyroOutput                  = new float[3];     // 陀螺仪变量
    private float[] magOutput                  = new float[3];      // 磁力计变量
    private float[] rotationVectorQuaternion    = new float[4];     // 旋转向量四元数
    private float[] rotationVectorMatrix        = new float[9];     // 旋转向量矩阵

    private float[] values=new float[3];
    private float[] rotate=new float[9];

    ImuData acc;
    ImuData gyr;
    ImuData mag;

    public ImuSensor(Context context, Handler handler){
        this.mContext=context;
        this.handler=handler;

    }
    public void  startSensor(){
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer   = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope       = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mRotationSensor  = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mMagnetic        = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(accListener, mAccelerometer , SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(gyrListener, mGyroscope , SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(magListener, mMagnetic , SensorManager.SENSOR_DELAY_FASTEST);
        // mSensorManager.registerListener(this, mRotationSensor, SensorManager.SENSOR_DELAY_FASTEST);

        timerSensorCapture = new Timer();
        timerSensorCapture.schedule(new sensorCaptureThread(), 500, Constants.INTERVAL_SEND_TO_SERVER);

    }
    class sensorCaptureThread extends TimerTask {
        @Override
        public void run() {
            acc=new ImuData(accTimestamp,accOutput[0],accOutput[1],accOutput[2]);
            gyr=new ImuData(gyroTimestamp,gyroOutput[0],gyroOutput[1],gyroOutput[2]);
            mag=new ImuData(magTimestamp,(float)Math.toDegrees(values[0]),(float)Math.toDegrees(values[1]),(float)Math.toDegrees(values[2]));

            MessageToPC imu=new MessageToPC(acc,gyr,mag);
            Message msg=new Message();
            msg.obj=imu;
            msg.what=1003;
            handler.sendMessage(msg);
        }
    }
    private SensorEventListener accListener=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            accTimestamp = event.timestamp;
           /* accOutput[0]=event.values.clone()[0]*-1;
            accOutput[1]=event.values.clone()[1]*-1;
            accOutput[2]=event.values.clone()[2]*-1;*/
            accOutput = event.values.clone();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    private SensorEventListener gyrListener=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            gyroTimestamp = event.timestamp;
            gyroOutput = event.values.clone();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    private SensorEventListener magListener=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            magTimestamp = event.timestamp;
            magOutput = event.values.clone();

            SensorManager.getRotationMatrix(rotate, null, accOutput, magOutput);
            SensorManager.getOrientation(rotate, values);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    @Override
    public void onSensorChanged(SensorEvent event) {
        //Log.i(TAG, "onSensorChanged: ");
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
        {
          //  Log.i(TAG, "acc: count_acc="+count_acc++);
            accTimestamp = event.timestamp;
            accOutput = event.values.clone();
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
        {
           // Log.i(TAG, "gyc: count_gyc="+count_gyc++);
            gyroTimestamp = event.timestamp;
            gyroOutput = event.values.clone();

        }

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
        {

            SensorManager.getQuaternionFromVector(rotationVectorQuaternion,event.values);

            float temp;
            temp = rotationVectorQuaternion[0];

            rotationVectorQuaternion[0] = rotationVectorQuaternion[1];
            rotationVectorQuaternion[1] = rotationVectorQuaternion[2];
            rotationVectorQuaternion[2] = rotationVectorQuaternion[3];
            rotationVectorQuaternion[3] = temp;

            SensorManager.getRotationMatrixFromVector(rotationVectorMatrix,rotationVectorQuaternion);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void releaseSensor(){
        if(mSensorManager!=null) {
           // mSensorManager.unregisterListener(this);
            mSensorManager.unregisterListener(accListener);
            mSensorManager.unregisterListener(gyrListener);
            mSensorManager.unregisterListener(magListener);
        }
        if(timerSensorCapture!=null)
            timerSensorCapture.cancel();
    }
}
