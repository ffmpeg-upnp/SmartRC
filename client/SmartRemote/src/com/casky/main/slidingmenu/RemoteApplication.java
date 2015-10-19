package com.casky.main.slidingmenu;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import com.casky.dlna.control.DlnaManager;
import com.casky.dlna.music.MusicService;
import com.casky.dlna.music.MusicService.LocalBinder;
import com.casky.dlna.picture.PictureSelectionManager;
import com.casky.dlna.video.Video;

import java.util.List;

public class RemoteApplication extends Application{

	private Handler mHandler;
	private MusicService mService;
	private DlnaManager mDlnaManager;
	private int playIndex;
	private List<Video> videoList;
	
	private PictureSelectionManager picManager = null;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			mService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			// TODO Auto-generated method stub
			LocalBinder binder = (LocalBinder) arg1;
			mService = binder.getService();
		}
	};
	@Override
	public void onCreate() {
		
		super.onCreate();
		initDlnaManager();
		startMusicPlayerService();
    }
	public void setHandler(Handler handler){
		mHandler = handler;
	}
	public void setService(MusicService service){
		mService = service;
	}
	public Handler getHandler(){
		return mHandler;
	}
	public MusicService getMusicPlayerService(){
		return mService;
	}
	public ServiceConnection getMusicPlayerServiceConnection(){
		
		return mConnection;
	}
	
	public DlnaManager getDlnaManager(){
		
		return mDlnaManager;
	}
	
	private void startMusicPlayerService(){
		
		Intent intent = new Intent(this, MusicService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		
	}
	
	private void initDlnaManager(){
		mDlnaManager = DlnaManager.getDlnaManagerInstance(this.getApplicationContext());
	}
	
	public PictureSelectionManager getPicManager() {
		return picManager;
	}

	public void setPicManager(PictureSelectionManager picManager) {
		this.picManager = picManager;
	}
	
	public void removePicManager(){
		this.picManager = null;
	}
	
	public void setVideoList(List<Video> videoList){
		this.videoList = videoList;
	}
	
	public List<Video> getVideoList(){
		return videoList;
	}
}
