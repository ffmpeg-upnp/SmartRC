/**
 * 
 */
package com.casky.dlna.picture.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.casky.smartremote.R;
import com.casky.dlna.main.MainFragmentDLNA;
import com.casky.dlna.picture.sub.AlbumListFragment;
import com.casky.dlna.picture.sub.GalleryFragment;
import com.casky.dlna.picture.sub.SubFragmentPicture;

/** 
 * ��Ŀ��ƣ�Smart_DLNA
 * ����ƣ�PictureFragmentSwitcher  
 * �������� 
 * �����ˣ�wangbo
 * ����ʱ�䣺2014-10-10 ����4:33:26
 * �޸��ˣ�wangbo
 * �޸�ʱ�䣺2014-10-10 ����4:33:26
 * �汾�� 1.0    
 */
public class PictureFragmentSwitcher {
	
	public static final int FragmentDLNA = 0;
	public static final int FragmentAlbumList = 1;
	public static final int FragmentSubPicture = 2;
	public static final int FragmentGallery = 3;
	
	public static int CurrentFragment = 0;
	public static int LastFragment = 0;
	
	private FragmentManager fm = null;
	
	static int containerId = 0;
	
	public PictureFragmentSwitcher(FragmentManager fm){
		this.fm = fm;
		containerId = R.id.rc_main_frag_content_fl_content;
	}

    public static void setCurrentFragment(int fragIndex){
        CurrentFragment = fragIndex;
    }
	
	
	public void switchFragment(Fragment from,Fragment to){
				
		PictureFragmentSwitcher.LastFragment 
		= getFragmentIndex(from);
		
		PictureFragmentSwitcher.CurrentFragment 
		= getFragmentIndex(to);
		
		FragmentTransaction ft = fm.beginTransaction();
		
		if(!to.isAdded()){
			ft.hide(from).add(R.id.rc_main_frag_content_fl_content, to).commitAllowingStateLoss();
		}else{
			ft.hide(from).show(to).commitAllowingStateLoss();
		}	
		
		if(PictureFragmentSwitcher.LastFragment == PictureFragmentSwitcher.FragmentGallery){
			ft.remove(from);
		}
		
		System.out.println("LastFragment = " + LastFragment);
		System.out.println("CurrentFragment = " + CurrentFragment);
	}
	
	public int getFragmentIndex(Fragment frag){
		if(frag instanceof MainFragmentDLNA){
			return PictureFragmentSwitcher.FragmentDLNA;
		}else if(frag instanceof AlbumListFragment){
			return PictureFragmentSwitcher.FragmentAlbumList;
		}else if(frag instanceof SubFragmentPicture){
			return PictureFragmentSwitcher.FragmentSubPicture;
		}else if(frag instanceof GalleryFragment){
			return PictureFragmentSwitcher.FragmentGallery;
		}else{
			return PictureFragmentSwitcher.FragmentDLNA;
		}
	}
	


}
