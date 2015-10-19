package com.casky.dlna.control;

import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import org.fourthline.cling.model.ModelUtil;

/**
 * Created by wangbo on 14-11-27.
 */
public class SeekBarController implements SeekBar.OnSeekBarChangeListener{

    private static final String TAG = "SeekBarController";

    private static SeekBarController controller = null;

    private SeekBarModule seekBarMudule = null;
    private DlnaServiceManager dlnaServiceManager = null;

    private  SeekBarController(SeekBarModule seekBarMudule,DlnaServiceManager dlnaServiceManager){
        this.seekBarMudule = seekBarMudule;
        this.dlnaServiceManager = dlnaServiceManager;
    }

    public static SeekBarController getSeekBarControllerInstance(SeekBarModule seekBarMudule,DlnaServiceManager dlnaServiceManager){
        if(controller != null) return controller;
        return new SeekBarController(seekBarMudule,dlnaServiceManager);
    }

    public void bindListener(){
        seekBarMudule.seekBar.setOnSeekBarChangeListener(this);
        Log.d(TAG,"bindListener");
    }

    public  void setSeekBarMax(int max){
        seekBarMudule.seekBar.setMax(max);
    }

    public void setSeekBarProgress(int progress){
        seekBarMudule.seekBar.setProgress(progress);
    }

    public void setDuration(String duration){
        seekBarMudule.duration.setText(duration);
    }

    public void setCurrentTime(String currentTime){
        seekBarMudule.currentTime.setText(currentTime);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.e(TAG, "seek to " + seekBar.getProgress() + "max "+seekBar.getMax());

    }


    public static  class  SeekBarModule {
        SeekBar seekBar = null;
        TextView duration = null;
        TextView currentTime = null;

        public SeekBarModule(SeekBar seekBar, TextView duration, TextView currentTime) {
            this.seekBar = seekBar;
            this.duration = duration;
            this.currentTime = currentTime;
        }
    }

}
