package com.flipo.avivams.flipo.ui;

import android.animation.Animator;
import android.view.View;

/**
 * Created by aviv_ams on 05/06/2018.
 */

public class MenuOptionAnimation implements Animator.AnimatorListener {

    private View view;

    public MenuOptionAnimation(View button){
        this.view = button;
    }


    @Override
    public void onAnimationStart(Animator animator) {

        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        if(view.getAlpha() == 0){
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
