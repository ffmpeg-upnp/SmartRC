package com.casky.remote.rc.speech;

import com.casky.smartremote.R;
import com.casky.remote.utils.Helper;
import com.casky.remote.utils.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
* 项目名称：SmartRemote
* 类名称：SpeechManager  
* 类描述： 声音识别管理类
* 创建人：wangbo
* 创建时间：2014-9-3 下午5:08:46
* 修改人：wangbo
* 修改时间：2014-9-3 下午5:08:46
* 修改备注：   
* 版本： 1.0    
*
 */
public class SpeechManager {

	public static final String TAG = "SpeechManager";
	public static final int TOAST_SHORT = 20000;
	public static final int TOAST_LONG = 20001;
	public static final int INIT = 20002;
	public static final int BEGINSPEECH = 20003;
	public static final int VOLUMECHANGE = 20004;
	public static final int ENDSPEECH = 20005;
	public static final int GETRESULT = 20006;
	public static final int ERROR = 21000;
	
	private static final String GRAMMAR_TYPE = "abnf";
	private static final String GRAMMAR_FILE = "voice_command.abnf";
	private static final String GRAMMAR_ENCODE = "utf-8";
	
	private Activity mMainActivity = null;
	private Handler mainHandler = null;
	private static SpeechRecognizer mRecognizer = null;
	private static String grammarContent = null;
	private static String cloudGrammarId = null;
	
	/**
	 * 
	 * SpeechManager构造方法
	 * @param mMainActivity 上下文信息
	 * @param mainHandler 控制UI的Handler
	 */
	public SpeechManager(Activity mMainActivity,Handler mainHandler){
		this.mMainActivity = mMainActivity;
		this.mainHandler = mainHandler;
	}
	
	/**
	* @param mContext
	* 方法描述：在程序创建时提前初始化声音识别、构建词典以提高识别效率 
	* 创建人：wangbo
	* 创建时间：2014-9-4 下午2:18:57
	 */
	public static void preLoadGrammarText(final Context mContext){
		initSpeechSetting(mContext);
		buildSpeechGrammar();
	}
	
	/**
	* 方法描述： 初始化声控识别控件
	* 创建人：wangbo
	* 创建时间：2014-9-3 下午5:20:54
	 */
	private static void initSpeechSetting(final Context mContext){
		SpeechUtility.createUtility(mContext, SpeechConstant.APPID +"=53ffea0b");
		mRecognizer = SpeechRecognizer.createRecognizer(mContext, null);
		grammarContent = Helper.readFile(mContext,GRAMMAR_FILE,GRAMMAR_ENCODE);
	}
	
