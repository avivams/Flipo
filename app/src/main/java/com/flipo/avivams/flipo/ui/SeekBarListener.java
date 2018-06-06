package com.flipo.avivams.flipo.ui;


import android.support.annotation.NonNull;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flipo.avivams.flipo.fragments.DrawingFragment;
import com.wacom.ink.rasterization.StrokePaint;

/**
 * Created by aviv_ams on 30/04/2018.
 */

public class SeekBarListener implements SeekBar.OnSeekBarChangeListener{

    protected int m_minimumValue;
    protected int m_progressValue;
    private int jump;
    private TextView m_currentValTxt;

    public SeekBarListener(int currentProgress, int minimumValue, @NonNull TextView currentValTxt, int jump){
        m_minimumValue = minimumValue;
        m_currentValTxt = currentValTxt;
        m_progressValue = currentProgress;
        m_currentValTxt.setText(String.valueOf(m_progressValue));
        this.jump = jump;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        if(b) {
            if (i == 0) {
                m_currentValTxt.setText(String.valueOf(m_progressValue = m_minimumValue));//update text
            } else {

                i = i / jump;
                m_progressValue = m_minimumValue + i * jump;
                m_currentValTxt.setText(String.valueOf(m_progressValue));//update text
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        seekBar.setProgress(m_progressValue);
     //   seekBar.setProgress(m_progressValue-1);
     //Log.d("seekbar", ""+seekBar.getProgress());
    }


    public static class BrushSeekBar extends SeekBarListener{
        StrokePaint mPaint;
        DrawingFragment.OnDrawingInteractionListener mListener;

        public BrushSeekBar(int currentProgress, int minimumValue, int jump,
                            @NonNull TextView currentValTxt, @NonNull StrokePaint sPaint,
                            @NonNull DrawingFragment.OnDrawingInteractionListener listener){
           super(currentProgress, minimumValue, currentValTxt, jump);
           mPaint = sPaint;
           mListener = listener;
        }


        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

           // seekBar.setProgress(m_progressValue - m_minimumValue);
            seekBar.setProgress(m_progressValue - m_minimumValue);
            mPaint.setWidth(m_progressValue);
            mListener.getRenderer().setStrokePaint(mPaint);
        }
    }


    public static class SpeedSeekBar extends SeekBarListener{

        private MenuManager.MenuManagerListener mListener;

        public SpeedSeekBar(int currentProgress, int minimumValue, int jump,
                            @NonNull TextView currentValTxt,
                            @NonNull MenuManager.MenuManagerListener listener){

            super(currentProgress, minimumValue, currentValTxt, jump);
            mListener = listener;
        }


        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekBar.setProgress(m_progressValue - m_minimumValue);
            mListener.setNewSpeed(m_progressValue);
        }
    }
}
