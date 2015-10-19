package com.casky.dlna.control;

import android.os.Handler;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;

/**
 * 
*    
* 项目名称：Smart_DLNA   
* 类名称：DlnaCommandManager   
* 类描述：   
* 创建人：shaojiansong   
* 创建时间＄1�714-9-23 下午1:54:41   
* 修改人：shaojiansong   
* 修改时间＄1�714-9-23 下午1:54:41   
* 修改备注＄1�7   
* 版本＄1�7 1.0   
*
 */
public class DlnaCommandManager implements CallbacksForAction{
	public static final String TAG = "DlnaCommandManager";
	private static DlnaCommandManager mDlnaCommandManager;
	private Runnable updateRunnable;
	private Runnable initMaxRunnable;
    private Runnable finishRunnable;
	private DlnaManager mDlnaManager;
    private LocalFinishCallback finishCallback = null;
    private boolean switchFlag = true;
    private int lastDur = 0;


    private SeekBarController sbController = null;

	private Handler seekHandler = new Handler();
	
	public static DlnaCommandManager getInstance(){
		if (mDlnaCommandManager == null) {
			mDlnaCommandManager = new DlnaCommandManager(); 
		}
		return mDlnaCommandManager;
	}

    public void initSeekBar(SeekBarController.SeekBarModule module,DlnaManager mDlnaManager ){
        this.sbController = SeekBarController.getSeekBarControllerInstance(module,mDlnaManager.getDlnaServiceManager());
        this.mDlnaManager = mDlnaManager;
        initUiRunnable();
    }

    private void initUiRunnable() {

        updateRunnable = new Runnable() {

            @Override
            public void run() {
                mDlnaManager.getDlnaServiceManager().getPositionInfo();
                sbController.setCurrentTime(Time.curString);
                sbController.setSeekBarProgress(Time.curInt);

                if (Time.lastDurInt != Time.durInt) {
                    //seekHandler.post(initMaxRunnable);
                    Time.lastDurInt = Time.durInt;
                    sbController.setSeekBarMax((int)Time.durInt);
                    Log.d(TAG,"setSeekBarMax="+Time.durInt);
                    sbController.setDuration(Time.durString);
                }
                seekHandler.postDelayed(updateRunnable, 1000);
                Log.d(TAG,"updateRunnable");
            }
        };



    }

    private void removeUIRunnable(){
        seekHandler.removeCallbacks(updateRunnable);
    }

    private void updateUIRunnable(){
        if(sbController != null) {
            seekHandler.post(updateRunnable);
        }
    }

    public void seekRunnbaleDelay(){
        removeUIRunnable();
        seekHandler.postAtTime(updateRunnable,android.os.SystemClock.uptimeMillis()+1500);
    }

    public void setFinishCallback(LocalFinishCallback finishCallback){
        this.finishCallback = finishCallback;
    }
	
	@Override
	public void avSetTransportActionCallback(boolean success, UpnpResponse rsp,
			String defaultMsg) {
		if(success){
			Log.d(TAG,"avSetTransportActionCallback success");
		}else{
			Log.d(TAG,"avSetTransportActionCallback failure,"+defaultMsg);
		}
	}

	@Override
	public void playActionCallback(boolean success, UpnpResponse rsp,
			String defaultMsg,LocalPlayCallback playCallbacks) {
		if(success){
			Log.d(TAG,"playActionCallback success");
			playCallbacks.playCallback();
            updateUIRunnable();
//            sbController.bindListener();
		}else{
            if(rsp != null) {
                Log.e(TAG, "playActionCallback failure," + "rsp:" + rsp.getResponseDetails()
                        + "," + rsp.toString() + ",defaultMsg:" + defaultMsg);
            }else{
                Log.e(TAG, "playActionCallback failure,defaultMsg:" + defaultMsg);
            }
		}
	}

	@Override
	public void seekActionCallback(boolean success, UpnpResponse rsp,
			String defaultMsg) {
		if(success){
            mDlnaManager.getDlnaServiceManager().getPositionInfo();

			Log.d(TAG,"seekActionCallback success");
		}else{
			Log.d(TAG,"seekActionCallback failure," + "rsp:" + rsp.getResponseDetails()
					+","+rsp.toString() + ",defaultMsg:"+defaultMsg);
		}
	}