	/** 
	* 方法描述： 构建语法
	* 创建人：wangbo
	* 创建时间：2014-9-3 下午5:26:38
	 */
	private static void buildSpeechGrammar(){
    	// 取得语法内容
    	mRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, GRAMMAR_ENCODE);
		mRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
		int ret = mRecognizer.buildGrammar(GRAMMAR_TYPE, grammarContent, grammarListener);
		if(ret != ErrorCode.SUCCESS){
			Log.e(TAG,"语法构建失败：" + ret);
		}
    }
	
	/** 
	* 方法描述： 开始识别
	* 创建人：wangbo
	* 创建时间：2014-9-3 下午5:21:20
	 */
    public void startSpeechListener(){
    	mRecognizer.setParameter(SpeechConstant.VAD_BOS, "4000");
		mRecognizer.setParameter(SpeechConstant.VAD_EOS, "300");
		//mRecognizer.setParameter(SpeechConstant.PARAMS, "local_grammar=voice,mixed_threshold=40");
		mRecognizer.setParameter(SpeechConstant.CLOUD_GRAMMAR, cloudGrammarId);

		int recode = mRecognizer.startListening(mRecognizerListener);
		if(recode != ErrorCode.SUCCESS)
			showTip(mMainActivity.getString(R.string.speech_voice_recognize_err) + recode);
    }
    
    /**
    * 方法描述： 判断是否正在识别
    * 创建人：wangbo
    * 创建时间：2014-9-3 下午5:22:36
     */
    public boolean isSpeechListenerListenering(){
    	return mRecognizer.isListening();
    }
    
    /**
    * 方法描述： 停止识别
    * 创建人：wangbo
    * 创建时间：2014-9-3 下午5:23:58
     */
    public void stopSpeechListener(){
    	mRecognizer.stopListening();
    }
    
    /**
    * 方法描述： 销毁正在识别对象
    * 创建人：wangbo
    * 创建时间：2014-9-3 下午5:25:09
     */
    public void destorySpeechListener(){
    	mRecognizer.destroy();
    }
    
    /**
     * 构建语法的回调接口
     */
    private static GrammarListener grammarListener = new GrammarListener() {
    	
    	/**
    	 * 构建完毕的回调方法
    	 */
		@Override
		public void onBuildFinish(String arg0, SpeechError arg1) {
			// TODO Auto-generated method stub
			if(arg1 == null){		
				cloudGrammarId = arg0;
				Log.d(TAG,"语法构建成功：" + arg0);
			}else{
				Log.e(TAG,"语法构建失败，错误码：" + arg1.getErrorCode());
			}
		}
	};
	
	/**
	 * 识别的回调接口
	 */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        
    	/**
    	 * 音量改变,音量范围[0-30]
    	 */
        @Override
        public void onVolumeChanged(int v) {
        	sendProcess(VOLUMECHANGE, String.valueOf(v));
        }
        
        /**
         * 录音自动停止回调
         *  说明：内部集成了端点检测功能，当用户一定时间内不说话，
         *  默认为用户已经不需要再录入语音，会自动调用此回调函数， 
         *  并停止当前录音。
         */
        @Override
        public void onEndOfSpeech() {
        	sendProcess(ENDSPEECH,null);
        }
        
        /**
         * 识别开始
         */
        @Override
        public void onBeginOfSpeech() {
        	sendProcess(BEGINSPEECH,null);
        }

        /**
         * 发生错误
         */
		@Override
		public void onError(SpeechError arg0) {
			//showTip("onError Code："	+ arg0);
			
			sendProcess(ERROR,errMsgFilter(arg0));
		}

		/**
		 * 识别会话事件 扩展用接口，由具体业务进行约定 
		 * 例如eventType为0显示网络状态，
		 * agr1为网络连接值
		 */
		@Override
		public void onEvent(int arg0, int arg1, int arg2, String arg3) {
			// TODO Auto-generated method stub
			
		}

		/**
		 *  返回识别结果,结果可能为空，请增加判空处理 说明：
		 *  SpeechRecognizer采用边录音边发送的方式，可能会多次返回结果。
		 */
		@Override
		public void onResult(RecognizerResult arg0, boolean arg1) {
			// TODO Auto-generated method stub
			String text = JsonParser.parseGrammarResult(arg0.getResultString());
			String result = JsonParser.getGrammarResult();
			
			if (null != result) {
				Log.i(TAG, "recognizer text：" + text);
				Log.i(TAG, "recognizer result：" + result); 
				Log.i(TAG,"is last one ? " + arg1);
            } else {
                Log.d(TAG, "recognizer result : null");
                result = "-1";
            }	
			sendProcess(GETRESULT,result);  
		}
    };
    
    /**
    * @param str 需要显示的内容
    * 方法描述：在UI上显示Toast 
    * 创建人：wangbo
    * 创建时间：2014-9-3 下午5:30:37
     */
    private void showTip(String str){
    	if(str == null) str = " ";
    	mainHandler.sendMessage(Helper.createMessage(TOAST_SHORT, str));
    	Log.d(TAG,str);
    }
    
    /**
    * @param messageType 识别消息的类型
    * @param str 识别消息的内容
    * 方法描述： 由回调方法调用以向UI线程发送消息
    * 创建人：wangbo
    * 创建时间：2014-9-3 下午5:30:58
     */
    private void sendProcess(int messageType,String str){
    	mainHandler.sendMessage(Helper.createMessage(messageType, str));
    }
    
    /**
    * 方法描述：错误信息过滤 
    * @param err
    * @return 过滤后的错误信息
     */
    private String errMsgFilter(SpeechError err){
    	Log.d(TAG, err.getErrorCode()+"");
    	if(err.getErrorCode() == ErrorCode.MSP_ERROR_NO_MORE_DATA){
    		return err.getErrorDescription() + mMainActivity.getResources().getString(R.string.speech_volume_too_low);
    	}
    	return err.getErrorDescription();
    }

}
