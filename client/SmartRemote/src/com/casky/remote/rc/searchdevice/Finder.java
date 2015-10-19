package com.casky.remote.rc.searchdevice;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import com.casky.remote.rc.kbmouse.FragmentMouse;
import com.casky.remote.rc.main.MainFragmentRemote;
import com.casky.remote.rc.network.PingResult;
import com.casky.remote.utils.Helper;

import android.os.Build;
import android.os.Handler;
import android.util.Log;
/**
 * 
*    
* 项目名称：SmartRemote   
* 类名称：Finder   
* 类描述： 用户查找Tv设备，向设备网段内的广播地址发�1�7�广播，然后监听Tv设备的反馄1�7  
* 创建人：shaojiansong   
* 创建时间＄1�714-8-25 下午2:06:40   
* 修改人：shaojiansong   
* 修改时间＄1�714-8-25 下午2:06:40   
* 修改备注＄1�7   
* 版本＄1�7 1.0   
*
 */
public class Finder {
	private HashSet<String>mHashSet = new HashSet<String>();
	InetAddress inetAddress=null; 
    String broadcastIp = "";
	private String tagString = "finder";
	private boolean multicastActive = false;
	private boolean listenerActive = false;
	private TimerTask timerTask = null;
	private Timer timer = null;
	private DatagramSocket dgSocket = null;
	private TvListener mTvListener = null;
	private static int TV_LISTEN_PORT = 9000;
	private static int TV_BROADCAST_PORT = 8989;
	
    public Finder(String hostIp)
    {
    	this.broadcastIp = hostIp;
    }
    
    public void startMulticast() {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				multicastActive = true;
				try {
					dgSocket = new DatagramSocket();
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while(multicastActive){
					try {
						broadcastIP(TV_BROADCAST_PORT);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
    
    public void startListener(final Handler DataHandler) {
    	mTvListener = new TvListener(DataHandler,TV_LISTEN_PORT);
    	startTimeOutListener(DataHandler);
    }
    
    private void stopMulticast() {
    	multicastActive = false;
		if (dgSocket != null) {
			dgSocket.close();
			dgSocket = null;
		}
    	Log.d(tagString,"close multicastSocket");
	}
    
    private void stopTimerTask()
    {
		if (timerTask != null) {
			timerTask.cancel();
		}
		if (timer != null) {
			timer.cancel();
		}
    }
    
    public HashSet<String> getClient()
    {
    	return this.mHashSet;
    }
    
    public static String getHostName(String defValue) {
        try {
            Method getString = Build.class.getDeclaredMethod("getString", String.class);
            getString.setAccessible(true);
            return getString.invoke(null, "net.hostname").toString();
        } catch (Exception ex) {
            return defValue;
        }
    }
    
    private void broadcastIP(int mBroadcastPort){

			byte b[]="query".getBytes();
			DatagramPacket dgPacket = null;
			try {
				dgPacket = new DatagramPacket(b,b.length,InetAddress.getByName(broadcastIp),mBroadcastPort);
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				if (dgSocket != null) {
					dgSocket.send(dgPacket);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.d(tagString,"send message is ok.");
	}
    
    private void startTimeOutListener(final Handler mainHandler)
    {
		timerTask = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
    
					if(mHashSet.size() > 0)
					{
						mainHandler.sendMessage(Helper.createMessage(MainFragmentRemote.DEVICE_SEARCH_COMPELET_MESSAGE, null));
					}
					else
					{
						mainHandler.sendMessage(Helper.createMessage(MainFragmentRemote.NODEVICE_FOUND_MESSAGE, null));
					}
					if (timerTask != null) {
						timerTask.cancel();
						closeResource();
						timerTask = null;
					}

			}
		};
		timer = new Timer();
		timer.schedule(timerTask,3000, 1);
    }
    
    private void closeResource() {
    	stopMulticast();
    	stopTimerTask();
    	if (mTvListener != null) {
			mTvListener.stopTvListener();
		}
	}
    
    private class TvListener implements Runnable{
    	private Handler mHandler;
    	private int mPort;
    	private DatagramSocket mListenerSocket = null;
    	public TvListener(final Handler DataHandler, int udpPort) {
    		this.mHandler = DataHandler;
    		this.mPort = udpPort;
    		new Thread(this).start();
    	}
    	
    	public void stopTvListener()
    	{
    		listenerActive = false;
    		if(mListenerSocket != null)
    		{
    			mListenerSocket.close();
    			mListenerSocket = null;
    		}
    	}
    	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				mListenerSocket = new DatagramSocket(this.mPort);
				byte[] data=new byte[1024];
				DatagramPacket packet=new DatagramPacket(data,data.length);
				listenerActive = true;
				try {
					while(listenerActive)
					{
						if (mListenerSocket != null) {
							mListenerSocket.receive(packet);
							String serverIpString = packet.getAddress().getHostAddress();
							Log.d(tagString,"got a tv ip is " + serverIpString);
							if(!mHashSet.contains(serverIpString)) {
								 mHashSet.add(serverIpString);
			                	 String serverName = getHostName(serverIpString);
			                	 PingResult PRInfo = new PingResult(serverIpString,serverName);
			                	 this.mHandler.sendMessage(Helper.createMessage(MainFragmentRemote.TV_IP_MESSAGE, PRInfo));
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
}
