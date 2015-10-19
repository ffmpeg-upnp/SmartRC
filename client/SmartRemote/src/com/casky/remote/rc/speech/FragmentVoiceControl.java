package com.casky.remote.rc.speech;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.casky.remote.rc.main.MainFragmentRemote;
import com.casky.smartremote.R;
import com.casky.remote.utils.Helper;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
* @file FragmentFrequentKey   
* @author wangbo fujiangtao
* @data 2014-9-3
* @version 1.0    
*
 */
public class FragmentVoiceControl extends Fragment {

	private static final String TAG = "FragmentVoiceControl";
	private static final int SPEECHSTATE_READY = 0x00;
	private static final int SPEECHSTATE_BEGIN = 0x01;
	private static final int SPEECHSTATE_RESULT = 0x02;
	private static final int SPEECHSTATE_FAIL = 0x03;
	private static final int SPEECHSTATE_ERROR = 0x04;
	private static final int SPEECHSTATE_HIDE_TEXTVIEW = 0x05;
	private static final int TEXTSIZE = 20;
	
	private int SpeechState = SPEECHSTATE_READY;
	
	private SpeechManager speechManager = null;
	private SpeechSender speechSender = null;
	private Toast toast= null;
	private Button voiceButton = null;
	private LinearLayout speechHelpLayout = null;
	private ImageView speechHelpBtn = null;
	private TextView textView= null;
	private ImageView volumeImageView = null;
	private int speechHelpHeight = 0;
		
	private static final int POINTHEIGHT = 20;
	private static final int POINTWIDTH = 20;

	private ArrayList<View> manTextViews = null;
	private ArrayList<View> circleButtons = null;
	private ViewPager mViewPager = null;
	private TextView manTextView = null;
	private LinearLayout circleImageLayout = null;
	
	
	/**
	 * 接收回调信息以更改UI的Handler
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@Override 
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
				switch (msg.what)
				{
				case SpeechManager.TOAST_SHORT:
					showTip(msg.obj.toString(),Toast.LENGTH_SHORT);
					break;
				case SpeechManager.TOAST_LONG:
					showTip(msg.obj.toString(),Toast.LENGTH_LONG);
					break;
				case SpeechManager.INIT:
					textView.setText("OK");
					break;
				case SpeechManager.BEGINSPEECH:		
					SpeechState = SPEECHSTATE_BEGIN;
					textView.setText("");
					break;
				case SpeechManager.ENDSPEECH:
					break;
				case SpeechManager.VOLUMECHANGE:
					changeVolumeImg(Integer.parseInt(msg.obj.toString()));
					break;
				case SpeechManager.GETRESULT:
					enableSpeechButton();
					volumeImageView.setVisibility(View.INVISIBLE);
					String result = msg.obj.toString();
					if(result == "-1"){
						SpeechState = SPEECHSTATE_FAIL;
						sendFailMsgDelay(getString(R.string.retry_info));
						break;
					}
					SpeechState = SPEECHSTATE_RESULT;
					sendOKAnim(msg.obj.toString());
					
					if(!speechSender.sendKey(result)){
						showTip(getActivity().getString(R.string.not_connecting), Toast.LENGTH_SHORT);
					}
					break;
				case SpeechManager.ERROR:			
					SpeechState = SPEECHSTATE_ERROR;
					enableSpeechButton();
					volumeImageView.setVisibility(View.INVISIBLE);
					sendFailMsgDelay(msg.obj.toString());
					break;
				case SPEECHSTATE_HIDE_TEXTVIEW:
					textView.setVisibility(View.INVISIBLE);
					textView.setText("");
					break;
				}
		}
	};
	
	/**
	* 方法描述：enable语音按钮
	 */
	private void enableSpeechButton(){
		voiceButton.setText(getActivity().getString(R.string.speech_press_to_speak));
		voiceButton.setEnabled(true);
	}
	
