package com.casky.sLD8_smart_rc_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.app.Instrumentation;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.app.TksControl;

public class Smart_RC_Server extends Service {

	private boolean runFlag = true;
	private static final String Tag = "SmartRemote_Service";
	private TksControl tkscontrol = new TksControl();
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.detectDiskReads()
		.detectDiskWrites()
		.detectNetwork()
		.penaltyLog()
		.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		.detectLeakedSqlLiteObjects()
		.penaltyLog()
		.penaltyDeath()
		.build());
		Log.d(Tag,"server started...");
		super.onCreate();
		Log.d(Tag,"server started...");
		tkscontrol.loadlibrary();
		new ServerThread().start();
	}
	
	private class ServerThread extends Thread{
		DatagramSocket tvSocket;
		DatagramPacket recData;
		byte[] message = new byte[1024];
		//byte[] inBuff = new byte[1024]; 
		String data = null;
		@Override
		public void run() {
			Log.d(Tag,"server thread started...");
			try {
				tvSocket = new DatagramSocket(4444);
				recData = new DatagramPacket(message, message.length);
				Instrumentation inst = new Instrumentation();
				boolean sendToTKS = true;
				
				try {  
					while (!tvSocket.isClosed() && runFlag) {
						for (int i=0;i<message.length;i++){
							message[i]=0;
						}
						tvSocket.receive(recData);
					//	data = recData.getData().toString();
					//	System.out.println(inBuff == recData.getData());  
					    data = new String(recData.getData()); 
						data = data.trim();
						Log.i("SmartRemote_PF"," data = " + data);
						switch(Integer.parseInt(data)){
							case TksControl.TREQ_RC_KEY_1:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_1);
								break;	
							case TksControl.TREQ_RC_KEY_2:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_2);
								break;
							case TksControl.TREQ_RC_KEY_3:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_3);
								break;
							case TksControl.TREQ_RC_KEY_4:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_4);
								break;
							case TksControl.TREQ_RC_KEY_5:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_5);
								break;
							case TksControl.TREQ_RC_KEY_6:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_6);
								break;
							case TksControl.TREQ_RC_KEY_7:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_7);
								break;
							case TksControl.TREQ_RC_KEY_8:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_8);
								break;
							case TksControl.TREQ_RC_KEY_9:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_9);
								break;
							case TksControl.TREQ_RC_KEY_0:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_0);
								break;
							case TksControl.TREQ_RC_KEY_mute:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUMEMUTE);
								break;
							case TksControl.TREQ_RC_KEY_OK:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
								break;
							case TksControl.TREQ_RC_KEY_Up:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
								break;
							case TksControl.TREQ_RC_KEY_Down:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
								break;
							case TksControl.TREQ_RC_KEY_Left:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_LEFT);
								break;
							case TksControl.TREQ_RC_KEY_Right:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
								break;
							case TksControl.TREQ_RC_KEY_VolumeUp:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUMEUP);
								break;
							case TksControl.TREQ_RC_KEY_VolumeDown:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUMEDOWN);
								break;
							case TksControl.TREQ_RC_KEY_dot:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_PERIOD);
								break;
							case TksControl.TREQ_RC_KEY_Back:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
								break;
							//not in use
							case TksControl.TREQ_RC_KEY_Option:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_OPTION);
								break;
							case TksControl.TREQ_RC_KEY_Channelup:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_PAGE_UP);
								break;
							case TksControl.TREQ_RC_KEY_Channeldown:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_PAGE_DOWN);
								break;
							case TksControl.TREQ_VOICE_StartSpeak:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_F9);
								break;
							case TksControl.TREQ_RC_KEY_SmartTV:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_F10);
								//inst.sendKeyDownUpSync(KeyEvent.KEYCODE_F10);
								break;
							case TksControl.TREQ_RC_KEY_Home:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
								break;
							case TksControl.TREQ_RC_KEY_TV:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_TV);
								break;
							case TksControl.TREQ_RC_KEY_info:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_INFO);
								break;
							case TksControl.TREQ_RC_KEY_Source:
								inst.sendKeyDownUpSync(KeyEvent.KEYCODE_SOURCE);
								break;
							case TksControl.TREQ_RC_KEY_PowerOFF:
								tkscontrol.SendCmd(Integer.parseInt(data));
								break;
							default:
								Log.e(Tag,"KEYCODE MISSING");
								break;
						}
						
						Log.i(Tag,data + " pressed");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d(Tag,"IOException");
				}
			} catch (SocketException e) {
				// TODO Auto-generated catch block
//				Toast.makeText(this, "Local Socket Error.", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
				Log.d(Tag,"SocketException");
			}finally{
				tvSocket.close();
			}
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
