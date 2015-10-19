package com.casky.dlna.control;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.casky.dlna.server.MediaServer;
import com.casky.dlna.utils.Utils;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
*    
* Project Name: mart_DLNA   
* Class Name: DlnaManager   
* Description
* Creator: shaojiansong   
* Create Date: 20114-9-23 PM 1:54:47   
* Modifier: shaojiansong   
* Modification Date: 2014-9-23 PM 1:54:47   
* Version: 1.0   
*
 */
public class DlnaManager{
	private final static String TAG = "DlnaManager";
	private Context appContext;
	private AndroidUpnpService mAndroidUpnpService;
	private MediaServer mediaServer;
	private ArrayList<HashMap<String, DeviceDisplay>> mArrayList = new ArrayList<HashMap<String,DeviceDisplay>>();
	private MyRegistryListener tvRegistryListener = new MyRegistryListener();
	public final static String RENDER_SERVICE = "RenderingControl";
	private static DlnaManager mDlnaManager;
	private DlnaServiceManager mDlnaServiceManager;
	
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			mAndroidUpnpService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			// TODO Auto-generated method stub
			Log.d("syo", "onServiceConnected");
			mAndroidUpnpService = (AndroidUpnpService)arg1;
			mAndroidUpnpService.getRegistry().addListener(tvRegistryListener);
			
			try {
				mediaServer = new MediaServer(Inet4Address.getByName(Utils.getLocalIpAddress()),appContext);
				try {
					mediaServer.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ValidationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mAndroidUpnpService.getRegistry().addDevice(mediaServer.getDevice());
			mAndroidUpnpService.getControlPoint().search();
			mDlnaServiceManager = new DlnaServiceManager(mAndroidUpnpService, "NO METADATA");
		}
		
	};
	
	private DlnaManager(Context mContext) {
		// TODO Auto-generated constructor stub
		this.appContext = mContext;
		//Intent intent = new Intent(appContext, AndroidUpnpServiceImpl.class);
		Intent intent = new Intent(appContext, MyUpnpService.class);
		appContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	public static DlnaManager getDlnaManagerInstance(Context mContext){
		if (mDlnaManager == null) {
			mDlnaManager = new DlnaManager(mContext);
		}
		return mDlnaManager;
	}
	
	public class MyRegistryListener implements RegistryListener{

		@Override
		public void afterShutdown() {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeShutdown(Registry arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void localDeviceAdded(Registry arg0, LocalDevice arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void localDeviceRemoved(Registry arg0, LocalDevice arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void remoteDeviceAdded(Registry arg0, RemoteDevice arg1) {
			// TODO Auto-generated method stub
			addDevice(arg1);
		}

		@Override
		public void remoteDeviceDiscoveryFailed(Registry arg0,
				RemoteDevice arg1, Exception arg2) {
			// TODO Auto-generated method stub
			//removeDevice(arg1);
		}

		@Override
		public void remoteDeviceDiscoveryStarted(Registry arg0,
				RemoteDevice arg1) {
			// TODO Auto-generated method stub
			//addDevice(arg1);
		}

		@Override
		public void remoteDeviceRemoved(Registry arg0, RemoteDevice arg1) {
			// TODO Auto-generated method stub
			removeDevice(arg1);
		}

		@Override
		public void remoteDeviceUpdated(Registry arg0, RemoteDevice arg1) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public void close(){
		if (!mArrayList.isEmpty()) {
			mArrayList.clear();
		}
		if (mAndroidUpnpService != null) {
			mAndroidUpnpService.getRegistry().removeListener(tvRegistryListener);
		}
		mediaServer.close();
	}
	public ArrayList<HashMap<String, DeviceDisplay>> getArrayList(){
		if (mArrayList.isEmpty()) {
			Log.d("syo", "ArrayList isEmpty");
		}
		return mArrayList;
	}

	private void addDevice(Device device){
		HashMap<String, DeviceDisplay> mHashMap = new HashMap<String, DeviceDisplay>();
		if (device.findService(new UDAServiceType(RENDER_SERVICE)) != null) {
			mHashMap.put("Device", new DeviceDisplay(device));
			if (!mArrayList.contains(mHashMap)) {
				mArrayList.add(mHashMap);
			}	
		}
	}
	private void removeDevice(Device device) {
		HashMap<String, DeviceDisplay> mHashMap = new HashMap<String, DeviceDisplay>();
		mHashMap.put("Device", new DeviceDisplay(device));
		mArrayList.remove(mHashMap);
	}
	
	public AndroidUpnpService getUpnpService() {
		// TODO Auto-generated method stub
		return mAndroidUpnpService;
	}
	public DlnaServiceManager getDlnaServiceManager(){
		return mDlnaServiceManager;
	}
}
