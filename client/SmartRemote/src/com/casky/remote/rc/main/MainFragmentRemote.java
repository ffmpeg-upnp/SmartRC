package com.casky.remote.rc.main;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.casky.main.slidingmenu.MainActivity;
import com.casky.main.slidingmenu.MainMenuUIUpdater;
import com.casky.remote.rc.gesture.FragmentGesture;
import com.casky.remote.rc.kbmouse.FragmentMouse;
import com.casky.remote.rc.key.FragmentKeyMain;
import com.casky.remote.rc.network.Client;
import com.casky.remote.rc.network.PingResult;
import com.casky.remote.rc.searchdevice.DeviceList;
import com.casky.remote.rc.searchdevice.DeviceListActivity;
import com.casky.remote.rc.searchdevice.Finder;
import com.casky.remote.rc.speech.FragmentVoiceControl;
import com.casky.remote.setting.Hapticswitch;
import com.casky.remote.setting.Preference;
import com.casky.smartremote.R;
import com.casky.remote.utils.Helper;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

/**
 * 
*    
* 项目名称：SmartRemote   
* 类名称：FragmentRemote   
* 类描述：基础遥控器类   
* 创建人：shaojiansong   
* 创建时间＄1�714-8-25 下午2:09:18   
* 修改人：shaojiansong   
* 修改时间＄1�714-8-25 下午2:09:18   
* 修改备注＄1�7   
* 版本＄1�7 1.0    
*
 */
@SuppressLint("NewApi")
public class MainFragmentRemote extends Fragment{
	
	private static final String TAG = "RC_Main";
	
	public static final int TV_IP_MESSAGE = 0x1001;
	public static final int SEARCH_DEVICE_MESSAGE = 0x1002;
	public static final int WIFI_DISABLED_MESSAGE = 0x1003;
	public static final int TV_LOST_CONNECTION_MESSAGE = 0x1004;
	public static final int DEVICE_SEARCH_COMPELET_MESSAGE = 0x1005;
	public static final int DEVICE_SET_MESSAGE = 0x1006;
	public static final int NODEVICE_FOUND_MESSAGE = 0x1007;
	public static final int SEART_HEART_BEAT_MESSAGE = 0x1008;
	
	private ProgressBar searchProgressBar;
	private Button buttonConnect;
	private Button buttonMainMenu;
	//public final static String EXTRA_MESSAGE_IP = "com.casky.TVIP";
	public final static String DEVICE_LIST = "com.casky.devicelist";
	public static Client sendClient = null;
	private List<PingResult> ping;
	private String broadcastIp = null;
	private Finder finder = null;
	private DeviceList mDeviceList = null;
	private NetStatus netStatus;
	private android.support.v4.app.FragmentTabHost mTabHost;
	private View mainFragmentview;
	static String btnText;
	static int volumeBtnStatus;
	static int selectStatus=0;
	//定义丄1�7个布屄1�7
	//private View layoutInflater;
	
	//定义数组来存放Fragment界面
	private Class<?> fragmentArray[] = {FragmentKeyMain.class,FragmentGesture.class,FragmentMouse.class,FragmentVoiceControl.class};
	//定义数组来存放按钮图牄1�7
	private int mImageViewArray[] = {R.drawable.rc_main_tab_key_btn,R.drawable.rc_tab_gesture_btn,R.drawable.rc_main_tab_mouse_btn,
									 R.drawable.rc_main_tab_voice_btn};
	//Tab选项卡的文字
	private String mTextviewArray[] = null;
	//private Toast toast = null;	
	
