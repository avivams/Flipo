package com.flipo.avivams.flipo.ui;


import android.support.annotation.NonNull;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flipo.avivams.flipo.fragments.DrawingFragment;
import com.wacom.ink.rasterization.StrokePaint;
import com.wacom.ink.rasterization.StrokeRenderer;

/**
 * Created by aviv_ams on 30/04/2018.
 */

public class SeekBarListener implements SeekBar.OnSeekBarChangeListener{

    protected int m_minimumValue;
    protected int m_progressValue;
    private TextView m_currentValTxt;

    public SeekBarListener(int currentProgress, int minimumValue, @NonNull TextView currentValTxt){
        m_minimumValue = minimumValue;
        m_currentValTxt = currentValTxt;
        m_progressValue = currentProgress;
        m_currentValTxt.setText(String.valueOf(m_progressValue));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        /*if(i == 0) {
            //hold the bar on 1
            seekBar.setProgress(m_minimumValue);
            m_currentValTxt.setText(String.valueOf(m_progressValue = m_minimumValue));//update text
        }else {
            //do not let the bar to get the Max+1
            m_progressValue = (i == seekBar.getMax() + 1 ?  seekBar.getMax() : m_minimumValue + i);
            m_currentValTxt.setText(String.valueOf(m_progressValue-1));//update text
        }*/
        if(i == 0) {
            m_currentValTxt.setText(String.valueOf(m_progressValue = m_minimumValue));//update text
        }else {
            m_progressValue = m_minimumValue + i;
            m_currentValTxt.setText(String.valueOf(m_progressValue));//update text
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
     //   seekBar.setProgress(m_progressValue-1);
     //Log.d("seekbar", ""+seekBar.getProgress());
    }


    public static class BrushSeekBar extends SeekBarListener{
        StrokePaint mPaint;
        DrawingFragment.OnDrawingInteractionListener mListener;

        public BrushSeekBar(int currentProgress, int minimumValue,
                            @NonNull TextView currentValTxt, @NonNull StrokePaint sPaint,
                            @NonNull DrawingFragment.OnDrawingInteractionListener listener){
           super(currentProgress, minimumValue, currentValTxt);
           mPaint = sPaint;
           mListener = listener;
        }


        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

           // seekBar.setProgress(m_progressValue - m_minimumValue);
            mPaint.setWidth(m_progressValue);
            mListener.getRenderer().setStrokePaint(mPaint);
        }
    }
}
