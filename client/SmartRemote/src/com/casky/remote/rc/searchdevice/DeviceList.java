package com.casky.remote.rc.searchdevice;

import java.util.List;

import com.casky.remote.rc.network.PingResult;
import com.casky.smartremote.R;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleAdapter.ViewBinder;
/**
 * 
*    
* 项目名称：SmartRemote   
* 类名称：DeviceList   
* 类描述： 显示获取到的Tv设备列表 ，项目中该类暂未使用
* 创建人：shaojiansong   
* 创建时间：2014-8-25 下午2:02:52   
* 修改人：shaojiansong   
* 修改时间：2014-8-25 下午2:02:52   
* 修改备注：   
* 版本： 1.0   
*
 */
public class DeviceList extends ListFragment{
	private List<PingResult> dataList = null;
	private SimpleAdapter adapter= null;
	private int fragmentId;
	private String LogTag = "syo";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rc_devices_list, container, false);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SimpleAdapter(getActivity(),dataList,R.layout.rc_devices_list_item,new String[]{"IP","HostName"},new int[]{R.id.rc_device_list_item_tv_ip,R.id.rc_device_list_item_tv_hostname});
        adapter.setViewBinder(IPViewBinder);
        setListAdapter(adapter);
        
    }
    public void setData(List<PingResult> data) {
    	dataList = data;
	}
    public void onListItemClick(ListView parent, View v, 
            int position, long id) {
        Toast.makeText(getActivity(), 
                "You have selected " + position,
                Toast.LENGTH_SHORT).show();
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
				Log.d(LogTag, "selected ip is " + IP);
			}
		}
	};
	public void backFragmentId(int id)
	{
		fragmentId = id;
	}
	public int getBackFragmentId()
	{
		return fragmentId;
	}
}
