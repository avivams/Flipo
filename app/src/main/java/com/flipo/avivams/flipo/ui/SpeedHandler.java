package com.flipo.avivams.flipo.ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flipo.avivams.flipo.R;

import java.util.LinkedList;

/**
 * Created by aviv_ams on 10/07/2018.
 */

public class SpeedHandler {

    private ImageButton m_cancelBtn, m_confirmBtn;
    private SeekBar m_speedSeekBar;
    private int m_speed;
    private LinkedList<View> m_viewsToToggle;

    /**
     *
     * @param context The activity's context.
     * @param listener The listener to be called when the speed is changed.
     * @param defaultSpeed The default speed to start with.
     * @param cancelBtn The button to cancel speed change on seek-bar's slider
     * @param confirmBtn The button to confirm speed change on seek-bar's slider
     * @param speedSeekBar The speed seek-bar
     * @param speedProgress The text view to show upon the progress of the seek-bar
     * @param viewsToToggle other views to show/hide when the seek-bar needs to show up.
     */
    public SpeedHandler(@NonNull Context context,
                        @NonNull OnSpeedChangeListener listener,
                        int defaultSpeed,
                        @NonNull ImageButton cancelBtn,
                        @NonNull ImageButton confirmBtn,
                        @NonNull SeekBar speedSeekBar,
                        @NonNull TextView speedProgress,
                        LinkedList<View> viewsToToggle){

        m_cancelBtn = cancelBtn;
        m_confirmBtn = confirmBtn;
        m_speedSeekBar = speedSeekBar;
        m_viewsToToggle = viewsToToggle;
        m_speed = defaultSpeed;
        m_speedSeekBar.setProgress(defaultSpeed);

        initButtonsListeners(listener);
        initSeekBarListener(speedProgress, context);
    }



    private void initButtonsListeners(final OnSpeedChangeListener listener){
        m_cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_speedSeekBar.setProgress(m_speed);
                toggleViews();
            }
        });

        m_confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_speed = m_speedSeekBar.getProgress();
                listener.newSpeed(m_speed);
                toggleViews();
            }
        });
    }



    private void initSeekBarListener(TextView speedProgress, Context context){
        Resources res = context.getResources();

        m_speedSeekBar.setOnSeekBarChangeListener(new SeekBarListener.SpeedSeekBar(
                m_speed, res.getInteger(R.integer.min_params_speed),
                res.getInteger(R.integer.max_params_speed),
                res.getInteger(R.integer.seekbar_speed_jumps),
                speedProgress
        ));
    }




    private void toggleViews(){

        int show = View.VISIBLE;

        // show/hide all other views
        if(!(m_viewsToToggle == null || m_viewsToToggle.isEmpty())){
            boolean isVisible = m_viewsToToggle.get(0).getVisibility() == View.VISIBLE;
            show = isVisible? View.GONE : View.VISIBLE;

            for(View view : m_viewsToToggle){
                view.setVisibility(show);
            }
        }

        boolean isVisible = m_speedSeekBar.getVisibility() == View.VISIBLE;
        show = isVisible? View.GONE : View.VISIBLE; // show the seek bar and confirmation buttons
        m_confirmBtn.setVisibility(show);
        m_cancelBtn.setVisibility(show);
        m_speedSeekBar.setVisibility(show);
    }




    public interface OnSpeedChangeListener{
        /**
         * when the speed is changed, this method will be called with the new speed value.
         * @param speed
         */
        void newSpeed(int speed);
    }
}
