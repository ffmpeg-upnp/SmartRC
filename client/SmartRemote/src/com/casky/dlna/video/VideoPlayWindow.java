package com.casky.dlna.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.casky.dlna.control.SeekBarController;
import com.casky.smartremote.R;
import com.casky.dlna.control.DlnaCommandManager.LocalPlayCallback;
import com.casky.dlna.control.DlnaCommandManager.LocalStopCallback;
import com.casky.dlna.control.DlnaManager;
import com.casky.main.slidingmenu.RemoteApplication;
import com.casky.dlna.main.RenderSelectCallBridge;
import com.casky.dlna.main.RenderSelectCallBridge.renderSelectCallBack;
import com.casky.dlna.music.MusicService;
import com.casky.dlna.utils.UriBuilder;
import com.casky.dlna.view.RenderDialog;

import org.fourthline.cling.model.ModelUtil;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VideoPlayWindow extends Activity implements Callback,
  MediaPlayer.OnPreparedListener, OnBufferingUpdateListener, 
  OnErrorListener,
  OnCompletionListener,OnTouchListener,OnSeekBarChangeListener, renderSelectCallBack
{
	
	private static final String TAG = "VideoPlayWindow";
	private static final int CHANGE_CONTROL_PANEL_VISIBILITY = 0x01;
	private static final int DRAW_PROGRESS_BAR_AND_TIME = 0x04;
	private static final int FF_FR_SIZE = 5;
	private static final int GESTURE_FLING_DISTANCE = 150;
	
	/** Called when the activity is first created. */
	private MediaPlayer mMediaPlayer;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private int mVideoWidth;
	private int mVideoHeight;
	private int currentVideoIndex = 0;
	private boolean buttonIsvisible = true;
	private boolean forceButtonVisible = false;
	private RelativeLayout mMediaPlayerTitleBar;
	private RelativeLayout mMediaPlayerControlBar;
	private ThreadShow buttonVisibilityTimer = null;
	private Timer mProgressTimer;  
	private SeekBar mSeekBar;
	private TextView fileNameOntitle;
	private TextView curTimeTV;
	private TextView totalTimeTV;
	private Button playBtn;
	private DlnaManager mDlnaManager;
	private ListView videoPlayListView;
	private VideoPlayListAdapter videoPlayListAdapter;
    private boolean isPlaying = false;
    private boolean isCreated = false;

	private List<Video> videoList = null;
	private RenderDialog mRenderDialog = null;
	
	public static final int DLNA_PLAY = 0x01;
	public static final int DLNA_STOP = 0x02;
    public static final int DLNA_FINISH = 0x03;
	
	private Handler videoUIHandler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case DLNA_PLAY:
                String process = ModelUtil.toTimeString(mMediaPlayer.getCurrentPosition()/1000);
				mDlnaManager.getDlnaServiceManager().seek(process);
				findViewById(R.id.video_mediaplayer_btn_push).setBackgroundResource(R.drawable.dlna_media_stop_normal);
                findViewById(R.id.video_mediaplayer_btn_pause).setBackgroundResource(R.drawable.dlna_media_pause_normal);
                mMediaPlayer.pause();
				break;
			case DLNA_STOP:

                if(mMediaPlayer == null) return;
                Log.d(TAG,"seekTo"+getMediaPlayerPosition());
                mMediaPlayer.start();
                mMediaPlayer.seekTo(getMediaPlayerPosition());
                mSeekBar.setMax(mMediaPlayer.getDuration());
//                findViewById(R.id.video_mediaplayer_btn_pause).setBackgroundResource(R.drawable.dlna_media_play_normal);
				findViewById(R.id.video_mediaplayer_btn_push).setBackgroundResource(R.drawable.dlna_multi_menu_push);
				break;
                case DLNA_FINISH:
                    break;
			}
		}
	};

    private int getMediaPlayerPosition(){

        int DlnaProcess = mSeekBar.getProgress();
        int DlnaMax = mSeekBar.getMax();
        if(mMediaPlayer == null || DlnaMax==0) return 0;
        int targetPosition = mMediaPlayer.getDuration()*DlnaProcess/DlnaMax;
        return targetPosition;
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlna_video_play);
	    if (!checkSDCard()) {
			/* ����Userδ��װSD�洢�� */
	    	mMakeTextToast(this.getResources().getText(R.string.dlna_movie_str_err).toString(),true);
		}
	    getVideoMap();
	    
		mSurfaceView = (SurfaceView)findViewById(R.id.video_mediaplayer_sv_surface_view);//(SurfaceView)getActivity().findViewById(R.id.mSurfaceView1);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mMediaPlayerTitleBar = (RelativeLayout)findViewById(R.id.video_mediaplayer_rl_titlebar);
		mMediaPlayerControlBar = (RelativeLayout)findViewById(R.id.video_mediaplayer_rl_controlbar);
		
		
		mSeekBar = (SeekBar) findViewById(R.id.video_mediaplayer_sb_progressbar);
		mSeekBar.setOnSeekBarChangeListener(this);
		
		buttonVisibilityTimer = new ThreadShow();
		buttonVisibilityTimer.start(); 
		mSurfaceView.setOnTouchListener(this);

		fileNameOntitle = (TextView) findViewById(R.id.video_mediaplayer_tv_name);
		curTimeTV = (TextView) findViewById(R.id.video_mediaplayer_tv_curtime);
		totalTimeTV = (TextView) findViewById(R.id.video_mediaplayer_tv_totaltime);
		
		Button mTitleBack = (Button) findViewById(R.id.video_mediaplayer_btn_back);
		playBtn = (Button) findViewById(R.id.video_mediaplayer_btn_pause);
		Button pushBtn = (Button) findViewById(R.id.video_mediaplayer_btn_push);
		Button playListBtn = (Button)findViewById(R.id.video_mediaplayer_btn_playList);
		Button frButton = (Button) findViewById(R.id.video_mediaplayer_btn_fr);
		Button ffButton = (Button) findViewById(R.id.video_mediaplayer_btn_ff);
		
		mTitleBack.setOnTouchListener(this);
		playBtn.setOnTouchListener(this);
		pushBtn.setOnTouchListener(this);
		playListBtn.setOnTouchListener(this);
		frButton.setOnTouchListener(this);
		ffButton.setOnTouchListener(this);
		
		videoPlayListView = (ListView)findViewById(R.id.video_play_lv_playList);
		videoPlayListAdapter = new VideoPlayListAdapter(this,videoList);
		videoPlayListView.setAdapter(videoPlayListAdapter);
		videoPlayListView.setOnItemClickListener(videoPlayListAdapter);
		
		mDlnaManager = ((RemoteApplication)this.getApplication()).getDlnaManager();
		initLayout();
        mDlnaManager.getDlnaServiceManager().initDLNASeekBar(
                new SeekBarController.SeekBarModule(mSeekBar,totalTimeTV,curTimeTV),
                mDlnaManager);
	}
	
	private void initLayout(){
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    	int screenWidth = wm.getDefaultDisplay().getWidth();
    	int screenHeight = wm.getDefaultDisplay().getHeight();
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(screenWidth*2/3, RelativeLayout.LayoutParams.WRAP_CONTENT);
	}

	private void getVideoMap(){
         Intent intent = this.getIntent();
         currentVideoIndex = intent.getIntExtra(MainFragmentVideo.KEY_POSITION ,0);
         RemoteApplication share = (RemoteApplication) getApplication().getApplicationContext();
         videoList = share.getVideoList();
	 }

	public void setCurrentPlayIndex(int index){
		currentVideoIndex = index;
	}

	/**
	 * description: when progress-bar is showed, draw current playing time
	 */
	private void DrawProgressBarAndTime(boolean firstEntry){
		if(mMediaPlayer == null){
			Log.e(TAG,"MediaPlayer is null,maybe surface is destoryed");
			return;
		}
		int position = mMediaPlayer.getCurrentPosition();  
		int duration = mMediaPlayer.getDuration();
		Log.d(TAG, "position:" + position + ", duration:" + duration);
		if(firstEntry){
			String totalTime = ModelUtil.toTimeString(duration/1000);
			totalTimeTV.setText(totalTime);
		}
		if (duration > 0) {  
			mSeekBar.setProgress(position);
			curTimeTV.setText(ModelUtil.toTimeString(position/1000));
		}  
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {  
		public void handleMessage(Message msg) {  
		switch(msg.what){
			case CHANGE_CONTROL_PANEL_VISIBILITY:
				if(buttonIsvisible == true){
					buttonIsvisible = false;
         			mMediaPlayerTitleBar.setVisibility(View.INVISIBLE);
         			mMediaPlayerControlBar.setVisibility(View.INVISIBLE);
         			videoPlayListView.setVisibility(View.GONE);
         			if(buttonVisibilityTimer != null){
         				buttonVisibilityTimer.shutDown();
         			}
         		}else{            			
         			mMediaPlayerTitleBar.setVisibility(View.VISIBLE);
         			mMediaPlayerControlBar.setVisibility(View.VISIBLE);
         			buttonIsvisible = true;
         		}
         		break;
			case DRAW_PROGRESS_BAR_AND_TIME:
         		if(buttonIsvisible){
         			DrawProgressBarAndTime(false);
         		}
         		break;
			default:
     			break;
			}
		};  
	}; 
	
	
 
	class ThreadShow extends Thread {  

		private int sleepCounter = 0;
		private boolean stopThread = false;
		private boolean resetButtonTimer = false;
		
		ThreadShow(){
			sleepCounter = 6;
		} 
		
		ThreadShow(int sc){
			sleepCounter = sc;
		} 
		
		@Override  
		public void run() {
			stopThread = false;

			for(int i = 0; i < sleepCounter; i++){
				if(stopThread){
					return;
				}
				try {  
					Thread.sleep(1000);
				} catch (Exception e) {     
					e.printStackTrace();  
					System.out.println("thread error...");  
				} 
				if(resetButtonTimer == true){
					resetButtonTimer = false;
					i = 0;
				}
			}   
			if(buttonIsvisible && !forceButtonVisible){
				mHandler.sendEmptyMessage(CHANGE_CONTROL_PANEL_VISIBILITY);
			}  
		}  
		
		public synchronized void shutDown(){
			stopThread = true;
		}
		
		public synchronized void reset(){
			resetButtonTimer = true;
		}
	}
 
	private boolean checkSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
			android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public void mMakeTextToast(String str, boolean isLong) {
		if (isLong) {
			Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
		}
	}
 
	public void playVideo() {
        MusicService musicService =  ((RemoteApplication)getApplication()).getMusicPlayerService();
        MediaPlayer musicMediaPlayer =musicService .getMediaPlayer();
        if(musicMediaPlayer != null){
            if(musicMediaPlayer.isPlaying()){
                musicService.stop();
            }
//            musicService.release();
        }
		mMediaPlayer.reset();
		Video curVideo = videoList.get(currentVideoIndex);
		String path = curVideo.getMetaData().getPath();

		fileNameOntitle.setText(videoList.get(currentVideoIndex).getMetaData().getName()); 

		videoPlayListAdapter.notifyDataSetChanged();
		
		try {
			mMediaPlayer.setDataSource((String) path);
			mMediaPlayer.prepare();
			mSeekBar.setMax(mMediaPlayer.getDuration());
            videoPlayListAdapter.setCurPlayingPosition(currentVideoIndex);
		}catch (IOException ioE) {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.dlna_movie_format_not_support),Toast.LENGTH_LONG).show();

        }catch (Exception e) {
			e.printStackTrace();
		}

	}
 
	TimerTask mTimerTask = new TimerTask() {  
	@Override  
		public void run() {  
			if(mMediaPlayer==null)  
				return;  
			// Log.v(MTAG, "isPressed : " + mSeekBar.isPressed());
			if (mMediaPlayer.isPlaying() && mSeekBar.isPressed() == false) {
				mHandler.sendEmptyMessage(DRAW_PROGRESS_BAR_AND_TIME);  
			}  
		}  
	};
 
	void initMediaPlayer(){
        if(mMediaPlayer != null){
            mMediaPlayer.release();
        }
		mMediaPlayer = new MediaPlayer(); 
		mMediaPlayer.setDisplay(mSurfaceHolder);  
		mMediaPlayer.setOnBufferingUpdateListener(this);
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setOnPreparedListener(this);
//		mMediaPlayer.setOnSeekCompleteListener(this);
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        if(!isCreated) {
            mProgressTimer = new Timer();
            mProgressTimer.schedule(mTimerTask, 0, 1000);
        }
		mMediaPlayer.setOnErrorListener(this);
        isCreated = true;
	}
 
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
		int height) {
		// TODO Auto-generated method stub
		//Log.i(TAG, "Surface Changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		System.out.println("Surface surfaceCreated");
		initMediaPlayer();
		playVideo();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		System.out.println("Surface Destroyed");
		releaseResource();
		super.onDestroy();
		
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mVideoWidth = mMediaPlayer.getVideoWidth();
		mVideoHeight = mMediaPlayer.getVideoHeight();
		
        DisplayMetrics metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int screenWidth = metric.widthPixels;  // ��Ļ��ȣ����أￄ1�7
        
		if(mVideoWidth > mVideoHeight){
			mVideoHeight = (int) (((float)mVideoHeight/(float)mVideoWidth)*(float)screenWidth);
			mVideoWidth = screenWidth;
		}
		
		if (mVideoWidth != 0 && mVideoHeight != 0)
		{
			/* ������Ƶ�Ŀ�Ⱥ͸߶ￄ1�7 */
			mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
			/* ��ʼ���� */
			mMediaPlayer.start();
			if(mRenderDialog != null && mRenderDialog.isDeviceSet()){
				doSelect();
			}
			// mTextView01.setText(R.string.str_play);
		}
		DrawProgressBarAndTime(true);
	}
	
	 @Override
     protected void onDestroy() {
         Log.v(TAG, "onDestroy");
         releaseResource();
         super.onDestroy();
     }

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if(currentVideoIndex < videoList.size()-1){
			currentVideoIndex++;
			playVideo();
		}else{
			this.finish();
		}
	}

	public void playPauseControl(){
		if(mMediaPlayer != null){
			if (isPlaying){
				pause();
			} else{                                  		
				resume();
			} 
		}
	}
	
	public void pause(){
        isPlaying= false;
		playBtn.setBackgroundResource(                  
			R.drawable.dlna_media_play_normal);
		if(mDlnaManager.getDlnaServiceManager().isDLNAPlaying()){
			mDlnaManager.getDlnaServiceManager().pause();
		}else{
            mMediaPlayer.pause();
        }
		
	}
	
	public void resume(){
        isPlaying = true;
		playBtn.setBackgroundResource(                 
				R.drawable.dlna_media_pause_normal);
		if(mDlnaManager.getDlnaServiceManager().isDLNAPlaying()){
			mDlnaManager.getDlnaServiceManager().resume();
		}else{
            mMediaPlayer.start();
        }
	}
	
	public void fastForward(){
//		int curTimeInt = mMediaPlayer.getCurrentPosition();
        int curTimeInt = mSeekBar.getProgress();
		int targetTimeInt = curTimeInt + FF_FR_SIZE;
        if(mMediaPlayer.isPlaying()) {
            mSeekBar.setProgress(targetTimeInt);
            mMediaPlayer.seekTo(targetTimeInt);
        }else{
            mDlnaManager.getDlnaServiceManager().seek(ModelUtil.toTimeString(targetTimeInt));
        }
		Log.e(TAG, "curTimeInt " + ModelUtil.toTimeString(curTimeInt)
				+ ",targetTimeInt "+ModelUtil.toTimeString(targetTimeInt));
	}
	
	public void fastRewind(){
//		int curTimeInt = mMediaPlayer.getCurrentPosition();
        int curTimeInt = mSeekBar.getProgress();
		int targetTimeInt = curTimeInt - FF_FR_SIZE*2;
        if(mMediaPlayer.isPlaying()) {
            mSeekBar.setProgress(targetTimeInt);
            mMediaPlayer.seekTo(targetTimeInt);
        }else{
            mDlnaManager.getDlnaServiceManager().seek(ModelUtil.toTimeString(targetTimeInt));
        }
		Log.e(TAG, "curTimeInt " + ModelUtil.toTimeString(curTimeInt)
				+ ",targetTimeInt "+ModelUtil.toTimeString(targetTimeInt));
	}

	public void pushToTV(){
		mRenderDialog = RenderDialog.getInstance(this);
		mRenderDialog.showRenderDialog();
		RenderSelectCallBridge.getInstance().setRenderSelectCallBack(this);
	}
	
	public void stopPlayInTV(){
		if(mDlnaManager.getDlnaServiceManager() != null){
			mDlnaManager.getDlnaServiceManager().stop(new LocalStopCallbackImpl());
		}
	}
	
	private int xStartPos = 0;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		Log.v(TAG, "On Touch!!!" + v + event);
			
		if(event.getAction() == MotionEvent.ACTION_DOWN &&
				v.getId() == R.id.video_mediaplayer_sv_surface_view){
			xStartPos = (int) event.getX();
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP){
			
			if(buttonVisibilityTimer != null){
 				if(buttonVisibilityTimer.isAlive()){
 					buttonVisibilityTimer.reset();
 				}else{
 					buttonVisibilityTimer = new ThreadShow();
 				    buttonVisibilityTimer.start();  
 				}
 			}else{
 				buttonVisibilityTimer = new ThreadShow();
 			    buttonVisibilityTimer.start();  
 			}
			
			switch(v.getId()){
				case R.id.video_mediaplayer_btn_back:
					this.finish();
					break;
				case R.id.video_mediaplayer_sv_surface_view:
					if(videoPlayListView.getVisibility() != View.VISIBLE){
						int xEndPos = (int) event.getX();
						if(xEndPos - xStartPos > GESTURE_FLING_DISTANCE){
							fastForward();
						}else if(xStartPos - xEndPos > GESTURE_FLING_DISTANCE){
							fastRewind();
						}else{
							mHandler.sendEmptyMessage(CHANGE_CONTROL_PANEL_VISIBILITY);
						}
					}else{
						videoPlayListView.setVisibility(View.GONE);
						forceButtonVisible = false;
						View btn = findViewById(R.id.video_mediaplayer_btn_playList);
						btn.setBackgroundResource(R.drawable.dlna_media_playlist_normal);
					}
					break;
				case R.id.video_mediaplayer_btn_pause:
					playPauseControl();
					break;
				case R.id.video_mediaplayer_btn_push:
					if(mDlnaManager != null &&
                            mDlnaManager.getDlnaServiceManager() != null &&
                            !mDlnaManager.getDlnaServiceManager().isDLNAPlaying()){
						pushToTV();
					}else{
						stopPlayInTV();
					}
					break;
				case R.id.video_mediaplayer_btn_playList:
					if(videoPlayListView.getVisibility() == View.VISIBLE){
                        Animation anim = AnimationUtils.loadAnimation(this, R.anim.dlna_hide_play_list);
                        videoPlayListView.startAnimation(anim);
						videoPlayListView.setVisibility(View.GONE);
						forceButtonVisible = false;
						v.setBackgroundResource(R.drawable.dlna_media_playlist_normal);
					}else{
						videoPlayListView.setVisibility(View.VISIBLE);
						forceButtonVisible = true;
						v.setBackgroundResource(R.drawable.dlna_media_playlist_hl);
                        Animation anim = AnimationUtils.loadAnimation(this, R.anim.dlna_show_play_list);
                        videoPlayListView.startAnimation(anim);
					}
					break;
				case R.id.video_mediaplayer_btn_fr:
					fastRewind();
					break;
				case R.id.video_mediaplayer_btn_ff:
					fastForward();
					break;
			}
			
			xStartPos = 0;
		}
		
		return true;
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(TAG,"seekBar.getProgress="+seekBar.getProgress());
        if(mDlnaManager.getDlnaServiceManager().isDLNAPlaying()){
            mDlnaManager.getDlnaServiceManager().seek(ModelUtil.toTimeString(mSeekBar.getProgress()));
            mMediaPlayer.seekTo(getMediaPlayerPosition());
        }else {
            mMediaPlayer.seekTo(seekBar.getProgress());
        }
	}


	@Override
	public void doSelect() {
		// TODO Auto-generated method stub.
//		mDlnaManager.getDlnaServiceManager().stop(new LocalStopCallbackImpl());
		mDlnaManager.getDlnaServiceManager().setPushMediaData(
			UriBuilder.MediaType.video,
			String.valueOf(videoList.get(currentVideoIndex).getMetaData().getId()), 
			videoList.get(currentVideoIndex).getMetaData().getMetadataString());//�����ļ���id��Type
		mDlnaManager.getDlnaServiceManager().play(new LocalPlayCallbackImpl());//��ʼ����
	}

	
	public class LocalPlayCallbackImpl implements LocalPlayCallback{
		@Override
		public void playCallback() {
			videoUIHandler.sendEmptyMessage(DLNA_PLAY);
		}
	}
	
	public class LocalStopCallbackImpl implements LocalStopCallback{
		@Override
		public void stopCallback() {
			videoUIHandler.sendEmptyMessage(DLNA_STOP);
		}
	}
	
	public void releaseResource(){
        if(mDlnaManager.getDlnaServiceManager().isDLNAPlaying()){
            mDlnaManager.getDlnaServiceManager().stop(new LocalStopCallbackImpl() );
        }
//		if(mProgressTimer != null){
//			mProgressTimer.cancel();
//			mProgressTimer.purge();
//		}
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		if(buttonVisibilityTimer != null && buttonVisibilityTimer.isAlive()){
			buttonVisibilityTimer.shutDown();
			buttonVisibilityTimer = null;
		}

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.e(TAG,"onError! what="+what+",extra="+extra);
		return false;
	}
}