	@SuppressLint("HandlerLeak")
	Handler  myHandler=new Handler() 
    {
		@Override 
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
				switch (msg.what)
				{
				case TV_IP_MESSAGE: /*receive TV IP, and add to IP list*/
					try 
					{
						PingResult pr = (PingResult)msg.obj;
						ping.add(pr);
					} 
					catch (Exception e) 
					{
						Log.e(TAG, "Exception happend!");
					}
					break;
				case SEARCH_DEVICE_MESSAGE: /*start search devices, and set progressBar visible*/
					searchProgressBar.setVisibility(View.VISIBLE);
					buttonConnect.setText(null);
					ping.clear();
					break;
				case WIFI_DISABLED_MESSAGE: /*WIFI has not been opened*/
					if (sendClient != null) {
						sendClient.closeClient();
						sendClient = null;
					}
					ping.clear();
					buttonConnect.setText(R.string.disconnect);
					Toast.makeText(getActivity(), R.string.turnon_wifi, Toast.LENGTH_SHORT).show();
					break;
				case TV_LOST_CONNECTION_MESSAGE: /*TV lost connect*/
					if (sendClient != null) {
						sendClient.closeClient();
						sendClient = null;
					}
					ping.clear();
					buttonConnect.setText(R.string.disconnect);
					Toast.makeText(getActivity(), R.string.loseclient, Toast.LENGTH_SHORT).show();
					break;
				case DEVICE_SEARCH_COMPELET_MESSAGE: /*devices search been completed, and show devices list*/
					searchProgressBar.setVisibility(View.GONE);
					mDeviceList.setData(ping);
					Intent intent = new Intent(getActivity(), DeviceListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(DEVICE_LIST, (Serializable) ping);
					intent.putExtras(bundle);
					startActivityForResult(intent, 0);
					break;
				case DEVICE_SET_MESSAGE: /*select a device, show connected*/
					buttonConnect.setText(R.string.connected);
					break;
				case NODEVICE_FOUND_MESSAGE: /*devices search been completed, but can't find device*/
					searchProgressBar.setVisibility(View.GONE);
					buttonConnect.setText(R.string.disconnect);
					Toast.makeText(getActivity(), R.string.no_found_device, Toast.LENGTH_SHORT).show();
					break;
				case SEART_HEART_BEAT_MESSAGE: /*start send HeartBeat broadcast packet*/
					Intent mIntent = new Intent("com.casky.TV.disc");
					getActivity().sendBroadcast(mIntent);
					break;
				
				default:
					break;
				}
		}
    };
    
    public void onActivityCreated(Bundle savedInstanceState)  
    {  
        super.onCreate(savedInstanceState);  
        Log.d(TAG, "onCreate");  
        
        initTitlebarBtn();
    } 
    
    @Override
    public View onCreateView(LayoutInflater inflater, 
    		ViewGroup container, Bundle savedInstanceState) {
    	//---Inflate the layout for this fragment---    
    	Log.d(TAG, "onCreateView!");
    	
    	String key = getActivity().getResources().getString(R.string.rc_mainFrag_key);
    	String gesture = getActivity().getResources().getString(R.string.rc_mainFrag_gesture);
    	String kmouse = getActivity().getResources().getString(R.string.rc_mainFrag_kmouse);
    	String speech = getActivity().getResources().getString(R.string.rc_mainFrag_speech);
    	
    	mTextviewArray = new String[]{key, gesture, kmouse,speech};
    	
    	//if(mainFragmentview == null){
    		mainFragmentview = inflater.inflate(R.layout.rc_main_fragment, null);
    	
		initView();
		regNetStatusListener(myHandler);
		startSearchDevice();

		return mainFragmentview;
    }
    
    /**
	 * 初始化组仄1�7
	 */
	
	private void initView(){
				
		//实例化TabHost对象，得到TabHost
		mTabHost = (FragmentTabHost)mainFragmentview.findViewById(android.R.id.tabhost);
		mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.rc_main_frag_fl_realtabcontent);

		//得到fragment的个敄1�7
		int count = fragmentArray.length;
				
