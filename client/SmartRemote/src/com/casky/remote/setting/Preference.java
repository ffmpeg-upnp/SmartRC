package com.casky.remote.setting;

import android.content.SharedPreferences;
import android.util.Log;

/**
 * preference interface, this is used to store the status variable which need to keep even if APK has
 * been closed.<br>
 * the style is [key, value]
 * */
public class Preference {
	private static final String TAG = "Preference";
	//static SharedPreferences preferences;
	static SharedPreferences.Editor editor;

	static void set_preferences( SharedPreferences preferences,String pre_name, boolean sw){
		//preferences = at.getSharedPreferences("SmartRemote",Context.MODE_PRIVATE);
		Log.v(TAG, "set_preferences sw is :"+sw);
		editor = preferences.edit();
		editor.putBoolean(pre_name, sw);
		editor.commit();
	}
	
	static void set_preferences( SharedPreferences preferences,String pre_name,String ipaddr){
		//preferences = at.getSharedPreferences("SmartRemote",Context.MODE_PRIVATE);		
		editor = preferences.edit();
		editor.putString(pre_name, ipaddr);
		editor.commit();
	}
	
	public static boolean get_preferences(SharedPreferences preferences,String pre_name,boolean def){
		//preferences = at.getSharedPreferences("SmartRemote",Context.MODE_PRIVATE);
		boolean sw = preferences.getBoolean(pre_name, def);
		Log.v(TAG, "get_preferences sw is :"+sw);
		return sw;		
	}
	
	public static String get_preferences(SharedPreferences preferences, String pre_name,String def){
		//preferences = at.getSharedPreferences("SmartRemote",Context.MODE_PRIVATE);
		return preferences.getString(pre_name, def);		
	}
}
