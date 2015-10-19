package com.casky.dlna.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.casky.dlna.main.MediaFile;
import com.casky.smartremote.R;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangbo on 14-11-24.
 */
public class FileUtil {
    public static boolean deleteEmptyDirectory(String path){
       File file = new File(path);
        if(file.exists() && file.isDirectory() && file.listFiles().length ==0){
            return  file.delete();
        }else{
            return  false;
        }
    }

    public static void shareSingleMediaFile(Activity mActivity,MediaFile media){
        Intent intent=new Intent(Intent.ACTION_SEND);
        String path = media.getMetaData().getPath();
        File file =  new File(path);
        Uri uri = null;

        if(file.exists()){
            uri = Uri.fromFile(file);
        }
        intent.putExtra(Intent.EXTRA_TEXT,media.getMetaData().getMimeType());
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(Intent.createChooser(intent,mActivity.getResources().getString(R.string.dlna_share_title)));
    }

    public static void shareMultipleMediaFile(Activity mActivity,List<MediaFile> fileList){
        Intent intent=new Intent(Intent.ACTION_SEND_MULTIPLE);
        Uri uri = null;
        String path  = null;
        File file = null;
        ArrayList<Uri> uriList = new ArrayList<Uri>();

        for(int i=0;i<fileList.size();i++){
            path = fileList.get(i).getMetaData().getPath();
            file = new File(path);
            if(file.exists()){
                uri = Uri.fromFile(file);
                uriList.add(uri);
            }
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,uriList);
        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(Intent.createChooser(intent,mActivity.getResources().getString(R.string.dlna_share_title)));
    }


}
