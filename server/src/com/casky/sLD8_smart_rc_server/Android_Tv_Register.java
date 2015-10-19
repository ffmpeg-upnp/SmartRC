package com.casky.sLD8_smart_rc_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class Android_Tv_Register extends Service
{
	
	private String tagString = "syo";
	private static int TV_LISTEN_PORT = 9000;
	private static int TV_BROADCAST_PORT = 8989;
	private DatagramSocket resSocket = null;
	private DatagramSocket lisSocket = null;
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
						setUpSocket(TV_BROADCAST_PORT);
						while(true){
							try {
								Log.d(tagString, "start listen broadcast!");
								receiveIP();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
				 
					}
				}).start();
		return Service.START_STICKY;
	}
	private void setUpSocket(int mBroadcastPort) {
		try {
			lisSocket = new DatagramSocket(mBroadcastPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			resSocket = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	private void responseToClient(String mIpString){
		byte b[]="act".getBytes();
		DatagramPacket dgPacket = null;
		try {
				dgPacket = new DatagramPacket(b,b.length,InetAddress.getByName(mIpString),TV_LISTEN_PORT);
			} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
						e.printStackTrace();
				}
			try {
			if (!resSocket.isClosed()) {
					resSocket.send(dgPacket);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	private void receiveIP() {
		byte[] mData=new byte[20];
		DatagramPacket mPacket=new DatagramPacket(mData,mData.length);
		try {
			if (!lisSocket.isClosed()) {
				lisSocket.receive(mPacket);
				String clientIpString = mPacket.getAddress().getHostAddress();
				Log.d(tagString, "received client ip " + clientIpString);
				responseToClient(clientIpString);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
