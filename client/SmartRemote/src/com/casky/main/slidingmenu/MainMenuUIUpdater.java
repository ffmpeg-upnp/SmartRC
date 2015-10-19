package com.casky.main.slidingmenu;

import com.casky.remote.utils.Helper;
import android.os.Handler;

public class MainMenuUIUpdater {
	static private Handler mHandler;
    public static final int TOGGLE = MainActivity.SLIDING_MENU_TOGGLE;
	
    public void HandlerRegister(final Handler DataHandler)
    {
    	MainMenuUIUpdater.mHandler = DataHandler;
    }
	
	public void sendMessage(int what) {

		if(MainMenuUIUpdater.mHandler != null){
			
			MainMenuUIUpdater.mHandler.sendMessage(Helper.createMessage(what, null));
		}
	}
}
