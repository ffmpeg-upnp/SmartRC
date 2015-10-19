package com.casky.dlna.picture;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import com.casky.dlna.picture.utils.AsyncImageLoader;
import com.casky.dlna.picture.utils.PictureLoaderCallbacks;

/**
 * Created by wangbo on 14-11-20.
 */
public class PicturePreloader {

    public static final int preloadCount = 24;

    public static int BitmapPathIndex = 0;

    public static void preloadPicture(Context context){
        Cursor mCursor = context.getContentResolver().
                query(PictureLoaderCallbacks.imgUri,
                PictureLoaderCallbacks.STORE_IMAGES,
                PictureLoaderCallbacks.cameraSelection,
                PictureLoaderCallbacks.cameraSelectionArgs,
                PictureLoaderCallbacks.sortOrder
                );

        mCursor.moveToFirst();

        if(mCursor.getCount() == 0){
            return;
        }

        BitmapPathIndex = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        String bitmapPath = null;
        while (!mCursor.isLast() && mCursor.getPosition() < preloadCount){
            bitmapPath = mCursor.getString(BitmapPathIndex);
            AsyncImageLoader.preloadBItmap(bitmapPath);
            mCursor.moveToNext();
        }
    }
}