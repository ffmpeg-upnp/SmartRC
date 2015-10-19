/**
 * 
 */
package com.casky.remote.rc.speech;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.casky.remote.rc.main.MainFragmentRemote;
import com.casky.remote.rc.network.Client;
import com.casky.smartremote.R;
import com.casky.remote.utils.Helper;

/** 
 * 椤圭洰鍚嶇О锛歋martRemote
 * 绫诲悕绉帮細SpeechSender  
 * 绫绘弿杩帮細 鍙戦�佸０闊宠瘑鍒寜閿�
 * 鍒涘缓浜猴細wangbo
 * 鍒涘缓鏃堕棿锛�14-9-4 涓婂崍10:22:55
 * 淇敼浜猴細wangbo
 * 淇敼鏃堕棿锛�14-9-4 涓婂崍10:22:55
 * 淇敼澶囨敞锛�   
 * 鐗堟湰锛� 1.0    
 */
public class SpeechSender {
	
	private static final String TAG = "SpeechSender";
	private Client sendClient = null;
	private Activity mActivity = null;
	private Handler mNetWorkHandler = null;
	
	public SpeechSender(Activity mActivity){
		this.mActivity = mActivity;
		this.sendClient = MainFragmentRemote.sendClient;
		initSender();
	}
	
	/**
	* 鏂规硶鎻忚堪锛氳幏寰桸etWorkHandler鐢ㄤ互鍙戦�佹寜閿秷鎭� 
	* @return ture:NetWorkHandler鑾峰彇鎴愬姛;false:NetWorkHandler鑾峰彇澶辫触
	 */
	private boolean initSender(){
		this.sendClient = MainFragmentRemote.sendClient;
		if (sendClient == null){
			Log.e(TAG,"sendClient is null");
			return false;
		}else{
			this.mNetWorkHandler = sendClient.getNetWorkHandler();
			if(mNetWorkHandler == null){
				return false;
			}
		}
		return true;
	}
	
	/**
	* 鏂规硶鎻忚堪锛� 鍙戦�佹寜閿秷鎭�
	* @param str 鎸夐敭鍐呭
	* @return ture:鍙戦�佹垚鍔�false:鍙戦�佸け璐�
	 */
	public boolean sendKey(String str){
		if(mNetWorkHandler == null){
			if(!initSender()){
				return false;
			}
		}
		if (mActivity.getResources().getString(R.string.SPEECH_WORD_Numbers).contains(String.valueOf(str.charAt(0)))
				|| mActivity.getResources().getString(R.string.SPEECH_WORD_Directions).contains(String.valueOf(str.charAt(0)))
				&& str.length() > 1) {
    		for(int i=0;i<str.length();i++){
    			String message = parseKeyFromSpeech(String.valueOf(str.charAt(i)));
    			mNetWorkHandler.sendMessage(Helper.createMessage(Client.REMOTE_MESSAGE, message));
    		}
		}else{
			String message = parseKeyFromSpeech(str);
			mNetWorkHandler.sendMessage(Helper.createMessage(Client.REMOTE_MESSAGE, message));
    		Log.d(TAG, "send msg is " + message);
		}
		return true;
	}
	
	private String parseKeyFromSpeech(String str){
		String key = null;
		if(str.equals(mActivity.getString(R.string.SPEECH_WORD_Up))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_Up);
		}else if(str.equals(mActivity.getString(R.string.SPEECH_WORD_Down))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_Down);
		}else if(str.equals(mActivity.getString(R.string.SPEECH_WORD_Left))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_Left);
		}else if(str.equals( mActivity.getString(R.string.SPEECH_WORD_Right))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_Right);
		}else if(str.equals( mActivity.getString(R.string.SPEECH_WORD_1))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_1);
		}else if(str.equals( mActivity.getString(R.string.SPEECH_WORD_2))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_2);
		}else if(str.equals( mActivity.getString(R.string.SPEECH_WORD_3))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_3);
		}else if(str.equals( mActivity.getString(R.string.SPEECH_WORD_4))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_4);
		}else if(str.equals( mActivity.getString(R.string.SPEECH_WORD_5))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_5);
		}else if(str.equals( mActivity.getString(R.string.SPEECH_WORD_6))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_6);
		}else if(str.equals( mActivity.getString(R.string.SPEECH_WORD_7))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_7);
		}else if(str.equals( mActivity.getString(R.string.SPEECH_WORD_8))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_8);
		}else if(str.equals(mActivity.getString(R.string.SPEECH_WORD_9))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_9);
		}else if(str.equals(mActivity.getString(R.string.SPEECH_WORD_0))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_0);
		}else if(str.equals(mActivity.getString(R.string.SPEECH_WORD_Mute))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_Mute);
		}else if(str.equals(mActivity.getString(R.string.SPEECH_WORD_PowerOn)) 
				|| str.equals(mActivity.getString(R.string.SPEECH_WORD_PowerOFF))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_PowerOFF);
		}else if(str.equals(mActivity.getString(R.string.SPEECH_WORD_OK_Enter)) 
				|| str.equalsIgnoreCase(mActivity.getString(R.string.SPEECH_WORD_OK_Ok))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_OK);
		}else if(str.equals(mActivity.getString(R.string.SPEECH_WORD_SmartTV_Android)) 
				|| str.equals(mActivity.getString(R.string.SPEECH_WORD_SmartTV_MainPage))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_SmartTV);
		}else if(str.equals(mActivity.getString(R.string.SPEECH_WORD_Back))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_Back);
		}else if(str.equals(mActivity.getString(R.string.SPEECH_WORD_Home))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_Home);
		}else if(str.equals(mActivity.getString(R.string.SPEECH_WORD_Info))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_Info);
		}else if(str.equals(mActivity.getString(R.string.SPEECH_WORD_TV))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_TV);
		}else if(str.equals(mActivity.getString(R.string.SPEECH_WORD_Source_Input)) 
				|| str.equals(mActivity.getString(R.string.SPEECH_WORD_Source_InputSource)) 
				|| str.equals(mActivity.getString(R.string.SPEECH_WORD_Source_SignalSource))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_Source);
		}else if(str.equals(mActivity.getString(R.string.SPEECH_WORD_ChannelUp))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_ChannelUp);
		}else if(str.equals(mActivity.getString(R.string.SPEECH_WORD_ChannelDown))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_ChannelDown);
		}else if(str.equals(mActivity.getString(R.string.SPEECH_WORD_VolumeUp))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_VolumeUp);
		}else if(str.equals(mActivity.getString(R.string.SPEECH_WORD_VolumeDown))){
			key = mActivity.getString(R.string.TREQ_RC_KEY_VolumeDown);
		}else{
			key = mActivity.getString(R.string.TREQ_RC_KEY_Dot);
		}
		return key;
	}
	

}
