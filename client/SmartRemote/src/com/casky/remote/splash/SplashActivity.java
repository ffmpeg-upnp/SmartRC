package com.casky.remote.splash;

import com.casky.dlna.picture.PicturePreloader;
import com.casky.main.slidingmenu.MainActivity;
import com.casky.remote.rc.main.ExitApplication;
import com.casky.remote.rc.speech.SpeechManager;
import com.casky.remote.setting.Preference;
import com.casky.smartremote.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;


public class SplashActivity extends Activity {
	
	private  int SPLASH_DISPLAY_LENGHT = 1000; // delay 1s
	public static String LASTIP = "com.casky.lastip";
	public static String HAPTICSWITCH = "com.casky.hapticswitch";
	private String mLastIpString = "N/A";
	//private boolean mOnOff = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_page);
		new Handler().postDelayed(new Runnable() {
			public void run() {
					Intent mainIntent = new Intent(SplashActivity.this,
							MainActivity.class);
					mainIntent.putExtra(LASTIP, mLastIpString);
					//mainIntent.putExtra(HAPTICSWITCH, mOnOff);
					SplashActivity.this.startActivity(mainIntent);
					SplashActivity.this.finish();
			}	
		}, SPLASH_DISPLAY_LENGHT);
        preLoadData();
	}
    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  
        	
        	ExitApplication.getInstance().exit(); 
            return true;  
        }  
        return true;  
    }
    private void preLoadData() {
    	mLastIpString = Preference.get_preferences(getSharedPreferences("SmartRemote",Context.MODE_PRIVATE), "lastIp", "N/A");
        SpeechManager.preLoadGrammarText(getApplicationContext());
        PicturePreloader.preloadPicture(getApplicationContext());

    }
}
