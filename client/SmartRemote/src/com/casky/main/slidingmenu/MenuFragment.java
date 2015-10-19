package com.casky.main.slidingmenu;

import java.util.ArrayList;
import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.casky.smartremote.R;

public class MenuFragment extends Fragment implements OnItemClickListener {

	private ListView mDrawerList;
	private String[] mNavMenuTitles;
	private TypedArray mNavMenuIconsTypeArray;
	private ArrayList<NavDrawerItem> mNavDrawerItems;
	private NavDrawerListAdapter mAdapter;
	private SLMenuListOnItemClickListener mCallback;
	private int selected = -1;

	@Override
	public void onAttach(Activity activity) {
		try {
			mCallback = (SLMenuListOnItemClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnResolveTelsCompletedListener");
		}
		super.onAttach(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.main_sliding_menu, null);
		
		findView(rootView);
		
		return rootView;
	}

	private void findView(View rootView) {
		
		mDrawerList = (ListView) rootView.findViewById(R.id.sliding_menu_lv_left_menu);
		
		mNavMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);  
        // nav drawer icons from resources  
        mNavMenuIconsTypeArray = getResources()  
                    .obtainTypedArray(R.array.nav_drawer_icons);  
              
        mNavDrawerItems = new ArrayList<NavDrawerItem>();  
  
        // adding nav drawer items to array  
        // Home  
        mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[0], mNavMenuIconsTypeArray  
                .getResourceId(0, -1)));  
        // Find People  
        mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[1], mNavMenuIconsTypeArray  
                .getResourceId(1, -1)));  
        // Photos  
        mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[2], mNavMenuIconsTypeArray  
                .getResourceId(2, -1)));  
        // Communities, Will add a counter here  
        mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[3], mNavMenuIconsTypeArray  
                .getResourceId(3, -1)));  
  
        // Recycle the typed array  
        mNavMenuIconsTypeArray.recycle();  
          
        // setting the nav drawer list adapter  
        mAdapter = new NavDrawerListAdapter(getActivity(),  
                        mNavDrawerItems);  
        mDrawerList.setAdapter(mAdapter);  
        mDrawerList.setOnItemClickListener(this);  
        
        if(selected!=-1){
        	mDrawerList.setItemChecked(selected, true);  
            mDrawerList.setSelection(selected);  
        }else{
        	mDrawerList.setItemChecked(0, true);  
            mDrawerList.setSelection(0);  
        }
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		// update selected item and title, then close the drawer  
        mDrawerList.setItemChecked(position, true);  
        mDrawerList.setSelection(position);  
        
        if(mCallback!=null){
        	mCallback.selectItem(position, mNavMenuTitles[position]);
        }
        selected = position;
        mAdapter.setCurSelectFrag(position);
	}

	/**
     * 左侧菜单 点击回调接口
     * @author FX_SKY
     *
     */
    public interface SLMenuListOnItemClickListener{
    	
    	public void selectItem(int position,String title);
    }
}
