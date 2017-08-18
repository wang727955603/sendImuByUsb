package threadService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
/**
 * Created by CM-WANGMIN on 2017/5/4.
 */

public class WriteThread implements Runnable {
    private static final String TAG = "socket:write";

    public static Socket socket;
    private String sendData;
    private int length;
    public static boolean mWriteRunning = false;
    SocketThread thread;
    int i=0;

    BufferedReader br = null;
    public static OutputStream os = null;

    public WriteThread(Socket socket) {
        this.socket = socket;
        mWriteRunning = true;
    }

    @Override
    public void run() {
        try {
            os = socket.getOutputStream();
            while (mWriteRunning) {
               /* if (isServerClose()) {
                    Log.i("sockete", "socket write closed");
                    mWriteRunning = false;
                } else {*/
                    sendData = LockDataQueue.queue.poll();
//                Log.i(TAG, "write"+i++);
                    if (socket.isOutputShutdown()) {
                        socket.getKeepAlive();
                    } else if (!"".equals(sendData) && sendData != null && sendData.length() > 0) {
                       /* byte[] bytes = DataUtils.intToMinBytes(sendData.length());
                        Log.i(TAG, sendData + ",length=" + String.valueOf(bytes.length) + ", ");
                        os.write(bytes);*/
                        os.write(sendData.getBytes());
                        os.flush();
                    }
//                }
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
    public static Boolean isServerClose(){
        try{
            socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        }catch(Exception se){
            return true;
        }
    }
}
