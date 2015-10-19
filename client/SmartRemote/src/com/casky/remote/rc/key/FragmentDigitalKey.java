package com.casky.remote.rc.key;


import java.lang.reflect.Field;

import com.casky.smartremote.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * 
*    
* 项目名称：SmartRemote   
* 类名称：FragmentDigitalKey   
* 类描述：数字键的Fragment   
* 创建人：shaojiansong   
* 创建时间：2014-8-25 下午2:15:40   
* 修改人：shaojiansong   
* 修改时间：2014-8-25 下午2:15:40   
* 修改备注：   
* 版本： 1.0   
*
 */
public class FragmentDigitalKey extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, 
    ViewGroup container, Bundle savedInstanceState) {
    	//---Inflate the layout for this fragment---    
    	ViewGroup rootView = (ViewGroup) inflater.inflate(
    			R.layout.rc_key_page_digital, container, false);
        return rootView;
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
