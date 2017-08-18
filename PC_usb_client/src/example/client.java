package example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import threadService.SocketThread;

public class client {
	public static void main(String[] args) throws Exception, IOException {
		
		//window 环境运行adb指令，需要配置adb系统环境变量
		Runtime.getRuntime().exec("cmd /c adb shell am broadcast -a NotifyServiceStop");
		Thread.sleep(3000);
		Runtime.getRuntime().exec("cmd /c adb forward tcp:8000 tcp:2009");
		Thread.sleep(3000);
		Runtime.getRuntime().exec("cmd /c adb shell am broadcast -a NotifyServiceStart");

		System.out.println("请输入命令：");
		Scanner sc = new Scanner(System.in);
		String msg = sc.nextLine();
		SocketThread socketThread = new SocketThread();
		socketThread.start();

	}

}