	/**
	* 方法描述：disable语音按钮
	 */
	private void disableSpeechButton(){
		voiceButton.setText(getActivity().getString(R.string.speech_recongnizing));
		voiceButton.setEnabled(false);
	}
	
	/**
	* 方法描述：发送成功动画
	 */
	private void sendOKAnim(String str){
		textView.setVisibility(View.VISIBLE);
		textView.setText(str);
		int[] location = new int[2];  
		textView.getLocationOnScreen(location);  
        int x = location[0];  
        int y = location[1];  
		TranslateAnimation translateAnimation = 
				new TranslateAnimation(0f, 0f,0f,-y);
        //设置动画时间  
        translateAnimation.setDuration(1000);  
        textView.setVisibility(View.INVISIBLE);
        textView.startAnimation(translateAnimation); 
	}
	
	/**
	* 方法描述：设置发送失败消息提示显示时间
	 */
	private void sendFailMsgDelay(String str){
		textView.setVisibility(View.VISIBLE);
		textView.setText(str);
		TimerTask task = new TimerTask(){
			@Override
			public void run() {
				mHandler.sendMessage(Helper.createMessage(SPEECHSTATE_HIDE_TEXTVIEW, ""));
			}
		};
		
		Timer t = new Timer(true);
		t.schedule(task, 1000);
	}
	
	/**
	* 方法描述：显示Toast 
	* @param msg Toast信息
	* @param length Toast持续时间
	 */
	public void showTip(String msg,int length){
		toast.setDuration(length);
		toast.setText(msg);
		toast.show();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		voiceButton.getBackground().setAlpha(100);
		speechHelpBtn.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void onResume() {
		voiceButton.getBackground().setAlpha(255);
		speechHelpBtn.setVisibility(View.VISIBLE);
		super.onResume();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, 
    ViewGroup container, Bundle savedInstanceState) {
    	//---Inflate the layout for this fragment---    
    	ViewGroup rootView = (ViewGroup) inflater.inflate(
    			R.layout.rc_fragment_voice_control, container, false);
    	speechHelpLayout = (LinearLayout)rootView.findViewById(R.id.rc_frag_voice_ll_speech_help);
    	speechHelpBtn = (ImageView)getActivity().findViewById(R.id.rc_main_frag_titlebar_iv_speech_help);
		speechManager = new SpeechManager(getActivity(),mHandler);
		speechSender = new SpeechSender(getActivity());
		textView = (TextView) rootView.findViewById(R.id.rc_frag_voice_tv_content);
		volumeImageView = (ImageView)rootView.findViewById(R.id.rc_frag_voice_iv_voice_volume);
		voiceButton = (Button)rootView.findViewById(R.id.rc_frag_voice_btn_press_speak);
		
		//设置ImageView大小
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int ImageViewHeight = dm.widthPixels/4;
		LinearLayout.LayoutParams volumeImgParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ImageViewHeight);//这里设置params的高度。
		volumeImageView.setLayoutParams(volumeImgParams);
		
		initSpeechHelp(rootView);
		
		/**
		 * 语音按钮Listener
		 */
		voiceButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN ){
					if(!Helper.checkWifiStatus(getActivity())){
						sendFailMsgDelay(getResources().getString(R.string.SPEECH_CHECK_WIFI_Connection));
						return true;
					}else if(MainFragmentRemote.sendClient == null){
						sendFailMsgDelay(getResources().getString(R.string.SPEECH_CHECK_TV_Connection));
						return true;
					}
				}
				
