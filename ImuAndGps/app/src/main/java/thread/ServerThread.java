package thread;

import android.os.Handler;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import thread.utils.DataUtils;
import thread.utils.SocketStatus;

public class ServerThread extends Thread {
    private static final String TAG = "ServerThread";

    private Handler handler;
    private int port;

    private boolean isKeepSending=false;
    private boolean isLoop;
    ServerSocket serverSocket = null;

    public ServerThread(Handler handler, int port) {
        this.handler = handler;
        this.port = port;
        isLoop = true;
    }


    public void setIsLoop(boolean isLoop) {
        this.isLoop = isLoop;
    }

    @Override
    public void run() {
        Log.i(TAG, "running");
        try {
            serverSocket = new ServerSocket(port);//2009
            Log.i(TAG, "create");

            while (isLoop) {
                Socket socket = serverSocket.accept();
                Log.i(TAG, "accept  ip=" + socket.getInetAddress());
                isKeepSending=true;
                if(handler!=null)
                    handler.sendEmptyMessage(SocketStatus.CONNECT);
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                try {
                    while(isKeepSending){
                        if (!LockDataQueue.queue.isEmpty()) {
                            String sendData = LockDataQueue.queue.poll();
                            if (!"".equals(sendData) && sendData != null && sendData.length() > 0) {
                                byte[] bytes = DataUtils.intToMinBytes(sendData.length());
                                Log.i(TAG, sendData + ",length=" + String.valueOf(sendData.length()));
                                outputStream.write(bytes);
                                outputStream.write(sendData.getBytes("utf-8"));
                                outputStream.flush();
                            }
                        }
                    }
                }catch (IOException e){
                    Log.i(TAG, "run: 1 IOException="+e.toString());
                }finally {
                    Log.i(TAG, "run: close soceket");
                    stopSend();
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "error");
        } finally {
            Log.d(TAG, "destory");

            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void closeServerThread(){
        stopSend();
        isLoop=false;
        if(serverSocket!=null&&!serverSocket.isClosed())
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public void stopSend(){
        if(handler!=null)
            handler.sendEmptyMessage(SocketStatus.DISCONNECT);
        isKeepSending=false;
        LockDataQueue.queue.clear();
    }
}