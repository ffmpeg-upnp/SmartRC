package com.casky.dlna.music;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.casky.dlna.control.DlnaCommandManager;
import com.casky.dlna.control.SeekBarController;
import com.casky.smartremote.R;
import com.casky.dlna.control.DlnaCommandManager.LocalPlayCallback;
import com.casky.dlna.control.DlnaCommandManager.LocalStopCallback;
import com.casky.dlna.control.DlnaManager;
import com.casky.dlna.main.MainFragmentDLNA;
import com.casky.dlna.main.Manageable;
import com.casky.main.slidingmenu.RemoteApplication;
import com.casky.dlna.main.RenderSelectCallBridge;
import com.casky.dlna.main.RenderSelectCallBridge.renderSelectCallBack;
import com.casky.dlna.utils.UriBuilder;
import com.casky.dlna.utils.Utils;
import com.casky.dlna.view.RenderDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainFragmentMusic extends Fragment implements Manageable,
        OnTouchListener, PopupWindow.OnDismissListener,
        renderSelectCallBack {

    private static final String TAG = "MainFragmentMusic";
    public static final int flag = CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER;

    private  MusicSelectionManager selectionManager = null;
    private ListView mListView;
    private PopupWindow mPopupWindow;
    private View playerView;
    private int screenWidth;
    private int screenHeight;
    private MusicService musicService;

    private boolean isPlaying = false;
    private RenderDialog mRenderDialog;
    private DlnaManager mDlnaManager;
    private SeekBar seekBar;
    private TextView progressText;
    private TextView durationText;
    private List<Integer> shuffleList = null;
    private int mCurrentPlayIndex = -1;
    private int mLastPlayIndex = -1;
    private static boolean shufflePlay = false;
    private MusicFileListAdapter mMusicFileListAdapter;

    private RelativeLayout musicFragTitleBar = null;
    private TextView musicFragTBCountTv = null;
    private Button musicFragTbCancelBtn = null;
    private Button musicFragTbSlcAllBtn = null;
    private Button musicFragTbPlayingBtn = null;
    private Button musicPlayBackBtn = null;
    private TextView musicPlayTvTitle = null;
    private ListView musicPlayListView = null;
    private MusicPlayListAdapter playListAdapter = null;
    private Button playButton = null;
    private Button pushButton = null;
    private Random random = null;
    
    public static final int DLNA_PLAY = 0x01;
	public static final int DLNA_STOP = 0x02;
    public static final int DLNA_FINISH = 0x03;
	
	public Handler musicUIHandler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case DLNA_PLAY:
//				musicService.start();
	            isPlaying = true;
	            playButton = (Button) playerView.findViewById(R.id.music_play_btn_playsong);
	            playButton.setBackgroundResource(R.drawable.dlna_media_pause_normal);
//	            mDlnaManager.getDlnaServiceManager().seek(musicService.getPlayProcess());
	            pushButton.setBackgroundResource(R.drawable.dlna_media_stop_normal);
                musicService.pause();
				break;
			case DLNA_STOP:
                playButton = (Button) playerView.findViewById(R.id.music_play_btn_playsong);
                playButton.setBackgroundResource(R.drawable.dlna_media_play_normal);
				pushButton.setBackgroundResource(R.drawable.dlna_multi_menu_push);
                resumeLocalPlay();
				break;
            case DLNA_FINISH:
                playNextMusic();
                break;
			}
		}
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindowMetric();
        mPopupWindow = new PopupWindow(screenWidth, screenHeight);
        Application application = getActivity().getApplication();
        musicService = ((RemoteApplication) application).getMusicPlayerService();
        random = new Random();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dlna_fragment_main_music, container, false);
        mDlnaManager = DlnaManager.getDlnaManagerInstance(getActivity().getApplicationContext());

        musicFragTitleBar = (RelativeLayout) rootView.findViewById(R.id.music_frag_rl_titleBar);
        musicFragTBCountTv = (TextView) rootView.findViewById(R.id.music_frag_tv_TitleText);
        musicFragTbCancelBtn = (Button) rootView.findViewById(R.id.music_frag_btn_cancel_mltslc);
        musicFragTbSlcAllBtn = (Button) rootView.findViewById(R.id.music_frag_btn_SelectAll);
        musicFragTbPlayingBtn = (Button) getActivity().findViewById(R.id.dlna_main_btn_playing);

        mListView = (ListView) rootView.findViewById(R.id.music_frag_lv_files_listview);
        mMusicFileListAdapter = new MusicFileListAdapter(this, null,flag, musicFragTitleBar,mListView);
        MusicLoaderCallbacks loaderCallbacks = new MusicLoaderCallbacks(getActivity(),mMusicFileListAdapter);
        getLoaderManager().initLoader(
                0,
                null,
                loaderCallbacks);
        mListView.setAdapter(mMusicFileListAdapter);
        mListView.setOnItemClickListener(mMusicFileListAdapter);
        mListView.setOnScrollListener(mMusicFileListAdapter);

        playerView = inflater.inflate(R.layout.dlna_music_play_popwindow, null);
        musicPlayBackBtn = (Button) playerView.findViewById(R.id.music_play_btn_back);
        musicPlayTvTitle = (TextView) playerView.findViewById(R.id.music_play_tv_count);
        musicPlayListView = (ListView) playerView.findViewById(R.id.music_play_lv_playList);

        initPopWindow();

        selectionManager = (MusicSelectionManager) mMusicFileListAdapter.getMusicFileSelectionManager();
        initViews(playerView);

        musicFragTbCancelBtn.setOnTouchListener(this);
        musicFragTbSlcAllBtn.setOnTouchListener(this);
        musicFragTbPlayingBtn.setOnTouchListener(this);
        musicPlayBackBtn.setOnTouchListener(this);

        shuffleList = new ArrayList<Integer>();

        return rootView;
    }

    public void initPopWindow(){
        mPopupWindow.setContentView(playerView);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOnDismissListener(this);
    }
    private void initViews(View view) {

        pushButton = (Button) view.findViewById(R.id.music_play_btn_pushsong);
        pushButton.setOnTouchListener(this);

        playButton = (Button) view.findViewById(R.id.music_play_btn_playsong);
        playButton.setOnTouchListener(this);

        Button lastButton = (Button) view.findViewById(R.id.music_play_btn_lastsong);
        lastButton.setOnTouchListener(this);

        Button nextButton = (Button) view.findViewById(R.id.music_play_btn_nextsong);
        nextButton.setOnTouchListener(this);

        Button playListButton = (Button) view.findViewById(R.id.music_play_btn_play_list);
        playListButton.setOnTouchListener(this);

        Button shuffleButton = (Button) view.findViewById(R.id.music_play_btn_shuffle);
        shuffleButton.setOnTouchListener(this);

        progressText = (TextView) view.findViewById(R.id.music_play_tv_process);
        durationText = (TextView) view.findViewById(R.id.music_play_tv_duration);
    }


    @Override
    public MusicSelectionManager getMediaFileSelectionManager() {
        return (MusicSelectionManager) mMusicFileListAdapter.getMusicFileSelectionManager();
    }

    private void getWindowMetric() {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
    }

    public void startPlayer(List<MusicFile> musicPlayList, int position) {
        musicService.setPlayList(musicPlayList);
        Log.d(TAG,"musicPlayList"+musicPlayList.size()+",position"+position);
        Log.d(TAG,"musicService.getPlayList()"+musicService.getPlayList().size());
        playListAdapter = new MusicPlayListAdapter(this, musicService.getPlayList());
        musicPlayListView.setAdapter(playListAdapter);
        musicPlayListView.setOnItemClickListener(playListAdapter);

        RenderSelectCallBridge.getInstance().setRenderSelectCallBack(this);
        seekBar = (SeekBar) playerView.findViewById(R.id.music_play_sb_process);
        musicPlayListView.setSelection(position);
        mLastPlayIndex = -1;

        musicService.setSeekBarAndPD(seekBar, progressText, durationText,  musicFragTbPlayingBtn,mDlnaManager);
        mDlnaManager.getDlnaServiceManager().initDLNASeekBar(
                new SeekBarController.SeekBarModule(seekBar,durationText,progressText),
                        mDlnaManager);
        shuffleList.clear();
        musicService.getMediaPlayer().setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(!mDlnaManager.getDlnaServiceManager().isDLNAPlaying()) {
                    if(mp.isPlaying())
                    playNextMusic();
                }
            }
        });
        mPopupWindow.showAtLocation(mListView, Gravity.CENTER, 0, 0);
        mPopupWindow.update(0, 0,screenWidth, screenHeight);

        setPlayData(position);

    }

    public void setPlayData(int position) {
        playListAdapter.setCurSelectPosition(position);
        musicPlayListView.setSelection(position);
        musicPlayListView.smoothScrollToPosition(position);

        playListAdapter.notifyDataSetChanged();
        mLastPlayIndex = mCurrentPlayIndex;
        mCurrentPlayIndex = position;

        musicPlayTvTitle.setText(musicService.getPlayList().get(position).getMetaData().getTitle());
        ImageView imageView = (ImageView) playerView.findViewById(R.id.music_play_iv_album_img);
        Bitmap bitmap = mMusicFileListAdapter.getBitmapFromMemoryCache(String.valueOf(position));
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
        musicService.setPlaySource(position);
    }

    public void pauseLocalPlay() {
        isPlaying = false;
        musicService.pause();
        playButton.setBackgroundResource(R.drawable.dlna_media_play_normal);
    }

    public void pauseDlnalPlay() {
        isPlaying = false;
        if (mDlnaManager.getDlnaServiceManager().isDLNAPlaying()) {
            mDlnaManager.getDlnaServiceManager().pause();
        }
        playButton.setBackgroundResource(R.drawable.dlna_media_play_normal);
    }

    public void resumeLocalPlay() {
        isPlaying = true;
        musicService.start();
        playButton.setBackgroundResource(R.drawable.dlna_media_pause_normal);
    }

    public void resumeDlnaPlay() {
        isPlaying = true;
        if (mDlnaManager.getDlnaServiceManager().isDLNAPlaying()) {
            mDlnaManager.getDlnaServiceManager().resume();
        }
        playButton.setBackgroundResource(R.drawable.dlna_media_pause_normal);
    }

    public void stopLocalPlay() {
        musicService.stop();
        playButton.setBackgroundResource(R.drawable.dlna_media_play_normal);
    }

    public void stopDlnaPlay() {
        if (mDlnaManager.getDlnaServiceManager().isDLNAPlaying()) {
            mDlnaManager.getDlnaServiceManager().stop(new LocalStopCallbackImpl());
        }
    }

    public void showDialogOrCancelPush() {
        if (mDlnaManager.getDlnaServiceManager().isDLNAPlaying()) {
            cancelPush();
        } else {
            showDialog();
        }
    }

    private void showDialog() {
        mRenderDialog = RenderDialog.getInstance(getActivity());
        mRenderDialog.showRenderDialog();
    }

    public void cancelPush() {
        mDlnaManager.getDlnaServiceManager().stop(new LocalStopCallbackImpl());
    }

    public int getmCurrentPlayIndex() {
        return mCurrentPlayIndex;
    }

    public void startPlayMusic(){
        setPlayData(mCurrentPlayIndex);
        if (mDlnaManager.getDlnaServiceManager().isDLNAPlaying()) {
            startPushData();
        } else {
            resumeLocalPlay();
        }
        isPlaying = true;
    }

    public void playPreviousMusic() {
        if (!shufflePlay) {
            if ((mCurrentPlayIndex - 1) > -1) {
                mCurrentPlayIndex = mCurrentPlayIndex - 1;
            } else {
                mCurrentPlayIndex = musicService.getPlayListSize() - 1;
            }
        } else {
            mCurrentPlayIndex = getShuffleIndex();
        }
        startPlayMusic();
    }

    public void playNextMusic() {
        if (!shufflePlay) {
            if ((mCurrentPlayIndex + 1) < musicService.getPlayListSize()) {
                mCurrentPlayIndex = mCurrentPlayIndex + 1;
            } else {
                mCurrentPlayIndex = 0;
            }
        } else {
            mCurrentPlayIndex = getShuffleIndex();
        }
        startPlayMusic();
    }

    public int getShuffleIndex() {
        int i = random.nextInt(musicService.getPlayListSize() - 1);
        if (shuffleList.size() == musicService.getPlayListSize()) {
            shuffleList.clear();
        }
        while (shuffleList.contains(i)) {
            i = random.nextInt(musicService.getPlayListSize() - 1);
        }
        return i;
    }

    public void setmCurrentPlayIndex(int mCurrentPlayIndex) {
        this.mCurrentPlayIndex = mCurrentPlayIndex;
    }

    public void startPushData() {
        MusicFile music = musicService.getPlayList().get(mCurrentPlayIndex);
        MusicMetaData metadata = (MusicMetaData) music.getMetaData();
        String id = metadata.getId();
        String metadataString = metadata.getMetadataString();
        mDlnaManager.getDlnaServiceManager().setPushMediaData(UriBuilder.MediaType.music, id, metadataString);
        mDlnaManager.getDlnaServiceManager().play(new LocalPlayCallbackImpl());
        mDlnaManager.getDlnaServiceManager().setFinishCallback(new LocalFinishCallbackImple());
    }

    @Override
    public void doSelect() {
//        if (musicService.getMediaPlayer().isPlaying()) {
//            musicService.pause();
//        }
//        pauseLocalPlay();
        startPushData();
    }


    @Override
    public void showTitleBar() {
        musicFragTBCountTv.setVisibility(View.VISIBLE);
        musicFragTBCountTv.setText(Utils.createTitleText(getMediaFileSelectionManager().getSelectFiles().size()));
        musicFragTitleBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void hideTitleBar() {
        musicFragTBCountTv.setVisibility(View.INVISIBLE);
        musicFragTitleBar.setVisibility(View.GONE);
    }

    @Override
    public void refreshTitleCount(){
        musicFragTBCountTv.setText(Utils.createTitleText(selectionManager.getSelectFiles().size()));
    }

    @Override
    public void onDismiss() {
        if(musicService.getMediaPlayer().isPlaying()) {
            musicFragTbPlayingBtn.setVisibility(View.VISIBLE);
        }else{
            musicFragTbPlayingBtn.setVisibility(View.INVISIBLE);
            stopLocalPlay();
            stopDlnaPlay();
            mCurrentPlayIndex = -1;
            mLastPlayIndex = -1;
        }
    }


    /**
     * 项目名称：Smart_DLNA
     * 类名称：LocalPlayCallbackImpl
     * 类描述： DLNA播放成功后调甄1�7
     * 创建人：wangbo
     * 创建时间＄1�714-11-5 下午3:55:36
     * 修改人：wangbo
     * 修改时间＄1�714-11-5 下午3:55:36
     * 版本＄1�7 1.0
     */
    public class LocalPlayCallbackImpl implements LocalPlayCallback {
        @Override
        public void playCallback() {
            musicUIHandler.sendEmptyMessage(DLNA_PLAY);
        }
    }

    public class LocalStopCallbackImpl implements LocalStopCallback {
        @Override
        public void stopCallback() {
            musicUIHandler.sendEmptyMessage(DLNA_STOP);
        }
    }

    public class LocalFinishCallbackImple implements DlnaCommandManager.LocalFinishCallback{
        @Override
        public void finishCallback() {
            musicUIHandler.sendEmptyMessage(DLNA_FINISH);
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }
        switch (v.getId()) {
            case R.id.music_frag_btn_SelectAll:
                if (!selectionManager.isAllSelected()) {
                    selectionManager.selectAll();
                    musicFragTbSlcAllBtn.setText(getActivity().getString(R.string.dlna_title_bar_unselect_all));
                } else {
                    selectionManager.unSelectAll();
                    musicFragTbSlcAllBtn.setText(getActivity().getString(R.string.dlna_title_bar_select_all));
                }
                musicFragTBCountTv.setText(Utils.createTitleText(selectionManager.getSelectFiles().size()));
                break;
            case R.id.music_play_btn_back:
                mPopupWindow.dismiss();
                break;
            case R.id.music_frag_btn_cancel_mltslc:
                selectionManager.cancelMultipleSelect();
                MainFragmentDLNA.changeSelectionUI(false);
                break;
            case R.id.dlna_main_btn_playing:
                //initPopWindow();
                //setPlayData(mCurrentPlayIndex);
                mPopupWindow.showAtLocation(mListView, Gravity.CENTER, 0, 0);
                break;
            case R.id.music_play_btn_pushsong:
                showDialogOrCancelPush();
                break;

            case R.id.music_play_btn_playsong:
                if (!isPlaying) {
                    if(!mDlnaManager.getDlnaServiceManager().isDLNAPlaying()) {
                        resumeLocalPlay();
                    }else {
                        resumeDlnaPlay();
                    }
                } else {
                    if(!mDlnaManager.getDlnaServiceManager().isDLNAPlaying()) {
                        pauseLocalPlay();
                    }else {
                        pauseDlnalPlay();
                    }
                }
                break;

            case R.id.music_play_btn_lastsong:
                playPreviousMusic();
                break;

            case R.id.music_play_btn_nextsong:
                playNextMusic();
                break;

            case R.id.music_play_btn_play_list:
                if (musicPlayListView.getVisibility() == View.VISIBLE) {
                    Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.dlna_hide_play_list);
                    musicPlayListView.startAnimation(anim);
                    musicPlayListView.setVisibility(View.GONE);
                    v.setBackgroundResource(R.drawable.dlna_media_playlist_normal);
                } else {
                    musicPlayListView.setVisibility(View.VISIBLE);
                    v.setBackgroundResource(R.drawable.dlna_media_playlist_hl);
                    Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.dlna_show_play_list);
                    musicPlayListView.startAnimation(anim);
                }
                break;

            case R.id.music_play_btn_shuffle:
                if (shufflePlay) {
                    shufflePlay = false;
                    v.setBackgroundResource(R.drawable.dlna_media_shuffle_normal);
                } else {
                    shufflePlay = true;
                    v.setBackgroundResource(R.drawable.dlna_media_shuffle_hl);
                }
                break;
        }
        return false;
    }

}
