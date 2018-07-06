package com.flipo.avivams.flipo.ui;

import android.animation.Animator;
import android.view.View;

/**
 * Created by aviv_ams on 05/06/2018.
 */

public class MenuOptionAnimation implements Animator.AnimatorListener {

    private View view, affected;


    public MenuOptionAnimation(View current, View affected){
        this.view = current;
        this.affected = affected;
    }


    @Override
    public void onAnimationStart(Animator animator) {
        view.setVisibility(View.VISIBLE);
        if(affected != null)
            affected.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationEnd(Animator animator) {

        view.setVisibility(View.GONE);
        if(affected != null)
            affected.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
