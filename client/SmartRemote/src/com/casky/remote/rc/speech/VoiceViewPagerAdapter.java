/**
 * 
 */
package com.casky.remote.rc.speech;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 项目名称：SmartRemote
 * 类名称：VoiceViewPagerAdapter  
 * 类描述： 为提示信息ViewPager重写的Adapter
 * 创建人：wangbo
 * 创建时间：2014-9-5 下午4:58:11
 * 修改人：wangbo
 * 修改时间：2014-9-5 下午4:58:11
 * 修改备注：   
 * 版本： 1.0    
 *
 */
public class VoiceViewPagerAdapter extends PagerAdapter {

	private ArrayList<View> views = null;
	
	public VoiceViewPagerAdapter(ArrayList<View> views){
		this.views = views;
	}
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager)container).removeView(views.get(position));
	}


	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager)container).addView(views.get(position));
		return views.get(position);
	}

}
