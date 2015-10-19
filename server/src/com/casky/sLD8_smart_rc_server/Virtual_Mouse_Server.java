package com.casky.sLD8_smart_rc_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class Virtual_Mouse_Server extends Service
{
	static {
		Log.d("syo", "Library path is " + System.getProperty("java.library.path"));
		System.loadLibrary("virtualmouse");

	}
	public static native String stringFromJNI();
	public static native String closeDevice();
	public static native void sendMsgToJni(int act, int x, int y);
	private String tagString = "syo";
	private DatagramSocket mMouseSocket = null;
	private static final int MOUSE_PORT_NUMBER = 5555;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	public int onStartCommand(Intent intent, int flags, int startId){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
						initSocket(MOUSE_PORT_NUMBER);
			            while (true) {    
			            	processData();
					}
				}
			}).start();
		return Service.START_STICKY;
			            }  
	private void initSocket(int mPort){
		try {
			mMouseSocket = new DatagramSocket(mPort);
		} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	private void processData(){
		byte[] mData=new byte[50];
		DatagramPacket mPacket=new DatagramPacket(mData,mData.length);
					try {
			if (!mMouseSocket.isClosed()) {
					mMouseSocket.receive(mPacket);
					String mString = new String(mPacket.getData(), 0, mPacket.getLength());
					String[] strArray = mString.trim().split("\\#");
					sendMsgToJni(Integer.parseInt(strArray[0]),Integer.parseInt(strArray[1]),Integer.parseInt(strArray[2]));
						/*
						if(Integer.parseInt(strArray[0]) == 0){
							sendMsgToJni(0,Integer.parseInt(strArray[1]),Integer.parseInt(strArray[2]));
						}
						else {
							sendMsgToJni(Integer.parseInt(strArray[0]),0,0);
					}*/
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
		}
	}
}
