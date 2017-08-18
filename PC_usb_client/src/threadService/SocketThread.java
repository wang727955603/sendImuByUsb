package threadService;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by CM-WANGMIN on 2017/5/4.
 */

public class SocketThread extends Thread {

    private static final String TAG = "socket";
    private final static String IPADRESS = "127.0.0.1";
    private final static int PORT = 8000;
    public Socket socket;
    private WriteThread writeThread;
    private ReadThread readThread;
    long i=0;
    Timer timer;

    public SocketThread() {
    }

    public void run() {
       connect();
    }

    private void connect(){
        try {
            socket = new Socket(IPADRESS, PORT);
            writeThread = new WriteThread(socket);
            readThread = new ReadThread(socket);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            System.out.println("e1="+e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("e2="+e.toString());
            if(timer!=null){
                timer.cancel();
                timer=null;
            }
            return;
        }
        new Thread(writeThread).start();
        new Thread(readThread).start();
    }

    public void stopThread() {
        if (readThread != null)
            readThread.mReadRunning = false;
        if (writeThread != null)
            writeThread.mWriteRunning = false;
        if (!LockDataQueue.queue.isEmpty()) {
            LockDataQueue.queue.clear();
        }
        try {
            if(socket!=null)
                socket.close();
        } catch (IOException e) {
        }

    }
  
    public Boolean isServerClose(){
        try{
            socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        }catch(Exception se){
            stopThread();
            return true;
        }
    }

}