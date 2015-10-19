package com.casky.remote.rc.searchdevice;

import java.util.List;

import com.casky.main.slidingmenu.MainActivity;
import com.casky.remote.rc.main.MainFragmentRemote;
import com.casky.remote.rc.network.PingResult;
import com.casky.smartremote.R;
import com.casky.remote.utils.Helper;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;
/**
 * 
*    
* 项目名称：SmartRemote   
* 类名称：DeviceListActivity   
* 类描述：显示获取到的Tv设备列表    
* 创建人：shaojiansong   
* 创建时间：2014-8-25 下午2:04:44   
* 修改人：shaojiansong   
* 修改时间：2014-8-25 下午2:04:44   
* 修改备注：   
* 版本： 1.0    
*
 */
public class DeviceListActivity extends ListActivity{
	//private static String TAG = "syo"; 
	private List<PingResult> dataList = null;
	private SimpleAdapter adapter= null;
	private LNetStatus mlNetStatus;
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rc_devices_list);
		dataList = (List<PingResult>) getIntent().getSerializableExtra(MainFragmentRemote.DEVICE_LIST);
        adapter = new SimpleAdapter(this,dataList,R.layout.rc_devices_list_item,new String[]{"IP","HostName"},new int[]{R.id.rc_device_list_item_tv_ip,R.id.rc_device_list_item_tv_hostname});
        adapter.setViewBinder(IPViewBinder);
        setListAdapter(adapter);
		regNetStatusListener();
	}
	protected void onResume() {
		super.onResume();
	}
	protected void onPause() {
		super.onPause();
	}
	protected void onStop() {
		super.onStop();
	}
	protected void onDestroy(){
		unregisterReceiver(mlNetStatus);
		super.onDestroy();
	}
	private ViewBinder IPViewBinder = new ViewBinder(){
		
		@Override
		public boolean setViewValue(View view, Object data, String textRepresentation) 
		{
			view.setOnClickListener(new myOnClickListener());
			// TODO Auto-generated method stub
			return false;
		}
		
		class myOnClickListener implements OnClickListener
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String IP = (String) ((TextView)((View)v.getParent()).findViewById(R.id.rc_device_list_item_tv_ip)).getText();
				//Helper.log(IP);
				//new Connect(IP).startConnect(mHandler);
				Intent intent = new Intent();
				intent.putExtra(MainActivity.EXTRA_MESSAGE_IP, IP);
				setResult(RESULT_OK, intent);
				finish();
			}
		}
	};
	public void onBackPressed(){
		Intent intent = new Intent();
		setResult(RESULT_CANCELED, intent);
		finish();
	}
	public class LNetStatus extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if (!Helper.checkWifiStatus(arg0)){
				onBackPressed();
			}
		}
	}
	private void regNetStatusListener() {
        mlNetStatus = new LNetStatus();
        IntentFilter netIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        //IntentFilter netDisFilter = new IntentFilter("com.casky.TV.disc");
        //this.registerReceiver(mlNetStatus, netDisFilter);
    	this.registerReceiver(mlNetStatus, netIntentFilter);
	}
}
