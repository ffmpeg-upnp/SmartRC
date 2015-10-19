package com.casky.remote.utils;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
/**
*    
* 项目名称：SmartRemote   
* 类名称：Helper   
* 类描述：工具类，用于消息创建和网络信息的解析等  
* 创建人：shaojiansong   
* 创建时间：2014-8-25 下午2:00:21   
* 修改人：shaojiansong   
* 修改时间：2014-8-25 下午2:00:21   
* 修改备注：   
* 版本： 1.0   
*
 */
@SuppressWarnings("deprecation")
public class Helper {
	private static String Tag = "Helper";
	private static Message message = new Message();
	
	public static Message createMessage(int what, Object object) {
		Message message = new Message();
		message.what = what;
		message.obj = object;
		return message;

	}
	
	public static String getHostName(String ip){
		String hostName = "Unknown";
		
		try {
			InetAddress add= InetAddress.getByName(ip); 
			hostName=add.getHostName(); 
            
			Log.d(Tag, "host name is " + hostName);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hostName;
	}
	
	public static String intToIp(int i) {
	     return (i & 0xFF ) + "." +
	     ((i >> 8 ) & 0xFF) + "." +
	     ((i >> 16 ) & 0xFF) + "." +
	     ( i >> 24 & 0xFF) ;
	 }
    public static String getLocalIpAddress() {
    	  try 
    	  {
    	   String ipv4;
    	   ArrayList<NetworkInterface> mylist = Collections.list(NetworkInterface.getNetworkInterfaces());

    	   for (NetworkInterface ni : mylist) 
    	   {
    	    ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
    	    for (InetAddress address : ialist) 
    	    {
    	     if (!address.isLoopbackAddress()&& InetAddressUtils.isIPv4Address(ipv4 = address.getHostAddress())) 
    	     {
    	      return ipv4;
    	     }
    	    }
    	   }
    	  } 
    	  catch (SocketException ex) 
    	  {
    		  Log.e("Can not get local ip address!", ex.toString());
    	  }
    	  return null;
    }
    @SuppressWarnings("static-access")
	public static boolean checkWifiStatus(Context mContext) {
    	ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    	if (networkInfo == null) {
			return false;
		}else {
			State wifiState = connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI).getState();
			if (wifiState == State.CONNECTED) {
				return true;
			}else {
				return false;
			}
		}
    	
    }
    public static String getDhcpIpString(Context mContext) {
    	WifiManager mWifiManager;
    	String broadcastIp = null;
    	mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    	if(mWifiManager.isWifiEnabled()) {
    		DhcpInfo myDhcpInfo = mWifiManager.getDhcpInfo();
        	if (myDhcpInfo == null) {
        		Toast.makeText(mContext, "can not get dhcp info", Toast.LENGTH_SHORT).show();
        		return null;
        	}else {
            	try {
            		broadcastIp = getBroadcastAddress(myDhcpInfo).getHostAddress();
    			} catch (UnknownHostException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    				return null;
    			}
			}
        	return broadcastIp;
    	}
    	return null;
    }
	private static InetAddress getBroadcastAddress(DhcpInfo mDhcpInfo) throws UnknownHostException{
    	int broadcast = (mDhcpInfo.ipAddress & mDhcpInfo.netmask)| ~mDhcpInfo.netmask;
    	byte[] quads = new byte[4];
    	try {
			for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return InetAddress.getByAddress(quads);
    }
	public static int dip2px(Context context, float dpValue) {
		  final float scale = context.getResources().getDisplayMetrics().density;
		  return (int) (dpValue * scale + 0.5f);
		 } 
	
	/**
	 * 读取语法文件。
	 * @return
	 */
	public static String readFile(Context context,String file,String code)
	{
		int len = 0;
		byte []buf = null;
		String grammar = "";
		try {
			InputStream in = context.getAssets().open(file);			
			len  = in.available();
			buf = new byte[len];
			in.read(buf, 0, len);
			
			grammar = new String(buf,code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return grammar;
	}
}