				if(event.getAction() == MotionEvent.ACTION_DOWN ){
					speechManager.startSpeechListener();
					textView.setText("");
					textView.setVisibility(View.VISIBLE);
					volumeImageView.setVisibility(View.VISIBLE);
					voiceButton.setText(getActivity().getString(R.string.speech_release_to_stop));
					voiceButton.setBackgroundResource(R.drawable.rc_vc_shape_stroke_pressed);
					speechHelpBtn.setBackgroundResource(R.drawable.rc_vc_speech_help_normal);
					
					if(speechHelpLayout.getVisibility() == View.VISIBLE){
						hideSpeechHelp();
					}
					
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					if(speechManager.isSpeechListenerListenering()){
						speechManager.stopSpeechListener();
						disableSpeechButton();
					}
					voiceButton.setBackgroundResource(R.drawable.rc_vc_shape_stroke);
					volumeImageView.setVisibility(View.INVISIBLE);
					
				}
				return true;
			}
		});
		
		/**
		 * 帮助按钮Listener
		 */
		speechHelpBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(speechHelpLayout.getVisibility() == View.INVISIBLE){
					showSpeechHelp();
					textView.setVisibility(View.INVISIBLE);
					speechHelpBtn.setBackgroundResource(R.drawable.rc_vc_speech_help_selected);
				}else{
					hideSpeechHelp();
					textView.setVisibility(View.VISIBLE);
					speechHelpBtn.setBackgroundResource(R.drawable.rc_vc_speech_help_normal);
				}
			}
		});
		
		toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        return rootView;
    }
	
	/**
	* 方法描述：显示帮助信息
	 */
	private void showSpeechHelp(){
		speechHelpLayout.setVisibility(View.VISIBLE);
		TranslateAnimation translateAnimation = 
				new TranslateAnimation(0f, 0f,-speechHelpHeight,0f);  
        //设置动画时间  
        translateAnimation.setDuration(700);  
        speechHelpLayout.startAnimation(translateAnimation); 
        textView.setText("");
	}
	
	/**
	* 方法描述：隐藏帮助信息
	 */
	private void hideSpeechHelp(){
		speechHelpLayout.setVisibility(View.INVISIBLE);
		TranslateAnimation translateAnimation = 
				new TranslateAnimation(0f, 0f,0f,-speechHelpHeight);  
        //设置动画时间  
        translateAnimation.setDuration(700);  
        speechHelpLayout.startAnimation(translateAnimation); 
	}
	
    @Override
    public void onDetach() {
    	releaseResource();
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
	
	/**
	* @param volume 声音大小
	* 方法描述： 通过声音大小改变音量图片
	 */
	private void changeVolumeImg(int volume){
		int imgNo = volume/5;
		switch(imgNo){
			case 0:
				volumeImageView.setImageResource(R.drawable.rc_vc_speake_volume_0);
			break;
			case 1:
				volumeImageView.setImageResource(R.drawable.rc_vc_speake_volume_1);
			break;
			case 2:
				volumeImageView.setImageResource(R.drawable.rc_vc_speake_volume_2);
			break;
			case 3:
				volumeImageView.setImageResource(R.drawable.rc_vc_speake_volume_3);
			break;
			case 4:
				volumeImageView.setImageResource(R.drawable.rc_vc_speake_volume_4);
			break;
			case 5:
				volumeImageView.setImageResource(R.drawable.rc_vc_speake_volume_5);
			break;
		}
		
	}
	
	/**
	* 方法描述：释放资源
	 */
	private void releaseResource(){
		if(speechManager.isSpeechListenerListenering()){
			speechManager.stopSpeechListener();
		}
		
		speechManager.destorySpeechListener();
	}
	
	/**
	* 方法描述：初始化帮助 
	* @param rootView
	 */
	private void initSpeechHelp(View rootView){
		mViewPager = (ViewPager)rootView.findViewById(R.id.rc_frag_voice_vp_voicePager);
		initViewPager();
		initCircleButton(rootView);
		initLayout(rootView);
	}
	
	/**
	* 方法描述：初始化帮助布局 
	* @param rootView
	 */
	private void initLayout(View rootView) {
		// TODO Auto-generated method stub
		
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int ActivityWidth = dm.widthPixels ;//高度

		/**
		 * 设置ViewPager大小
		 */
		LinearLayout viewPagerContainer = (LinearLayout)rootView.findViewById(R.id.rc_frag_voice_ll_pager_container);
		int ViewPagerWidth  = (ActivityWidth/4)*3;
		int ViewPagerHeight = ActivityWidth/3; 
		int pagerMargin = (ActivityWidth - ViewPagerWidth)/2;
		LinearLayout.LayoutParams pagerParams = new LinearLayout.LayoutParams(ViewPagerWidth, ViewPagerHeight);
		pagerParams.setMargins(pagerMargin, 10, pagerMargin, 0);
		viewPagerContainer.setLayoutParams(pagerParams);
		viewPagerContainer.setGravity(Gravity.CENTER_HORIZONTAL);
		
		/**
		 * 设置小圆点位置
		 */
		LinearLayout.LayoutParams pointParams = new LinearLayout.LayoutParams
				(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		int pointLeft = pagerMargin + ViewPagerWidth - POINTWIDTH*3;
		Log.d(TAG,"pointLeft = " + pointLeft);
		pointParams.setMargins(pointLeft, 20, pagerMargin, 0);
		circleImageLayout.setLayoutParams(pointParams);
	
		/**
		 * 设置帮助整体布局大小
		 */
		speechHelpHeight =  ViewPagerHeight + POINTHEIGHT +40;
		RelativeLayout.LayoutParams speechHelpParams = new RelativeLayout.LayoutParams
				(RelativeLayout.LayoutParams.MATCH_PARENT, speechHelpHeight);
		speechHelpLayout.setLayoutParams(speechHelpParams);
	}
	
	/**
	* 方法描述：初始化提示用ViewPager 
	 */
	private void initViewPager(){
		manTextViews = new ArrayList<View>();
		manTextView = new TextView(getActivity().getApplicationContext()); 
		manTextView.setText(getActivity().getResources().getString(R.string.SPEECH_WORD_Help_Page_1));
		manTextView.setTextSize(TEXTSIZE);
		manTextViews.add(manTextView);
		manTextView = new TextView(getActivity().getApplicationContext()); 
		manTextView.setText(getActivity().getResources().getString(R.string.SPEECH_WORD_Help_Page_2));
		manTextView.setTextSize(TEXTSIZE);
		manTextViews.add(manTextView);
		manTextView = new TextView(getActivity().getApplicationContext()); 
		manTextView.setText(getActivity().getResources().getString(R.string.SPEECH_WORD_Help_Page_3));
		manTextView.setTextSize(TEXTSIZE);
		manTextViews.add(manTextView);
		mViewPager.setAdapter(new VoiceViewPagerAdapter(manTextViews));
		mViewPager.setOnPageChangeListener(onPageChangeListener);

	}
	
	/**
	* 方法描述：初始化ViewPager上面的三个小圆点 
	 */
	private void initCircleButton(View rootView){
		ImageView circleButton;
		circleImageLayout = (LinearLayout)rootView.findViewById(R.id.rc_frag_voice_recommend_circle_images);;
		circleButtons = new ArrayList<View>(manTextViews.size());
		
		for(int i=0;i<manTextViews.size();i++){
			circleButton = new ImageView(getActivity());
			circleButton.setLayoutParams(new LayoutParams(POINTWIDTH,POINTHEIGHT));   
			circleButtons.add(circleButton);
			
			if(i == 0){
				circleButton.setBackgroundResource(R.drawable.rc_page_indicator_focused);
			}else{
				circleButton.setBackgroundResource(R.drawable.rc_page_indicator);
			}
			
			circleImageLayout.addView(circleButton);
		}
	}
	
	/**
	 * 处理ViewPager中翻页事件
	 */
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			for(int i=0;i<circleButtons.size();i++){
				if(i == arg0){
					circleButtons.get(i).setBackgroundResource(R.drawable.rc_page_indicator_focused);
				}else{
					circleButtons.get(i).setBackgroundResource(R.drawable.rc_page_indicator);
				}
			}
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		
		@Override
		public void onPageScrollStateChanged(int state) {
		}
	};
    
  
}
