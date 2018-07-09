package com.flipo.avivams.flipo.ui;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flipo.avivams.flipo.R;
import com.flipo.avivams.flipo.animation.ResizeAnimation;
import com.flipo.avivams.flipo.fragments.DrawingFragment;
import com.wacom.ink.rasterization.StrokePaint;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by aviv_ams on 05/06/2018.
 */

public class MenuManager {

    private int brushSizes[];
    private ArrayList<Integer> brushColors;
    private LinearLayout m_btnMenuView;
    private ImageButton m_btnOpnDraw;
    private boolean menuVisible;

    private int openWidth, openHeight, closeWidth, closeHeight;

    public MenuManager(Context context) {

        brushColors = new ArrayList<>();
        brushSizes = context.getResources().getIntArray(R.array.brush_sizes);

        Resources resources = context.getResources();

        openWidth = (int)resources.getDimension(R.dimen.palette_width);
        openHeight = (int)resources.getDimension(R.dimen.palette_height);
        closeHeight = (int)resources.getDimension(R.dimen.menu_bar_tools_btn_height);
        closeWidth = (int)resources.getDimension(R.dimen.menu_bar_tools_btn_width);

        // get colors from 'array.xml'
        TypedArray colors = context.getResources().obtainTypedArray(R.array.palette_colors);
        for (int i=0; i < colors.length(); i++) {
            brushColors.add(colors.getColor(i, 0));
        }
        colors.recycle(); // finished using the colors TypedArray
    }


    /**
     * After the opnMenuButton, insert each button button ordered by their position after each other.
     * @param opnMenuView the menu.
     * @param btnOpnButton the button which responsible of opening the menu.
     * @param buttons the buttons to show on the menu.
     */
    public void registerButtons(
            final DrawingFragment.OnDrawingInteractionListener drawListener,
            final MenuManagerListener listener,
            LinearLayout opnMenuView, ImageButton btnOpnButton, int numOfSizes, int numOfColors, ImageView... buttons){

        m_btnMenuView = opnMenuView;
        m_btnOpnDraw = btnOpnButton;

        initAnimsListeners(drawListener, listener, numOfSizes, numOfColors, buttons);
    }



    /**
     * sets listeners to animations (which also take control of their Visibility).
     */
    private void initAnimsListeners(final DrawingFragment.OnDrawingInteractionListener drawListener,
                                    final MenuManagerListener listener,
                                    int numOfSizes, int numOfColors, ImageView[] buttons){

        int i=0;

        for (; i < numOfSizes; i++) {
            final int j = i;

            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.getPaint().setWidth(brushSizes[j]);
                    drawListener.getRenderer().setStrokePaint(listener.getPaint());
                }
            });
         //   buttons[i].animate().setListener(new MenuOptionAnimation(buttons[i]));
        }

        int k = 0;
        for(; i < buttons.length; i++, k++){
            final int j = k;

            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.getPaint().setColor(brushColors.get(j));
                    drawListener.getRenderer().setStrokePaint(listener.getPaint());
                }
            });
        //    buttons[i].animate().setListener(new MenuOptionAnimation(buttons[i]));
        }

        m_btnMenuView.findViewById(R.id.draw_icon).setOnClickListener(new View.OnClickListener() {
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

            // set the starting height (the current height) and the new height that the view should have after the animation
            ResizeAnimation anim = new ResizeAnimation(m_btnMenuView);
            anim.setHeights(m_btnMenuView.getHeight(), openHeight);
            anim.setWidths(m_btnMenuView.getWidth(), openWidth);
            anim.setOriginXY(m_btnMenuView.getX(), m_btnMenuView.getY());

            anim.setDuration(500);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    m_btnMenuView.setVisibility(View.VISIBLE);
                    m_btnOpnDraw.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            m_btnMenuView.startAnimation(anim);

            menuVisible = true;
        }
        else {


            // set the starting height (the current height) and the new height that the view should have after the animation
            ResizeAnimation anim = new ResizeAnimation(m_btnMenuView);
            anim.setHeights(m_btnMenuView.getHeight(), closeHeight);
            anim.setWidths(m_btnMenuView.getWidth(), closeWidth);
            anim.setOriginXY(m_btnMenuView.getX(), m_btnMenuView.getY());

            anim.setDuration(500);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    m_btnMenuView.setVisibility(View.GONE);
                    m_btnOpnDraw.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            m_btnMenuView.startAnimation(anim);
            m_btnOpnDraw.setVisibility(View.VISIBLE);

            menuVisible = false;
        }

    }


    public boolean isMenuVisible(){
        return menuVisible;
    }


    public interface MenuManagerListener{
        StrokePaint getPaint();
        void setNewSpeed(int speed);
    }


/*
    private void initBrushTab(y activity,
                              final DrawingFragment.OnDrawingInteractionListener drawListener,
                              final MenuManagerListener menuListener){

        m_btnTitle.setImageDrawable(activity.getDrawable(R.drawable.brush_style));

        ViewGroup group =  ((ViewGroup)m_menuTabView);
        if(group.getChildAt(0) != null)
            group.removeViewAt(0);
        View view = activity.getLayoutInflater().inflate(R.layout.menu_tab_brush, group, false);
        group.addView(view, 0);

        GridView gridview = view.findViewById(R.id.menu_style_palette);
        gridview.setAdapter(new PaletteAdapter(activity));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                menuListener.getPaint().setColor(((ColorDrawable)v.getBackground()).getColor());
                drawListener.getRenderer().setStrokePaint(menuListener.getPaint());
            }
        });


    }*/



/*
    private void initParamsTab(Activity activity,
                               final MenuManagerListener menuListener){

        m_btnTitle.setImageDrawable(activity.getDrawable(R.drawable.parameters));

        ViewGroup group =  ((ViewGroup)m_menuTabView);
        if(group.getChildAt(0) != null)
            group.removeViewAt(0);
        View view = activity.getLayoutInflater().inflate(R.layout.menu_tab_params, group, false);
        group.addView(view, 0);

        Resources res = activity.getResources();
        SeekBar seekBar = view.findViewById(R.id.skbar_params_speed);

        seekBar.setOnSeekBarChangeListener(

                new SeekBarListener.SpeedSeekBar(
                        res.getInteger(R.integer.default_params_speed),
                        res.getInteger(R.integer.min_params_speed),
                        res.getInteger(R.integer.max_params_speed),
                        res.getInteger(R.integer.seekbar_speed_jumps),
                        (TextView)view.findViewById(R.id.txt_params_speed),
                        menuListener));
        seekBar.incrementProgressBy(R.integer.seekbar_speed_jumps);
        seekBar.setMax(res.getInteger(R.integer.max_params_speed) - res.getInteger(R.integer.min_params_speed));
        seekBar.setProgress(res.getInteger(R.integer.default_params_speed) - res.getInteger(R.integer.min_params_speed) );
    }*/


}