		for(int i = 0; i < count; i++){	
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			//设置Tab按钮的背晄1�7
			mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.rc_main_tab_background);
		}
	}
	
	@SuppressLint("CutPasteId")
	private void initTitlebarBtn()
	{
		
		buttonConnect = (Button)getActivity().findViewById(R.id.rc_main_frag_titlebar_btn_connect);
		buttonMainMenu = (Button)getActivity().findViewById(R.id.rc_main_frag_titlebar_btn_mainmenu);
		
		buttonConnect.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if(!searchProgressBar.isShown()){
					startSearchDevice();
				}
			}
		});
		
		buttonMainMenu.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				Log.v(TAG,"buttonMainMenu has been clicked!");//("TAG","buttonMainMenu has been clicked!");
				MainMenuUIUpdater UIupdater = new MainMenuUIUpdater();
				UIupdater.sendMessage(MainMenuUIUpdater.TOGGLE);
			}
		});


		ping = Collections.synchronizedList(new ArrayList<PingResult>());
		searchProgressBar = (ProgressBar)getActivity().findViewById(R.id.rc_main_frag_titlebar_pb_progress_search);
		searchProgressBar.setVisibility(View.GONE);
		//buttonConnect = (Button)getActivity().findViewById(R.id.rc_main_frag_titlebar_btn_connect);
		buttonConnect.setText(R.string.disconnect);
		recSavedData();
		mDeviceList = new DeviceList();
		
		//toast = Toast.makeText(getActivity().getApplicationContext(), "", Toast.LENGTH_SHORT);
		
		
	}	
	
	private View getTabItemView(int index) {
		LayoutInflater tabLayoutInflater = LayoutInflater.from(getActivity());
		View view = tabLayoutInflater.inflate(R.layout.rc_main_tab_layout, null);

		ImageView imageView = (ImageView) view.findViewById(R.id.rc_main_tab_iv_tabImage);
		imageView.setImageResource(mImageViewArray[index]);

		TextView textView = (TextView) view.findViewById(R.id.rc_main_tab_tv_tabText);
		textView.setText(mTextviewArray[index]);

		return view;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v(TAG, "requestCode:"+requestCode+"resultCode:" + resultCode + "data:"+data);
		if (resultCode == -1){
			sendClient = new Client(data.getExtras().getString(MainActivity.EXTRA_MESSAGE_IP),myHandler);
		}
		else{
			if(sendClient != null){
				sendClient.closeClient();
				sendClient = null;
			}
			buttonConnect.setText(R.string.disconnect);
		}
	}
	
	public void startSearchDevice()
    {
		if(Helper.checkWifiStatus(getActivity())){
			broadcastIp = Helper.getDhcpIpString(getActivity());
			if(broadcastIp != null) {
				finder = new Finder(broadcastIp);
				//timer = new Timer();
				finder.startMulticast();
				finder.startListener(myHandler);
	            myHandler.sendMessage(Helper.createMessage(MainFragmentRemote.SEARCH_DEVICE_MESSAGE, null));
			}else {
				Toast.makeText(getActivity(), R.string.not_get_dhcp, Toast.LENGTH_SHORT).show();
				return;
			}
		}else {
			Log.d(TAG, "wifi turn off!");
			Toast.makeText(getActivity(), R.string.turnon_wifi, Toast.LENGTH_SHORT).show();

			return;
		}
	}
	
	/**
     * 注册网络状�1�7�监听器
     * @param mHandler
     */
    private void regNetStatusListener(Handler mHandler) {
    	netStatus = new NetStatus(mHandler);
    	IntentFilter netIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    	IntentFilter netDisFilter = new IntentFilter("com.casky.TV.disc");
    	getActivity().registerReceiver(netStatus, netIntentFilter);
    	getActivity().registerReceiver(netStatus, netDisFilter);
    }
    
	private void recSavedData(){
		//mLastIpString = getActivity().getIntent().getStringExtra(SplashActivity.LASTIP);
		boolean sw_status = Preference.get_preferences(getActivity().getSharedPreferences("SmartRemote",Context.MODE_PRIVATE),"Hapticswitch", true);
		Hapticswitch.set_hs_onoff(sw_status);
		//Hapticswitch.set_hs_onoff(getActivity().getIntent().getBooleanExtra(SplashActivity.HAPTICSWITCH, true));
	}
	
	/**
	 * 
	*    
	* 项目名称：SmartRemote   
	* 类名称：NetStatus   
	* 类描述：网络状�1�7�监听类   
	* 创建人：shaojiansong   
	* 创建时间＄1�714-8-25 下午2:14:07   
	* 修改人：shaojiansong   
	* 修改时间＄1�714-8-25 下午2:14:07   
	* 修改备注＄1�7   
	* 版本＄1�7 1.0    
	*
	 */
	public class NetStatus extends BroadcastReceiver{
		private Handler mHandler;
		public NetStatus(Handler handler){
			mHandler = handler;
		}
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if (arg1.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
			if (!Helper.checkWifiStatus(arg0)) {
					mHandler.sendMessage(Helper.createMessage(WIFI_DISABLED_MESSAGE, null));
				}
			}else if (arg1.getAction().equals("com.casky.TV.disc")) {
				mHandler.sendMessage(Helper.createMessage(TV_LOST_CONNECTION_MESSAGE, null));
			}

		}

	}
	/*
	private void showTip(String str){
		//toast.setDuration(Toast.LENGTH_LONG);
		//toast.setText(str);
		//toast.show();
	}*/
	
	@Override
	public void onDetach() {
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
	@Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }
}
