package com.casky.remote.rc.key;

import java.lang.reflect.Field;

import com.casky.smartremote.R;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
/**
 * 
*    
* 项目名称：SmartRemote   
* 类名称：FragmentRemote   
* 类描述：基础遥控器类   
* 创建人：shaojiansong   
* 创建时间：2014-8-25 下午2:09:18   
* 修改人：shaojiansong   
* 修改时间：2014-8-25 下午2:09:18   
* 修改备注：   
* 版本： 1.0    
*
 */
@SuppressLint("NewApi")
public class FragmentKeyMain extends Fragment{
    private ViewPager viewPager;
    private ImageView imageView;
    private ImageView[] imageViews;
    private ViewGroup remoteViewGroup;
    private ViewGroup viewPoints;
    private String TAG = "mylog";
    private FragmentChannelVolKey normalKeyFragment = new FragmentChannelVolKey();
    private Fragment digitalKeyFragment = new FragmentDigitalKey();
    private Fragment frequentKeyFragment = new FragmentFrequentKey();
    @Override
    public View onCreateView(LayoutInflater inflater, 
    ViewGroup container, Bundle savedInstanceState) {
    	//---Inflate the layout for this fragment---    
    	remoteViewGroup = (ViewGroup) inflater.inflate(
                R.layout.rc_key_main_fragment, container, false);
    	viewPoints = (ViewGroup) remoteViewGroup.findViewById(R.id.rc_key_main_frag_ll_viewGroup);
        return remoteViewGroup;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageViews = new ImageView[3];
    	viewPager = (ViewPager) getView().findViewById(R.id.rc_key_main_frag_vp_vPager);
    	GuidePageAdapter mGuidePageAdapter = new GuidePageAdapter(getChildFragmentManager());
        // Inflate the layout for this fragment
        viewPager.setAdapter(mGuidePageAdapter);
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());
	   	for(int i=0;i<3;i++) {
			 imageView = new ImageView(getActivity());
			 imageView.setLayoutParams(new LayoutParams(20,20));
			 imageView.setPadding(20, 0, 20, 0);
			 imageViews[i] = imageView;
		     if(i==0)
		     {
		    		imageViews[i].setBackgroundResource(R.drawable.rc_page_indicator_focused);
		     }
		     else
		     {
		    		imageViews[i].setBackgroundResource(R.drawable.rc_page_indicator);
		     }
	     	viewPoints.addView(imageViews[i]);
		 }
        Log.d(TAG, "onActivityCreated");
    }
    /**
     * 
    *    
    * 项目名称：SmartRemote   
    * 类名称：GuidePageAdapter   
    * 类描述：实现左右滑动的Adapter   
    * 创建人：shaojiansong   
    * 创建时间：2014-8-25 下午2:10:02   
    * 修改人：shaojiansong   
    * 修改时间：2014-8-25 下午2:10:02   
    * 修改备注：   
    * 版本： 1.0    
    *
     */
    class GuidePageAdapter extends FragmentPagerAdapter{
    	public GuidePageAdapter(FragmentManager fm) {
            super(fm);
        }

		@Override
		public Fragment getItem(int arg0) {
			Log.d(TAG, "getItem"+arg0);
			// TODO Auto-generated method stub
		      if (arg0 == 0) {
		    	  	return frequentKeyFragment;
	            } else if(arg0 == 1){
	                return normalKeyFragment;
	            }
		       else {
				return digitalKeyFragment;
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3;
		}
    }
    /**
     * 
    *    
    * 项目名称：SmartRemote   
    * 类名称：GuidePageChangeListener   
    * 类描述：实现导航图标的切换   
    * 创建人：shaojiansong   
    * 创建时间：2014-8-25 下午2:10:51   
    * 修改人：shaojiansong   
    * 修改时间：2014-8-25 下午2:10:51   
    * 修改备注：   
    * 版本： 1.0    
    *
     */
    class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			normalKeyFragment.setMoveDown(false);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int arg0) {
			Log.d(TAG, "onPageSelected"+arg0);
			// TODO Auto-generated method stub
			for(int i=0;i<imageViews.length;i++){
				imageViews[arg0].setBackgroundResource(R.drawable.rc_page_indicator_focused);
				if(arg0 !=i){
					imageViews[i].setBackgroundResource(R.drawable.rc_page_indicator);
				}
			}
		}
    	
    }
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
}
