/**
 * 
 */
package com.casky.dlna.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

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
public class BitmapLruCache {
		
	public static LruCache<String, Bitmap> bmLruCache = null;
    public static final String TAG = "BitmapLruCache";
	
	/**
	 * ����bitmap��������
	 */
	static{
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);  
	    // ʹ���������ڴ�ֵ��1/8��Ϊ����Ĵ�С��  
	    int cacheSize = maxMemory / 8;
	    System.out.println("cacheSize = " + cacheSize);
	    bmLruCache = new LruCache<String, Bitmap>(cacheSize) {  
	        @Override 
	        protected int sizeOf(String key, Bitmap bitmap) {  
	            // ��д�˷���������ÿ��ͼƬ�Ĵ�С��Ĭ�Ϸ���ͼƬ������  
	            return bitmap.getRowBytes() * bitmap.getHeight() / 1024;  
	        }  
	    }; 
	}
    
	
	/**
	* ������������View���뻺�� 
	* �����ˣ�wangbo
	 */
	public static void saveBitmapCache(Bitmap bm,String bitmapPath){

		if(getBitmapCache(bitmapPath) == null){
			bmLruCache.put(bitmapPath, bm);
//            Log.d(TAG,"saveBitmapCache"+bm.toString());
        }

	}
	
	/**
	* �������������ر����View 
	* �����ˣ�wangbo
	 */
	public static Bitmap getBitmapCache(String bitmapPath){
		return bmLruCache.get(bitmapPath);
	}
	
	/**
	* ��������������ָ��bm 
	* @param bitmapPath
	 */
	public void recycleBitmap(String bitmapPath){
		if(bmLruCache.get(bitmapPath) != null){
			bmLruCache.remove(bitmapPath);
		}
	}
	
	/**
	* ������������������bm
	 */
	public static void recycleAllBitmap(){
		bmLruCache.evictAll();
	}
	
}
