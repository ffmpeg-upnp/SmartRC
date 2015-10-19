package com.casky.dlna.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.casky.dlna.utils.UriBuilder.MediaType;
import com.casky.smartremote.R;

import org.apache.http.conn.util.InetAddressUtils;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.ImageItem;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.fourthline.cling.support.model.item.VideoItem;
import org.seamless.util.MimeType;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * 项目名称：Smart_DLNA
 * 类名称：Utils
 * 类描述：
 * 创建人：shaojiansong
 * 创建时间＄1�714-9-23 下午1:51:43
 * 修改人：shaojiansong
 * 修改时间＄1�714-9-23 下午1:51:43
 * 修改备注＄1�7
 * 版本＄1�7 1.0
 */
public class Utils {

    private static final String TAG = "Utils";
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    private static Bitmap mCachedBit = null;
    private static HashMap<Integer, Integer> mHashMap;
    private static Context mContext = null;

    public static void initContext(Context context) {
        Utils.mContext = context;
    }

    public static String bSubstring(String s, int length) throws Exception {

        byte[] bytes = s.getBytes("Unicode");
        int n = 0;
        int i = 2;
        for (; i < bytes.length && n < length; i++) {
            if (i % 2 == 1) {
                n++;
            } else {
                if (bytes[i] != 0) {
                    n++;
                }
            }
        }
        if (i % 2 == 1)

        {
            if (bytes[i - 1] != 0)
                i = i - 1;
            else
                i = i + 1;
        }

        return new String(bytes, 0, i, "Unicode");
    }

    public static Message createMessage(int what, Object object) {
        Message message = new Message();
        message.what = what;
        message.obj = object;
        return message;

    }

    public static Bitmap getArtwork(Context context, long song_id, long album_id,
                                    boolean allowdefault) {
        if (album_id < 0) {
            // This is something that is not in the database, so get the album art directly
            // from the file.
            if (song_id >= 0) {
                Bitmap bm = getArtworkFromFile(context, song_id, -1);
                if (bm != null) {
                    return bm;
                }
            }
            if (allowdefault) {
                return getDefaultArtwork(context);
            }
            return null;
        }
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, sBitmapOptions);
            } catch (FileNotFoundException ex) {
                // The album art thumbnail does not actually exist. Maybe the user deleted it, or
                // maybe it never existed to begin with.
                Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                if (bm != null) {
                    if (bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                        if (bm == null && allowdefault) {
                            return getDefaultArtwork(context);
                        }
                    }
                } else if (allowdefault) {
                    bm = getDefaultArtwork(context);
                }
                return bm;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                }
            }
        }

        return null;
    }

    public static Bitmap getArtworkFromFile(Context context, long songid, long albumid) {
        Bitmap bm = null;
        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try {
            if (albumid < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            }
        } catch (FileNotFoundException ex) {

        }
        if (bm != null) {
            mCachedBit = bm;
        }
        return bm;
    }

    private static Bitmap getDefaultArtwork(Context context) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
//        return BitmapFactory.decodeStream(
//                context.getResources().openRawResource(R.drawable.dlna_music_play_img_default), null, opts);
        BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(R.drawable.dlna_music_play_img_default);
        return bd.getBitmap();
    }

    public static void setBuildMap(HashMap<Integer, Integer> hashMap) {
        mHashMap = hashMap;
    }

    public static HashMap<Integer, Integer> getBuildMap() {
        return mHashMap;
    }

    public static String getLocalIpAddress() {
        try {
            String ipv4;
            ArrayList<NetworkInterface> mylist = Collections.list(NetworkInterface.getNetworkInterfaces());

            for (NetworkInterface ni : mylist) {
                ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
                for (InetAddress address : ialist) {
                    if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = address.getHostAddress())) {
                        return ipv4;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.d("Can not get local ip address!", ex.toString());
        }
        return null;
    }

    public static String unitFormat(int time) {
        String retStr = null;
        if (time < 10) {
            retStr = "0" + Integer.toString(time);
        } else {
            retStr = "" + time;
        }
        return retStr;
    }

    public static String timeToText(int mSec) {
        String timeStr = null;
        int allSecond = mSec / 1000;
        int tHour = allSecond / 3600;
        int tMinite = (allSecond - tHour * 3600) / 60;
        int tSecond = allSecond % 60;
        timeStr = unitFormat(tHour) + ":" + unitFormat(tMinite) + ":" + unitFormat(tSecond);
        return timeStr;
    }

    public static String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }

    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s = encode;
                return s;
            }
        } catch (Exception exception) {
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s1 = encode;
                return s1;
            }
        } catch (Exception exception1) {
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s2 = encode;
                return s2;
            }
        } catch (Exception exception2) {
        }
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s3 = encode;
                return s3;
            }
        } catch (Exception exception3) {
        }
        return "";
    }

    public static String createMetadataForMusic(String id, String title, String path, String creator, String album, long duration,
                                                String mimeType, long size) {
        String metaData = "";
        Res res = new Res(new MimeType(mimeType.substring(0, mimeType.indexOf('/')),
                mimeType.substring(mimeType.indexOf('/') + 1)), size, UriBuilder.buildUriForType(MediaType.music, id));
        String durationString = ModelUtil.toTimeString(duration / 1000);
        res.setDuration(durationString);

        DIDLContent didl = new DIDLContent();
        MusicTrack track = new MusicTrack(id, "0$" + id, title, creator, album, new PersonWithRole(creator, "Performer"), res);
        track.setRestricted(true);

        didl.addItem(track);

        try {
            metaData = new DIDLParser().generate(didl);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "send metadata is " + metaData);
        return metaData;
    }

    public static String createMetadataForVideo(String id, String name, String path,
                                                String artist, String resolution, String mimeType, long size,
                                                long duration) {
        String metaData = "";
        Res res = new Res(new MimeType(mimeType.substring(0,
                mimeType.indexOf('/')), mimeType.substring(mimeType
                .indexOf('/') + 1)), size,
                UriBuilder.buildUriForType(MediaType.video, id));
        res.setDuration(duration / (1000 * 60 * 60) + ":"
                + (duration % (1000 * 60 * 60)) / (1000 * 60) + ":"
                + (duration % (1000 * 60)) / 1000);
        res.setResolution(resolution);


        VideoItem videoItem = new VideoItem(id, "0$" + id, name,
                artist, res);

        DIDLContent didl = new DIDLContent();
        didl.addItem(videoItem);
        try {
            metaData = new DIDLParser().generate(didl);
            // System.out.println("didl jiyongfeng:"+metaData);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "send metadata is " + metaData);
        return metaData;
    }


    public static String createMetadataForPhoto(String id, String title, String path,
                                                String mimeType, long size) {
        String metaData = "";
        Res res = new Res(new MimeType(mimeType.substring(0,
                mimeType.indexOf('/')), mimeType.substring(mimeType
                .indexOf('/') + 1)), size,
                UriBuilder.buildUriForType(MediaType.picture, id));

        ImageItem imageItem = new ImageItem(id, "-1", title,
                "unkown", res);

        DIDLContent didl = new DIDLContent();
        didl.addItem(imageItem);
        try {
            metaData = new DIDLParser().generate(didl);
            // System.out.println("didl jiyongfeng:"+metaData);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // System.out.println("metadata:"+metaData);
        return metaData;
    }

    public static String createTitleText(int selectedCount) {
        String str = mContext.getResources().getString(R.string.dlna_title_bar_select_count) + "(" + selectedCount + ")";
        return str;
    }

    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
}