	@Override
	public void pauseActionCallback(boolean success, UpnpResponse rsp,
			String defaultMsg) {
		if(success){
			Log.d(TAG,"pauseActionCallback success");
		}else{
			Log.d(TAG,"pauseActionCallback failure,"+defaultMsg);
		}
	}

	@Override
	public void resumeActionCallback(boolean success, UpnpResponse rsp,
			String defaultMsg) {
		if(success){
			Log.d(TAG,"resumeActionCallback success");
		}else{
			Log.d(TAG,"resumeActionCallback failure,"+defaultMsg);
		}
	}

	@Override
	public void stopActionCallback(boolean success, UpnpResponse rsp,
			String defaultMsg,LocalStopCallback stopCallback) {
		if(success){
			Log.d(TAG,"stopActionCallback success");
			stopCallback.stopCallback();
            Time.reset();
            removeUIRunnable();
		}else{
			Log.d(TAG,"stopActionCallback failure,"+defaultMsg);
		}
	}

	@Override
	public void getPositionInfoActionCallback(boolean success,
			UpnpResponse rsp, String defaultMsg, PositionInfo positionInfo) {
		// TODO Auto-generated method stub
		if(success){
            Time.curInt = (int) positionInfo.getTrackElapsedSeconds();
            Time.curString = positionInfo.getRelTime();
            Time.durInt = (int) positionInfo.getTrackDurationSeconds();
            Time.durString = positionInfo.getTrackDuration();

            if(finishCallback != null && Time.durInt != 0 && (Time.curInt == Time.durInt)  && lastDur != Time.durInt){
                Log.d(TAG,"curInt="+Time.curInt+",durInt="+Time.durInt);
                finishCallback.finishCallback();
                lastDur = Time.durInt;
                switchFlag = false;
            }else {
                switchFlag = true;
            }
            Log.d(TAG,"getTransportInfoActionCallback success");
		}else{
			Log.d(TAG,"getTransportInfoActionCallback failure,"+defaultMsg);
		}

	}

	@Override
	public void getTransportInfoActionCallback(boolean success,
			UpnpResponse rsp, String defaultMsg, TransportInfo transportInfo) {
		if(success){
			Log.d(TAG,"getTransportInfoActionCallback success");
		}else{
			Log.d(TAG,"getTransportInfoActionCallback failure,"+defaultMsg);
		}
	}

	@Override
	public void getVolumeActionCallback(boolean success, UpnpResponse rsp,
			String defaultMsg, int currentVol) {
		if(success){
			Log.d(TAG,"getVolumeActionCallback success");
		}else{
			Log.d(TAG,"getVolumeActionCallback failure,"+defaultMsg);
		}
	}

	@Override
	public void setVolumeActionCallback(boolean success, UpnpResponse rsp,
			String defaultMsg) {
		if(success){
			Log.d(TAG,"setVolumeActionCallback success");
		}else{
			Log.d(TAG,"setVolumeActionCallback failure,"+defaultMsg);
		}
	}

	@Override
	public void getMuteActionCallback(boolean success, UpnpResponse rsp,
			String defaultMsg) {
		if(success){
			Log.d(TAG,"getMuteActionCallback success");
		}else{
			Log.d(TAG,"getMuteActionCallback failure,"+defaultMsg);
		}
	}

	@Override
	public void getMuteActionCallback(boolean success, boolean currentMute) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMuteActionCallback(boolean success, UpnpResponse rsp,
			String defaultMsg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getMediaInfoInfoActionCallback(boolean success,
			UpnpResponse rsp, String defaultMsg, MediaInfo mediaInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getCurrentTransportActionCallback(boolean success,
			UpnpResponse rsp, String defaultMsg, TransportAction[] actions) {
		if(success){
			Log.d(TAG,"getCurrentTransportActionCallback success");
		}else{
			Log.d(TAG,"getCurrentTransportActionCallback failure,"+defaultMsg);
		}
	}
	
	public interface LocalPlayCallback{
		public void playCallback();
	}
	
	public interface LocalStopCallback{
		public void stopCallback();
	}

    public interface  LocalFinishCallback{
        public void finishCallback();
    }

    private static class Time{
        public static int curInt = 0;
        public static int durInt = 0;
        public static int lastDurInt = 0;
        public static String curString = null;
        public static String durString = null;

        public static void reset(){
            curInt = 0;
            durInt = 0;
            lastDurInt = 0;
            curString = null;
            durString = null;
        }
    }

}
