package com.casky.dlna.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.casky.smartremote.R;
import com.casky.dlna.control.DeviceDisplay;
import com.casky.dlna.control.DlnaManager;
import com.casky.dlna.main.RenderSelectCallBridge;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
*    
* 项目名称：Smart_DLNA   
* 类名称：RenderDialog   
* 类描述：   
* 创建人：shaojiansong   
* 创建时间＄1�714-9-26 下午4:04:37   
* 修改人：shaojiansong   
* 修改时间＄1�714-9-26 下午4:04:37   
* 修改备注＄1�7   
* 版本＄1�7 1.0   
*
 */
public class RenderDialog{
	private static RenderDialog mRenderDialog;
	private SimpleAdapter adapter;
	private ListView mListView;
	private ArrayList<HashMap<String, DeviceDisplay>> mArrayList;
	private Dialog mDialog;
	private LayoutInflater inflater;
	private View viewDialog;
	private Context mContext;
	private int screenWidth;
	private int screenHeight;
	private LayoutParams layoutParams;
	private DeviceDisplay deviceDisplay;
	private DlnaManager dlnaManager;
	private int playIndex = -1;
	private static boolean isDlnaPlaying = false;
	private RenderDialog(Context context){
		this.mContext = context;
		prepare();
	}
	public static RenderDialog getInstance(Context context){
		if (mRenderDialog == null) {
			return new RenderDialog(context);
		}
		return mRenderDialog;
	}
	public void prepare(){
		mDialog = new Dialog(mContext,R.style.DLNA_Render_Dialog);

		mDialog.setTitle(mContext.getResources().getString(R.string.dlna_render_title));
		getWindowMetric();
		inflater = LayoutInflater.from(mContext);
		viewDialog = inflater.inflate(R.layout.dlna_control_render_dialog, null);
		layoutParams = new LayoutParams(screenWidth * 90 / 100,LayoutParams.WRAP_CONTENT);
		mListView = (ListView) viewDialog.findViewById(R.id.dlna_control_render_items);
		mArrayList = DlnaManager.getDlnaManagerInstance(mContext).getArrayList();
		adapter = new SimpleAdapter(mContext, mArrayList, R.layout.dlna_control_render_item, new String []{"Device"}, new int []{R.id.name});
		mListView.setAdapter(adapter);
		dlnaManager = DlnaManager.getDlnaManagerInstance(mContext.getApplicationContext());
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				HashMap<String, DeviceDisplay> mHashMap = mArrayList.get(arg2);
				deviceDisplay = mHashMap.get("Device");
				dlnaManager.getDlnaServiceManager().setRenderDevice(deviceDisplay);
				isDlnaPlaying = RenderSelectCallBridge.getInstance().invokeMethod();
				//isDlnaPlaying = true;
				mDialog.dismiss();
			}
		});
	}
	
    private void getWindowMetric(){
    	WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    	screenWidth = wm.getDefaultDisplay().getWidth();
    	screenHeight = wm.getDefaultDisplay().getHeight();
    }
    
    public void showRenderDialog(){
    	mDialog.setContentView(viewDialog, layoutParams);
		mDialog.show();
		isDlnaPlaying = false;
    }

//	public boolean isDlnaPlaying(){
//		return isDlnaPlaying;
//	}
	
	public boolean isDeviceSet(){
		return (deviceDisplay != null);
	}
	
//	public void stopDlnaPlay(){
//		isDlnaPlaying = false;
//	}
}
