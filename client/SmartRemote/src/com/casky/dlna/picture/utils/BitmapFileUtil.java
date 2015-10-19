package com.casky.dlna.picture.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/** 
* ��Ŀ��ƣ�Smart_DLNA
* ����ƣ�BitmapFileUtil  
* �������� ��ȡbitmap�Ĺ�����
* �����ˣ�wangbo
* ����ʱ�䣺2014-9-12 ����11:25:12
* �޸��ˣ�wangbo
* �޸�ʱ�䣺2014-9-12 ����11:25:12
* �޸ı�ע��   
* �汾�� 1.0    
*
 */
public class BitmapFileUtil {
	
	public static final int SAMPLESIZE_THUMBNAIL = 100;
	public static final int SAMPLESIZE_PREVIEW = 100;
	 
   /**
   * ���������� ��ȡѹ�����bitmap
   * �����ˣ�wangbo
    */
   public static Bitmap getSmallBitmap(String filePath,int sampleSize) {
   	
		final BitmapFactory.Options options = new BitmapFactory.Options();
		//options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, sampleSize, sampleSize);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		
		Bitmap bm = BitmapFactory.decodeFile(filePath, options);
		if(bm == null){
			return  null;
		}
		//bm = ThumbnailUtils.extractThumbnail(bm, sampleSize, sampleSize, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		
		ByteArrayOutputStream baos = null ;
		try{
			baos = new ByteArrayOutputStream();
			int quality = 30;
			bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			//ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��
	        while ( baos.toByteArray().length / 1024>5) {        
	        	//����baos�����baos
	            baos.reset();
	          //ÿ�ζ�����10
	            quality  -= 5;
	            bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);//����ѹ��options%����ѹ�������ݴ�ŵ�baos��
	        }
			
		}finally{
			try {
				if(baos != null)
					baos.close() ;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bm ;

	}
   
   /**
    * ���������� ��ȡδѹ����bitmap
    * �����ˣ�wangbo
     */
    public static Bitmap getFullBitmap(String filePath) {
    	
 		final BitmapFactory.Options options = new BitmapFactory.Options();
 		
 		Bitmap bm = BitmapFactory.decodeFile(filePath, options);
 		if(bm == null){
 			return  null;
 		}
 		
 		return bm ;

 	}
   
   /**
   * ��������������bitmapѹ���ߴ� 
   * �����ˣ�wangbo
    */
   private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
		}

		return inSampleSize;
	}
}
