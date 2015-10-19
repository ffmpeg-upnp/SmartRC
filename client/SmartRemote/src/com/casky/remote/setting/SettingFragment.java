package com.casky.remote.setting;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.casky.main.slidingmenu.MainMenuUIUpdater;
import com.casky.remote.rc.main.ExitApplication;
import com.casky.remote.setting.WiperSwitch.OnChangedListener;
import com.casky.smartremote.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SettingFragment extends Fragment
{

	View settingView;
	private Button buttonMainMenu;
	private static String TAG = "Setting"; 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		
		settingView = LayoutInflater.from(getActivity()).inflate(R.layout.setting_page, null);
		Log.v(getTag(), "settingView" + settingView);
		ListView lvMenu = (ListView) settingView.findViewById(R.id.setting_lv_List);
		MyAdapter mAdapter = new MyAdapter(getActivity());
		lvMenu.setAdapter(mAdapter);
		lvMenu.setOnItemClickListener(new OnItemClickListener(){		 
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int itemNumeber,
					long arg3) {
					Hapticswitch.configHaptics(arg1);	
					// TODO Auto-generated method stub
					if(itemNumeber==1)//softwareupdate
						{
						
						}
					else if(itemNumeber==2)//exit
						{
							ExitApplication.getInstance().exit(getActivity());
						}
					
				}});
   
		return settingView;	
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState)  
	{
		super.onCreate(savedInstanceState);
		
		buttonMainMenu = (Button)getActivity().findViewById(R.id.setting_tb_btn_MainMenu);
		
		buttonMainMenu.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				//Log.v(TAG,"buttonMainMenu has been clicked!");
				MainMenuUIUpdater UIupdater = new MainMenuUIUpdater();
				UIupdater.sendMessage(MainMenuUIUpdater.TOGGLE);
			}
		});

	}
		
	private List<? extends Map<String, ?>> getData() {
		// TODO Auto-generated method stub
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		  Map<String, Object> map = new HashMap<String, Object>();  
          map.put("imageView1", R.drawable.setting_item_vibrate);
          map.put("textView1",  "   " + getString(R.string.vibrationswitch));
          list.add(map);    
	      map = new HashMap<String, Object>();
	      map.put("imageView1", R.drawable.setting_item_updateicon);
	      map.put("textView1", "   " + getString(R.string.softwareupdate));  
	      list.add(map);     
	      map = new HashMap<String, Object>();
	      map.put("imageView1", R.drawable.setting_item_exiticon);
	      map.put("textView1", "   " + getString(R.string.exit));  
	      list.add(map);    
	      return list;
	}
	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//        case android.R.id.home:
//            //finish();
//            return true;
//        default:
//            return super.onOptionsItemSelected(item);
//		}
//	}
	
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


	/*      * BaseAdapter
     */ 
	private class MyAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<? extends Map<String, ?>> data;
    public MyAdapter(Context context) {
    		this.mInflater = LayoutInflater.from(context);
    		this.data = getData();
        }

        @Override
        public int getCount() {
            
            return data.size();
        }

        public Object getItem(int position) {
            return null;
        }
        public long getItemId(int position) {
            return 0;
        }
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewHolder holder;
			if(convertView == null){
				holder = new ViewHolder();  
				if(position != 0){
					convertView = mInflater.inflate(R.layout.setting_list_item, null);				                 
					holder.title = (TextView) convertView.findViewById(R.id.setting_list_itm_tv_text);
					holder.image = (ImageView) convertView.findViewById(R.id.setting_list_itm_iv_img);
					convertView.setTag(holder); 
					holder.title.setText(data.get(position).get("textView1").toString());
					holder.image.setImageResource((Integer)getData().get(position).get("imageView1"));
				}else{
					convertView = mInflater.inflate(R.layout.setting_list_item_vbi, null);
					holder.title = (TextView) convertView.findViewById(R.id.setting_list_itm_tv_vb);
					holder.image = (ImageView) convertView.findViewById(R.id.setting_list_itm_iv_vb);
					convertView.setTag(holder); 
					holder.title.setText(data.get(position).get("textView1").toString());
					holder.image.setImageResource((Integer)getData().get(position).get("imageView1"));
					WiperSwitch vib=(WiperSwitch)convertView.findViewById(R.id.setting_list_itm_ws_wiperS);

					vib.setChecked(Hapticswitch.get_hs_onoff());     
					OnChangedListener listeners = new OnChangedListener(){
						@Override
						public void OnChanged(WiperSwitch vib,boolean isChecked){
							if(isChecked){ 						
								Hapticswitch.set_hs_onoff(true);
								Preference.set_preferences(getActivity().getSharedPreferences("SmartRemote",Context.MODE_PRIVATE), "Hapticswitch", true);
								Hapticswitch.configHaptics(vib);				 						
							}
							else{					
								Hapticswitch.set_hs_onoff(false);
								Preference.set_preferences(getActivity().getSharedPreferences("SmartRemote",Context.MODE_PRIVATE), "Hapticswitch", false);
								Hapticswitch.configHaptics(vib);
							}
						}
					};
					vib.setOnChangedListener(listeners);
				}
			}	 			
            return convertView;
		}    


    }
    public final class ViewHolder{
    public TextView title;
    public ImageView image;
  //  public WiperSwitch vib;
    }

}
