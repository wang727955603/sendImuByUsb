package presenter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.google.gson.Gson;

import sensor.data.GpsData;
import sensor.data.MessageToPC;
import sensor.monitor.ImuSensor;
import sensor.monitor.gpsSensor.HalopayLocation;
import sensor.monitor.gpsSensor.HalopayLocationListener;
import sensor.monitor.gpsSensor.LocationAPI;
import thread.LockDataQueue;
import thread.ServerThread;

/**
 * @author Created by CM-WANGMIN on 2017/8/18.
 */

public class TransferData {
    private static final String TAG = "TransferData";

    private Context context;


    private LocationAPI locationAPI;
    private GpsData gpsData;

    private ImuSensor imuSensor;
    private ServerThread serverThread;

    private boolean isEnqueue = false;

    private viewImpl view;

    private int count = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    isEnqueue = false;
                    view.showState("客户端断开连接");
                    break;
                case 1002:
                    isEnqueue = true;
                    break;
                case 1003:
                    if (isEnqueue) {
                        MessageToPC messageToPC = (MessageToPC) msg.obj;
                        if (gpsData != null) {
                            messageToPC.setGps(gpsData);
                            gpsData=null;
                        }

                        Gson gson = new Gson();
                        String msgJson = gson.toJson(messageToPC);
                        view.showMessage(msgJson);
                        view.showState("客户端已连接");
                    if(LockDataQueue.queue.size()==20){
                        LockDataQueue.queue.poll();
                    }
                        LockDataQueue.queue.offer(msgJson);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public Handler getHandler() {
        return handler;
    }

    public TransferData(Context context, viewImpl view) {
        this.context = context;
        this.view=view;
    }

    public void start() {
        startGps();
        startImu();
        startServerThread();
    }

    private void startGps() {
        locationAPI = new LocationAPI(context.getApplicationContext());
        locationAPI.getChangeCurLocation(mListener);
    }

    private void startImu() {
        imuSensor = new ImuSensor(context, handler);
        imuSensor.startSensor();
    }

    private void startServerThread() {
        serverThread = new ServerThread(handler, 2009);
        serverThread.start();
    }

    public void stop() {
        LockDataQueue.queue.clear();
        if (locationAPI != null)
            locationAPI.stopGpsListener();
        if (imuSensor != null)
            imuSensor.releaseSensor();
        if (serverThread != null)
            serverThread.closeServerThread();
    }

    private HalopayLocationListener mListener = new HalopayLocationListener() {

        @Override
        public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String paramString) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String paramString) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationFail(String paramString) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationChanged(final HalopayLocation paramYongcheLocation) {
            // TODO Auto-generated method stub
            long gpsTimestamp= SystemClock.elapsedRealtimeNanos();
            Log.i(TAG, "onLocationChanged: gpsTimestamp="+gpsTimestamp);
            gpsData = new GpsData(gpsTimestamp,paramYongcheLocation.getLatitude(), paramYongcheLocation.getLongitude());
            //transferData.setGps(gpsData);
        }
    };
}
