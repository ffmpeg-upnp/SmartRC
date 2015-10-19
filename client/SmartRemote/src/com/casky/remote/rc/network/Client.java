package com.casky.remote.rc.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import com.casky.remote.rc.main.MainFragmentRemote;
import com.casky.remote.utils.Helper;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
/**
 * 
*    
* 项目名称：SmartRemote   
* 类名称：Client   
* 类描述：网络客户端类，每个连接都会有丄1�7个该类的实例存在   
* 创建人：shaojiansong   
* 创建时间＄1�714-8-25 下午2:18:29   
* 修改人：shaojiansong   
* 修改时间＄1�714-8-25 下午2:18:29   
* 修改备注＄1�7   
* @version    
*
 */
@SuppressLint("HandlerLeak")
public class Client{


    private static final int MOUSE_PORT_NUMBER = 5555;
    private static final int REMOTE_PORT_NUMBER = 4444;
    private static final int INPUTMETHOD_PORT_NUMBER = 3333;
    private static final int PORT = 12345;//server port

    public static final int MOUSE_MESSAGE = 0x100;
    public static final int REMOTE_MESSAGE = 0x101;
    public static final int INPUTMETHOD_MESSAGE = 0x102;
    public static final int INIT_MESSAGE = 0x103;
    public static final int INIT_UDP_MESSAGE = 0x104;

    private  String IP_ADDR = "192.168.253.3";//server IP address
    private Socket socket = null;
    private DataOutputStream out = null;
	private InetAddress srvAddressUdp = null;

	private DatagramSocket sndSocketUdp = null;
	private Handler sHandler = null;
	private HandlerThread handlerThread = null;
	private MyHandler mHandler;
	private int mHeartBeatFailedCount = 0;
	private Timer mTimer = null;
	private TimerTask mTimerTask = null;
    public Client(String ipString , Handler aHandler)
    {
    	this.IP_ADDR = ipString;
    	this.sHandler = aHandler;
    	prepare();
    }
	private void init(){
    	try {
			socket = new Socket(IP_ADDR, PORT);
			out = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    @SuppressWarnings("unused")
	private void sendTcpMsg(String str){
    	if (socket!= null && socket.isConnected()) {
        	try {
    			out.writeUTF(str);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
		}
    }
	public Handler getNetWorkHandler()
	{
		return mHandler;
	}
	private void initUdp()
	{
		try {
			srvAddressUdp = InetAddress.getByName(IP_ADDR);
			try {
				sndSocketUdp = new DatagramSocket();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (srvAddressUdp != null) {
			sHandler.sendMessage(Helper.createMessage(MainFragmentRemote.DEVICE_SET_MESSAGE, null));
		}
	}
	private void sendUdpMsg(String str, final int port){
				DatagramPacket sendPacketUdp = new DatagramPacket(str.getBytes(),str.getBytes().length,srvAddressUdp,port);
				try {
					sndSocketUdp.send(sendPacketUdp);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	public void closeClient() {
		if (socket != null) {
			try {
				socket.close();
				socket = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		if (sndSocketUdp != null) {
			sndSocketUdp.close();
			sndSocketUdp = null;
		}
		if (handlerThread != null) {
			handlerThread.getLooper().quit();
			mHandler = null;
		}
		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}
	/**
	 * 创建UI线程消息队列之外的一个消息队刄1�7
	 */
	private void prepare() {
		handlerThread = new HandlerThread("handlerThread");
		handlerThread.start();
		mHandler = new MyHandler(handlerThread.getLooper());
		//mHandler.sendMessage(Helper.createMessage(1004, null));
		mHandler.sendMessage(Helper.createMessage(INIT_UDP_MESSAGE, null));
		mTimer = new Timer();
		mTimerTask = new HeartBeatTask();
		mTimer.schedule(mTimerTask, 10*1000,10*1000);
		
	}
	/**
	 * 
	*    
	* 项目名称：SmartRemote   
	* 类名称：MyHandler   
	* 类描述：自定义的Handler，独立于UI线程中的Handler   
	* 创建人：shaojiansong   
	* 创建时间＄1�714-8-25 下午2:21:16   
	* 修改人：shaojiansong   
	* 修改时间＄1�714-8-25 下午2:21:16   
	* 修改备注＄1�7   
	* 版本＄1�7 1.0   
	*
	 */
	class MyHandler extends Handler {
		public MyHandler(Looper looper ){
		      super(looper);
		     }
		@Override
		 public void handleMessage(Message msg){
			 super.handleMessage(msg);
			 switch (msg.what){
				case MOUSE_MESSAGE:
					//sendTcpMsg((String)msg.obj);
					sendUdpMsg((String)msg.obj,MOUSE_PORT_NUMBER);
					break;
				case REMOTE_MESSAGE:
					sendUdpMsg((String)msg.obj,REMOTE_PORT_NUMBER);
					break;
				case INPUTMETHOD_MESSAGE:
					sendUdpMsg((String)msg.obj,INPUTMETHOD_PORT_NUMBER);
					break;
				case INIT_MESSAGE:
					init();
					break;
				case INIT_UDP_MESSAGE:
					initUdp();
					break;
				default:
					break;
			}
		 }
	}
	class HeartBeatTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.d("syo", "heart beat");
			try {
				if (InetAddress.getByName(IP_ADDR).isReachable(5000)) {
					mHeartBeatFailedCount = 0;
				}else {
					mHeartBeatFailedCount++;
					if (mHeartBeatFailedCount >= 3) {
						sHandler.sendMessage(Helper.createMessage(MainFragmentRemote.SEART_HEART_BEAT_MESSAGE, null));	
					}
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
