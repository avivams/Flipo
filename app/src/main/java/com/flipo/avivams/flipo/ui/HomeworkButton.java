package com.flipo.avivams.flipo.ui;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;

import com.flipo.avivams.flipo.R;
import com.flipo.avivams.flipo.animation.ResizeAnimation;

/**
 * Created by aviv_ams: on 08/07/2018.
 */

public class HomeworkButton {

    private View m_homeworkWindow;
    private Button m_okBtn, m_homeworkBtn;
    private int wind_openHeight, wind_closeHeight, hwBtn_openHeight, hwBtn_closeHeight;
    private boolean menuVisible;

    public HomeworkButton(Context context, Button homeworkBtn, View homeworkWindow) {

        m_homeworkBtn = homeworkBtn;

        Resources resources = context.getResources();

        wind_openHeight = (int)resources.getDimension(R.dimen.dialog_assignment_height);
        hwBtn_openHeight = (int)resources.getDimension(R.dimen.hw_height);

        wind_closeHeight = hwBtn_closeHeight = 1;

        registerViews(homeworkWindow);

        menuVisible = false;
    }


    /**
     * After the opnMenuButton, insert each button button ordered by their position after each other.
     * @param homeworkWindow the window.
     */
    private void registerViews(View homeworkWindow){

        m_homeworkWindow = homeworkWindow;

        m_okBtn = m_homeworkWindow.findViewById(R.id.homework_ok_btn);

        m_homeworkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateMenu(true);
            }
        });

        m_okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateMenu(false);
            }
        });
    }


    /**
     * animation action for the brush palette.
     * @param open is opening animation?
     */
    public void animateMenu(boolean open){

        if(open){

            ResizeAnimation anim = getHWBtnCloseAnim();
            m_homeworkBtn.startAnimation(anim);

            menuVisible = true;
        }
        else {

            ResizeAnimation anim = getWindowCloseAnim();
            m_homeworkWindow.startAnimation(anim);

            menuVisible = false;
        }
    }

    private ResizeAnimation getHWBtnCloseAnim(){
        ResizeAnimation resizeAnimation = new ResizeAnimation(m_homeworkBtn);

        resizeAnimation.setHeights(m_homeworkBtn.getHeight(), hwBtn_closeHeight);
        //    anim.setOriginXY(m_btnMenuView.getX(), m_btnMenuView.getY());

        resizeAnimation.setDuration(500);

        resizeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                m_homeworkBtn.setVisibility(View.INVISIBLE);
                m_homeworkWindow.setVisibility(View.VISIBLE);
                //start the window animation
                m_homeworkWindow.startAnimation(getWindowOpenAnim());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return resizeAnimation;
    }

    private ResizeAnimation getHWBtnOpenAnim(){
        ResizeAnimation resizeAnimation = new ResizeAnimation(m_homeworkBtn);

        resizeAnimation.setHeights(m_homeworkBtn.getHeight(), hwBtn_openHeight);
        //    anim.setOriginXY(m_btnMenuView.getX(), m_btnMenuView.getY());


        resizeAnimation.setDuration(500);

        return resizeAnimation;
    }

    // close the window and start the HW button
    private ResizeAnimation getWindowCloseAnim(){
        ResizeAnimation resizeAnimation = new ResizeAnimation(m_homeworkWindow);

        resizeAnimation.setHeights(m_homeworkWindow.getHeight(), wind_closeHeight);
        //    anim.setOriginXY(m_btnMenuView.getX(), m_btnMenuView.getY());

        resizeAnimation.setDuration(500);

        resizeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                m_homeworkWindow.setVisibility(View.INVISIBLE);
                m_homeworkBtn.setVisibility(View.VISIBLE);
                // the window is closed, then open the HW button
                m_homeworkBtn.startAnimation(getHWBtnOpenAnim());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return resizeAnimation;
    }

    // open the window
    private ResizeAnimation getWindowOpenAnim(){
        ResizeAnimation resizeAnimation = new ResizeAnimation(m_homeworkWindow);

        resizeAnimation.setHeights(m_homeworkBtn.getHeight(), wind_openHeight);
        //    anim.setOriginXY(m_btnMenuView.getX(), m_btnMenuView.getY());

        resizeAnimation.setDuration(500);


        return resizeAnimation;
    }


    public boolean isMenuVisible(){
        return menuVisible;
    }

}
