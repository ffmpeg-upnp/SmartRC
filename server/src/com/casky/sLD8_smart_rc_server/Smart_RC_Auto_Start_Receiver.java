package com.casky.sLD8_smart_rc_server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Smart_RC_Auto_Start_Receiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		 //TODO Auto-generated method stub
		if (arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent srvIntent = new Intent(arg0, Smart_RC_Server.class);
			srvIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			srvIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Intent regIntent = new Intent(arg0, Android_Tv_Register.class);
			regIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			regIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			regIntent.addCategory("android.intent.category.LAUNCHER");
			//MyActivity category defined in AndroidManifest.xml
			srvIntent.addCategory("android.intent.category.LAUNCHER");
			Intent vIntent = new Intent(arg0, Virtual_Mouse_Server.class);
			vIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			vIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			vIntent.addCategory("android.intent.category.LAUNCHER");
			arg0.startService(srvIntent);
			arg0.startService(regIntent);
			arg0.startService(vIntent);
			Log.d("BootReceiver", "system boot completed"); 
			//arg0.startService(new Intent("com.casky.sLD8_smart_rc_server.action.Android_Tv_Register"));
//			Intent actIntent = new Intent(arg0, Smart_RC_Server_Test_Panel.class);
//			actIntent.setAction("android.intent.action.MAIN"); //MyActivity action defined in AndroidManifest.xml
//			actIntent.addCategory("android.intent.category.LAUNCHER");//MyActivity category defined in AndroidManifest.xml
//			actIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //If activity is not launched in Activity environment, this flag is mandatory to set
//       	arg0.startActivity(actIntent);
			
			Log.d("AUTO_START_SUCCESS", arg1.getAction());
		}else{
			Log.d("AUTO_START_FAIL", arg1.getAction());
		}
	}

}
