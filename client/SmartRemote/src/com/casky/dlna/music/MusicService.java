package com.casky.dlna.music;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.casky.dlna.control.DlnaManager;

import org.fourthline.cling.model.ModelUtil;

import java.io.IOException;
import java.util.List;

public class MusicService extends Service implements OnCompletionListener,OnSeekBarChangeListener {
    private static String TAG = "MusicService";
    private Uri uri;
    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer mPlayer;
    private SeekBar mSeekBar;
    private int duration;
    private int seekPosition;
    private boolean isLocalPlaying;
    private Runnable updateRunnable;
    private Runnable textViewRunnable;
    private TextView progressText;
    private TextView durationText;
    private Button tbPlayingBtn = null;
    private List<MusicFile> musicPlayList;
    private DlnaManager mDlnaManager;
    private MusicFile curMusicFile = null;
    private Handler controlHandler = new Handler();

    @Override
    public void onCreate() {
        Log.d(TAG, "service onCreate");
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.ANSWER");
        registerReceiver(InComingSMSReceiver, filter);
        if (mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
        mPlayer = new MediaPlayer();
        initUiRunnable();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }



    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        Log.d(TAG, "service onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mPlayer.stop();
        if (mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
        unregisterReceiver(InComingSMSReceiver);
        Log.d(TAG, "service onUnbind");
        return false;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "service onRebind");
    }

    public void setPlayList(List<MusicFile> musicPlayList) {
        this.musicPlayList = musicPlayList;
    }

    public List<MusicFile> getPlayList() {
        return musicPlayList;
    }

    public int getPlayListSize() {
        return musicPlayList.size();
    }

    public void setPlaySource(int position) {

        uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                "" + musicPlayList.get(position).getMetaData().getId());
        mPlayer.reset();
        try {
            mPlayer.setDataSource(this, uri);
            //mPlayer.prepare();
            mPlayer.prepare();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setSeekBarAndPD(SeekBar seekBar,
                                TextView progressTextView,
                                TextView durationTextView,
                                Button tbPlayingBtn,
                                DlnaManager mDlnaManager) {

        this.mSeekBar = seekBar;
        this.progressText = progressTextView;
        this.durationText = durationTextView;
        this.tbPlayingBtn = tbPlayingBtn;
        this.mDlnaManager = mDlnaManager;
        if (mSeekBar == null || progressText == null || durationText == null) {
            return;
        }

        mSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        mPlayer.seekTo(seekBar.getProgress());
        Log.e(TAG, "seek to " +seekBar.getProgress() + "max "+seekBar.getMax());
        if(mDlnaManager.getDlnaServiceManager().isDLNAPlaying()){
            mDlnaManager.getDlnaServiceManager().seek(ModelUtil.toTimeString(seekBar.getProgress()));
        }
    }

    public void start() {
        mPlayer.start();
        mSeekBar.setMax(mPlayer.getDuration());
        Log.e(TAG, "seekbar set = " + mPlayer.getDuration());
        controlHandler.post(textViewRunnable);
        controlHandler.post(updateRunnable);
        isLocalPlaying = true;
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    public void pause() {
        mPlayer.pause();
        controlHandler.removeCallbacks(updateRunnable);
        isLocalPlaying = false;
    }

    public void stop() {
        mPlayer.stop();
        tbPlayingBtn.setVisibility(View.INVISIBLE);
        controlHandler.removeCallbacks(updateRunnable);
        mSeekBar.setProgress(0);
        isLocalPlaying = false;
    }

    public void release(){
        if(mPlayer != null){
            if(mPlayer.isPlaying()){
                mPlayer.stop();
            }
            mPlayer.release();
        }
    }


    @Override
    public void onCompletion(MediaPlayer arg0) {
        stop();
    }

    public boolean getLocalPlayState() {
        return isLocalPlaying;
    }

    public String getPlayProcess() {
        return ModelUtil.toTimeString(mPlayer.getCurrentPosition() / 1000);
    }

    public MediaPlayer getMediaPlayer() {
        return mPlayer;
    }

    private void initUiRunnable() {
        updateRunnable = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mSeekBar.setProgress(mPlayer.getCurrentPosition());
                progressText.setText(ModelUtil.toTimeString(mPlayer.getCurrentPosition() / 1000));
                controlHandler.postDelayed(updateRunnable, 1000);
            }
        };
        textViewRunnable = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Log.d(TAG, " textViewRunnable getDuration " + mPlayer.getDuration());
                Log.d(TAG, " textViewRunnable getDuration 1 " + mPlayer.getDuration() / 1000);
                durationText.setText(ModelUtil.toTimeString(mPlayer.getDuration() / 1000));
            }
        };
    }

    protected BroadcastReceiver InComingSMSReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("android.intent.action.ANSWER");
            if (intent.getAction().equals(Intent.ACTION_ANSWER)) {
                TelephonyManager telephonymanager =
                        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                switch (telephonymanager.getCallState()) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        break;
                    default:
                        break;
                }
            }
        }
    };
}
