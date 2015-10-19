/**
 * 
 */
package com.casky.dlna.picture.utils;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/** 
 * ��Ŀ��ƣ�Smart_DLNA
 * ����ƣ�PictureCache  
 * �������� ��������ͼƬView����
 * �����ˣ�wangbo
 * ����ʱ�䣺2014-10-13 ����10:15:06
 * �޸��ˣ�wangbo
 * �޸�ʱ�䣺2014-10-13 ����10:15:06
 * �汾�� 1.0    
 */
public class PictureBitmapSoftReferenceCache {

	public static final int BITMAP_CACHE_LIMIT = 150;
	public static final int BITMAP_CACHE_RECYCLE_SIZE = 50;
		
	private static int bmCacheSize = 0;
	private static int bmCacheCurPointer = 0;
	private static int bmChacheRclPointer = 0;
	private static String[] cacheKeyArr = new String[BITMAP_CACHE_LIMIT];
	
	/**
	 * ����bitmap��������
	 */
	public static HashMap<String,SoftReference<Bitmap>> bmCacheMap = 
			new HashMap<String,SoftReference<Bitmap>>();
	
	
	/**
	* ������������View���뻺�� 
	* �����ˣ�wangbo
	 */
	public static void saveBitmapCache(Bitmap bm,String bitmapPath){

		if(bmCacheSize++ >= BITMAP_CACHE_LIMIT){
			for(int i=0;i<BITMAP_CACHE_RECYCLE_SIZE;i++){
				bmCacheMap.remove(cacheKeyArr[bmChacheRclPointer++]);
				bmCacheSize--;
			}
		}
		
		if(bmChacheRclPointer >= BITMAP_CACHE_LIMIT){
			bmChacheRclPointer = 0;
		}
		
		if(++bmCacheCurPointer >= BITMAP_CACHE_LIMIT){
			bmCacheCurPointer = 0;
		}
		
		bmCacheMap.put(bitmapPath, new SoftReference<Bitmap>(bm));
		cacheKeyArr[bmCacheCurPointer] = bitmapPath;

	}
	
	/**
	* �������������ر����View 
	* �����ˣ�wangbo
	 */
	public static Bitmap getBitmapCache(String bitmapPath){
		if(bmCacheMap.containsKey(bitmapPath)){
			SoftReference<Bitmap> softReference = bmCacheMap.get(bitmapPath);
			return softReference.get();
		}else{
			return null;
		}
	}
	
	/**
	* ��������������ָ��bm 
	* @param bitmapPath
	 */
	public void recycleBitmap(String bitmapPath){
		if(bmCacheMap.containsKey(bitmapPath)){
			SoftReference<Bitmap> softReference = bmCacheMap.get(bitmapPath);
			if(softReference.get() != null  && !softReference.get().isRecycled()){
				softReference.get().recycle();
			}
			bmCacheMap.remove(bitmapPath);
		}
	}
	
	/**
	* ������������������bm 
	* @param bitmapPath
	 */
	public static void recycleAllBitmap(){
		Iterator<Entry<String, SoftReference<Bitmap>>> i = bmCacheMap.entrySet().iterator();
		Entry<String, SoftReference<Bitmap>> e = null;
		SoftReference<Bitmap> softReference = null;
		Bitmap b = null;
		
		while(i.hasNext()){
			e = (Entry<String, SoftReference<Bitmap>>) i.next();
			softReference = (SoftReference<Bitmap>)e.getValue();
			b = softReference.get();
			if(b != null && !b.isRecycled()){
				b.recycle();
				b = null;
			}
			bmCacheMap.remove(e.getKey());
		}
	}
	
}
