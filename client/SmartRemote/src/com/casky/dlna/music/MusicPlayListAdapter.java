/**
 *
 */
package com.casky.dlna.music;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.casky.smartremote.R;
import com.casky.dlna.utils.Utils;

import java.util.List;

/**
 * ��Ŀ��ƣ�Smart_DLNA
 * ����ƣ�MusicPlayListAdapter
 * ��������
 * �����ˣ�wangbo
 * ����ʱ�䣺2014-10-28 ����3:09:10
 * �޸��ˣ�wangbo
 * �޸�ʱ�䣺2014-10-28 ����3:09:10
 * �汾�� 1.0
 */
public class MusicPlayListAdapter extends BaseAdapter implements OnItemClickListener {

    public static final String TAG = "MusicPlayListAdapter";

    private List<MusicFile> musicPlayList = null;
    private MainFragmentMusic mFrag = null;
    private LayoutInflater inflater = null;
    private MusicSelectionManager mSelectionManager = null;
    private int curSelectPosition = -1;

    public MusicPlayListAdapter(MainFragmentMusic frag, List<MusicFile> musicPlayList) {
        this.mFrag = frag;
        this.musicPlayList = musicPlayList;
        this.inflater = LayoutInflater.from(frag.getActivity());
        mSelectionManager = frag.getMediaFileSelectionManager();
    }

    @Override
    public int getCount() {
        return musicPlayList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return musicPlayList.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(musicPlayList.get(position).getMetaData().getId());
    }

    public void setCurSelectPosition(int position){
        this.curSelectPosition = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dlna_music_play_list_item, null);
            mViewHolder = new ViewHolder();
            //convertView = LayoutInflater.from(mFrag.getActivity()).inflate(R.layout.dlna_music_play_list_item, null);
            mViewHolder.title = (TextView) convertView.findViewById(R.id.music_play_item_tv_title);
            mViewHolder.singer = (TextView) convertView.findViewById(R.id.music_play_item_tv_singer);
            mViewHolder.time = (TextView) convertView.findViewById(R.id.music_play_item_tv_time);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        MusicFile preloadMusic = musicPlayList.get(position);
        MusicMetaData metadata = preloadMusic.getMetaData();
        String title = metadata.getTitle();
        String singer = metadata.getArtist();
        String time = String.valueOf(metadata.getDuration());

        mViewHolder.title.setText(title);
        mViewHolder.singer.setText(singer);
        mViewHolder.time.setText(Utils.formatTime(Long.valueOf(time)));

        if (position == curSelectPosition) {
            convertView.setBackgroundColor(mFrag.getResources().getColor(R.color.transparent_green));
        } else {
            convertView.setBackgroundColor(mFrag.getResources().getColor(R.color.transparent_black));
        }
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        mFrag.setPlayData(position);
        mFrag.startPlayMusic();
    }


    final class ViewHolder {
        TextView title;
        TextView singer;
        TextView time;
    }
}
