package threadService;


import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by CM-WANGMIN on 2017/5/4.
 */

public class ReadThread implements Runnable {
    private static final String TAG = "socket:read";
    public static Socket socket;
    public static boolean mReadRunning = false;
    SocketThread thread;

    static InputStream is = null;


    public ReadThread(Socket socket) {
        this.socket = socket;
        mReadRunning = true;
    }

    @Override
    public void run() {
        try {
            is = socket.getInputStream();
            StringBuffer sb = new StringBuffer();
            int ss = 0;
            while (mReadRunning) {
                /*if(isServerClose()){
                    Log.i("sockete","socket read closed");
                    mReadRunning=false;
                }else{*/
                byte[] buff = new byte[4]; // buff用于存放循环读取的临时数据
                int rc = 0;
                if ((rc = is.read(buff, 0, 4)) > 0) {
                    ss = DataUtils.bytesToInt(buff);
                }
                byte[] databuffer = new byte[ss];
                rc=is.read(databuffer, 0, ss);
                if(rc==-1){
                	mReadRunning=false;
                	break;
                }
                System.out.println("接收服务器的信息：" + ss + ",s=" + new String(databuffer));
//                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                is.close();
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
