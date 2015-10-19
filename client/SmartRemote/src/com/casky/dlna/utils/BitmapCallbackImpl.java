/**
 * 
 */
package com.casky.dlna.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

import com.casky.dlna.picture.utils.AsyncImageLoader.BitmapCallback;

/**
 * ��Ŀ��ƣ�Smart_DLNA
 * ����ƣ�BitmapCallbackImpl  
 * �������� ʵ����BitmapCallba���࣬����ΪImageView����Bitmap
 * �����ˣ�wangbo
 * ����ʱ�䣺2014-9-15 ����4:46:47
 * �޸��ˣ�wangbo
 * �޸�ʱ�䣺2014-9-15 ����4:46:47
 * �޸ı�ע��   
 * �汾�� 1.0    
 *
 */
public class BitmapCallbackImpl implements BitmapCallback {

	private ImageView iv = null;
	private boolean transparent = false;
	private int position = 0;
	
	/**
	 * BitmapCallbackImpl���췽��
	 */
	public BitmapCallbackImpl(ImageView iv,int position) {
		this.iv = iv;
		this.position = position;
	}
	
	/**
	 * BitmapCallbackImpl���췽��
	 */
	public BitmapCallbackImpl(ImageView iv,boolean transparent,int position) {
		this.iv = iv;
		this.transparent = transparent;
		this.position = position;
	}


	@Override
	public void BitmapLoaded(Bitmap bitmap) {
		
		
		int holderPos = (Integer) iv.getTag();
		if(holderPos != this.position){
			return;
		}
		iv.setImageBitmap(bitmap);
		if(transparent && bitmap != null){
			iv.getDrawable().setAlpha(100);
			iv.setBackgroundColor(Color.BLACK);
		}
	}

}
