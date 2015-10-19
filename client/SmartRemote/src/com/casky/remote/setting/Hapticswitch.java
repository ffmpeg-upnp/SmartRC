package com.casky.remote.setting;

import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
/**
 * interface to control the status of haptic switch
 * */
public class Hapticswitch {
	private static final String TAG = "Hapticswitch";
	private static boolean onoff = false; //haptic on off flag
	
	public Hapticswitch(boolean ponoff)
	{
		onoff = ponoff;		
	}
	
	public static void set_hs_onoff(boolean sonoff)
	{
		onoff = sonoff;		
	}
	
	public static boolean get_hs_onoff()
	{		
		Log.v(TAG, "onoff :" + onoff);
		return onoff;
	}
	/**
	 * set the view 's haptic status according to haptic flag
	 * */
	public static void configHaptics(View v)
	{
		if(get_hs_onoff())
		{
			v.setHapticFeedbackEnabled(true);
			v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
		}	
		else{
			v.setHapticFeedbackEnabled(false);			
		}
	}
}
