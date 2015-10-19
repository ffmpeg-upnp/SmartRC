package com.casky.main.slidingmenu;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.casky.smartremote.R;

public class NavDrawerListAdapter extends BaseAdapter {
	
	private Context context;
	private List<NavDrawerItem> navDrawerItems;
    private int curSelectFrag = 0;
	
	public NavDrawerListAdapter(Context context, List<NavDrawerItem> navDrawerItems){
		this.context = context;
		this.navDrawerItems = navDrawerItems;
	}

    public void setCurSelectFrag(int position){
        this.curSelectFrag = position;
    }

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.rc_main_drawer_list_item, null);
        }
         
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.rc_main_drawer_list_item_iv_icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.rc_main_drawer_list_item_tv_title);
        TextView txtCount = (TextView) convertView.findViewById(R.id.rc_main_drawer_list_item_tv_counter);
         
        imgIcon.setImageResource(navDrawerItems.get(position).getIcon());        
        txtTitle.setText(navDrawerItems.get(position).getTitle());
        
        // displaying count
        // check whether it set visible or not
        if(navDrawerItems.get(position).getCounterVisibility()){
        	txtCount.setText(navDrawerItems.get(position).getCount());
        }else{
        	// hide the counter view
        	txtCount.setVisibility(View.GONE);
        }

        if(position == curSelectFrag){
            convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.main_menu_list_item_bg_pressed));
        }else{
            convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.main_menu_list_item_bg_normal));
        }
        
        return convertView;
	}

}
