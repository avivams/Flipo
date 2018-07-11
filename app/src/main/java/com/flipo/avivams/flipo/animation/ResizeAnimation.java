package com.flipo.avivams.flipo.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by אבי on 05/07/2018.
 */

public class ResizeAnimation extends Animation {

    private int startHeight;
    private int deltaHeight;

    private int startWidth;
    private int deltaWidth;

    private View view;

    private float originX, originY;

    public ResizeAnimation(View view) {
        this.view = view;

        originX = originY = -1.0f;
    }

    public void setHeights(int start, int end) {
        this.startHeight = start;
        this.deltaHeight = end - this.startHeight;
    }

    public void setWidths(int start, int end) {
        this.startWidth = start;
        this.deltaWidth = end - this.startWidth;
    }

    public void setOriginXY(float x, float y){
        originX = x;
        originY = y;
    }


    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        System.out.println(view.getLayoutParams().height +"\n");
        if (startHeight != 0) {
            if (deltaHeight > 0) {
                view.getLayoutParams().height = (int) (startHeight + deltaHeight * interpolatedTime);
            } else {
                view.getLayoutParams().height = (int) (startHeight - Math.abs(deltaHeight) * interpolatedTime);
            }
        }

        if (startWidth != 0) {
            if (deltaWidth > 0) {
                view.getLayoutParams().width = (int) (startWidth + deltaWidth * interpolatedTime);
            } else {
                view.getLayoutParams().width = (int) (startWidth - Math.abs(deltaWidth) * interpolatedTime);
            }
        }

        if(originX >= 0)
            view.setX(originX);
        if(originY >= 0)
            view.setY(originY);

        view.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

}
