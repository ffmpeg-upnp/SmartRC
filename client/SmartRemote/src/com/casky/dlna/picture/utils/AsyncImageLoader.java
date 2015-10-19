package com.casky.dlna.picture.utils;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video.Thumbnails;

import com.casky.dlna.utils.BitmapLruCache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** 
* ��Ŀ��ƣ�Smart_DLNA
* ����ƣ�AsyncImageLoader  
* �������� �첽����ͼƬ
* �����ˣ�wangbo
* ����ʱ�䣺2014-9-15 ����4:32:06
* �޸��ˣ�wangbo
* �޸�ʱ�䣺2014-9-15 ����4:32:06
* �޸ı�ע��   
* �汾�� 1.0    
*
 */
public class AsyncImageLoader {
	
	private static ExecutorService executorService = null;
	private static final int THREAD_POOL_SIZE = 3;
	private static final int COMPRESSED_BITMAP_LOADED = 0x00;
	private static final int UNCOMPRESSED_BITMAP_LOADED = 0x01;
	private static final int BITMAP_LOADING = 0x02;

	private Bitmap bmTmp = null;
	
	
	private static ExecutorService getExecutorService(){
		if(executorService == null || executorService.isShutdown() || executorService.isTerminated()){
			executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		}
		return executorService;
	}
	
	public static void shutDownExecutorService(){
		executorService.shutdownNow();
	}
	
	/**
	* @param bitmapPath
	* @param callback
	* ���������� ��ȡbitmapCache������ڷ���bitmap�����򷵻�null������callback��ͼ
	 */
	public void LoadBitmap(final String bitmapPath,final boolean compress,final BitmapCallback callback){

		final Handler handler = new Handler(){
			boolean compressedBitmapLoaded = false;
			boolean unCompressedBitmapLoaded = false;
			
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case COMPRESSED_BITMAP_LOADED:
					compressedBitmapLoaded = true;
					if(compress || (!compress && !unCompressedBitmapLoaded)) {
						callback.BitmapLoaded((Bitmap)msg.obj);
					}
					break;
				case UNCOMPRESSED_BITMAP_LOADED:
					unCompressedBitmapLoaded = true;
					callback.BitmapLoaded((Bitmap)msg.obj);
					break;
				case BITMAP_LOADING:
					if(!compressedBitmapLoaded) {
						callback.BitmapLoaded(null);
					}
					break;
				}
				
			}
		};
		
		if(compress){
			if((bmTmp = BitmapLruCache.getBitmapCache(bitmapPath)) != null){
				Message msg = handler.obtainMessage(COMPRESSED_BITMAP_LOADED, bmTmp);
				handler.sendMessage(msg);
				return;
			}else{
				Message msg = handler.obtainMessage(BITMAP_LOADING, null);
				handler.sendMessage(msg);
			}
		}
		
		/**
		 * �½��̴߳���ݿ�ȡbitmap
		 */
		getExecutorService().submit(new Thread(){
			@Override
			public void run() {
				Bitmap bm;
				if(compress){
					bm = BitmapFileUtil.getSmallBitmap(bitmapPath,BitmapFileUtil.SAMPLESIZE_THUMBNAIL);
					BitmapLruCache.saveBitmapCache(bm,bitmapPath);
				}else{
					bm = BitmapFileUtil.getSmallBitmap(bitmapPath,BitmapFileUtil.SAMPLESIZE_PREVIEW);
				}
				
				Message msg = handler.obtainMessage(COMPRESSED_BITMAP_LOADED, bm);
				handler.sendMessage(msg);
			}
		});
		
		if(!compress){

			getExecutorService().submit(new Thread(){
				@Override
				public void run() {
					Bitmap bm;
					bm = BitmapFileUtil.getFullBitmap(bitmapPath);
					Message msg = handler.obtainMessage(UNCOMPRESSED_BITMAP_LOADED, bm);
					handler.sendMessage(msg);
				}
			});
		}
	}

	public Bitmap getVideoThumbnail(final String videoPath,final int width ,final int height, final BitmapCallback callback){
		
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				
				switch(msg.what){
				case COMPRESSED_BITMAP_LOADED:
					callback.BitmapLoaded((Bitmap)msg.obj);
					break;
				}
				
			}
		};
		
		if((bmTmp = BitmapLruCache.getBitmapCache(videoPath)) != null){
			Message msg = handler.obtainMessage(COMPRESSED_BITMAP_LOADED, bmTmp);
			handler.sendMessage(msg);
			return bmTmp;
		}
		
		
		getExecutorService().submit(new Thread(){
			@Override
			public void run() {
				Bitmap bitmap = null;  
			    bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, Thumbnails.MICRO_KIND);  
			    bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
			    Message msg = handler.obtainMessage(COMPRESSED_BITMAP_LOADED, bitmap);
				handler.sendMessage(msg);
				BitmapLruCache.saveBitmapCache(bitmap,videoPath);
			}
			
		});
		
	    return null;
	}

    /**
     * preload bitmap into LruCache
     * @param bitmapPath
     */
    public static void preloadBItmap(final String bitmapPath){
        getExecutorService().submit(new Thread(){
            @Override
            public void run() {
                Bitmap bm;
                bm = BitmapFileUtil.getSmallBitmap(bitmapPath,BitmapFileUtil.SAMPLESIZE_THUMBNAIL);
                BitmapLruCache.saveBitmapCache(bm,bitmapPath);
            }
        });
    }
	
	/** 
	* ��Ŀ��ƣ�Smart_DLNA
	* ����ƣ�BitmapCallback  
	* �������� ���غ�ΪImageView��ͼ�Ľӿ�
	* �����ˣ�wangbo
	* ����ʱ�䣺2014-9-15 ����4:33:16
	* �޸��ˣ�wangbo
	* �޸�ʱ�䣺2014-9-15 ����4:33:16
	* �汾�� 1.0    
	*
	 */
	public interface BitmapCallback{
		public void BitmapLoaded(Bitmap bitmap);
	}

}
