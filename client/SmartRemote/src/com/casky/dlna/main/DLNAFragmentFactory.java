/**
 * 
 */
package com.casky.dlna.main;

import android.support.v4.app.Fragment;

import com.casky.dlna.music.MainFragmentMusic;
import com.casky.dlna.picture.MainFragmentPicture;
import com.casky.dlna.picture.sub.AlbumListFragment;
import com.casky.dlna.picture.sub.GalleryFragment;
import com.casky.dlna.picture.sub.SubFragmentPicture;
import com.casky.dlna.video.MainFragmentVideo;

/** 
 * ��Ŀ��ƣ�Smart_DLNA
 * ����ƣ�PictureFragFactory  
 * �������� Fragment����
 * �����ˣ�wangbo
 * ����ʱ�䣺2014-10-14 ����11:38:21
 * �޸��ˣ�wangbo
 * �޸�ʱ�䣺2014-10-14 ����11:38:21
 * �汾�� 1.0    
 */
public class DLNAFragmentFactory {
	public static final int FragmentMainDLNA = 0;
	public static final int FragmentMainPicture = 1;
	public static final int FragmentMainVideo = 2;
	public static final int FragmentMainMusic = 3;
	public static final int FragmentAlbumList = 4;
	public static final int FragmentSubPicture = 5;
	public static final int FragmentGallery = 6;
	
	private static MainFragmentDLNA mainDLNAFrag = null;
	private static MainFragmentPicture mainPicFrag = null;
	private static MainFragmentVideo mainVideoFrag = null;
	private static MainFragmentMusic mainMusicFrag = null;
	private static AlbumListFragment albumFrag = null;
	private static SubFragmentPicture subPicFrag = null;
	private static GalleryFragment galleryFrag = null;

	
	public static Fragment getFragInstance(int fragId){
		switch(fragId){
			case FragmentMainDLNA:
				if(mainDLNAFrag == null){
					mainDLNAFrag = new MainFragmentDLNA();
				}
				return mainDLNAFrag;
			case FragmentMainPicture:
				if(mainPicFrag == null){
					mainPicFrag = new MainFragmentPicture();
				}
				return mainPicFrag;
			case FragmentMainVideo:
				if(mainVideoFrag == null){
					mainVideoFrag = new MainFragmentVideo();
				}
				return mainVideoFrag;
			case FragmentMainMusic:
				if(mainMusicFrag == null){
					mainMusicFrag = new MainFragmentMusic();
				}
				return mainMusicFrag;
			case FragmentAlbumList:
				if(albumFrag == null){
					albumFrag = new AlbumListFragment();
				}
				return albumFrag;
			case FragmentSubPicture:
				if(subPicFrag == null){
					subPicFrag = new SubFragmentPicture();
				}
				return subPicFrag;
			case FragmentGallery:
				if(galleryFrag == null){
					galleryFrag = new GalleryFragment();
				}
				return galleryFrag;
			default:
				return null;
		}
		
	}
	
	public static void setFragInstance(int fragId,Fragment frag){
		switch(fragId){
			case FragmentMainDLNA:
					mainDLNAFrag = (MainFragmentDLNA) frag;
					break;
			case FragmentMainPicture:
					mainPicFrag = (MainFragmentPicture) frag;
					break;
			case FragmentMainVideo:
					mainVideoFrag = (MainFragmentVideo) frag;
					break;
			case FragmentMainMusic:
					mainMusicFrag = (MainFragmentMusic) frag;
					break;
			case FragmentAlbumList:
					albumFrag = (AlbumListFragment) frag;
					break;
			case FragmentSubPicture:
					subPicFrag = (SubFragmentPicture) frag;
					break;
			case FragmentGallery:
					galleryFrag = (GalleryFragment) frag;
					break;
		}
	}

}
