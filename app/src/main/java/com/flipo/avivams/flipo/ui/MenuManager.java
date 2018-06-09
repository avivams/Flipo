package com.flipo.avivams.flipo.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flipo.avivams.flipo.R;
import com.flipo.avivams.flipo.fragments.DrawingFragment;
import com.wacom.ink.rasterization.StrokePaint;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by aviv_ams on 05/06/2018.
 */

public class MenuManager {

    public enum TabType {STYLE_TAB, PARAMS_TAB};
    private enum AnimType{BUTTONS, TEXTS};

    private ArrayList<ImageButton> buttons;
    private ArrayList<TextView> buttons_text;
    private View m_menuTabView;
    private ImageButton m_btnTitle;
    private ImageButton m_btnMenuOpn;
    private boolean menuVisible;

    public MenuManager() {
    }


    /**
     * After the opnMenuButton, insert each button button ordered by their position after each other.
     * @param opnMenuButton the button which responsible of opening the menu.
     * @param buttons the buttons to show on the menu.
     */
    public void registerButtons(ImageButton opnMenuButton, ImageButton... buttons){
        if(this.buttons != null)
            this.buttons.clear();

        this.buttons = new ArrayList<>(Arrays.asList(buttons));
        m_btnMenuOpn = opnMenuButton;

        initAnimsListeners(AnimType.BUTTONS);
    }

    /**
     * Insert texts strings by the order of the buttons which passed to 'registerButtons' function.
     * @param texts the texts which will be shown besides each button
     */
    public void registerButtonsText(TextView... texts){
        if(buttons_text != null)
            buttons_text.clear();

        buttons_text = new ArrayList<>(Arrays.asList(texts));
        initAnimsListeners(AnimType.TEXTS);
    }


    public void registerTab(View tab){
        this.m_menuTabView = tab;
        final View parentTab = ((View)m_menuTabView.getParent());
        m_btnTitle = parentTab.findViewById(R.id.btn_menu_title_image);
        m_btnTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeTab();
            }
        });
        parentTab.animate().setListener(new MenuOptionAnimation(parentTab));
    }


    /**
     * sets listeners to animations (which also take control of their Visibility).
     */
    private void initAnimsListeners(AnimType type){

        if(type == AnimType.BUTTONS) {
            for (ImageButton button : buttons) {
                button.animate().setListener(new MenuOptionAnimation(button));
            }
        }
        else if(type == AnimType.TEXTS) {
            for (TextView textView : buttons_text) {
                textView.animate().setListener(new MenuOptionAnimation(textView));
            }
        }
    }

    /**
     * animation action for the top left menu bar
     * @param open is opening animation
     */
    public void animateMenu(Activity activity, boolean open){

        if(open){
            m_btnMenuOpn.setImageDrawable(activity.getDrawable(R.drawable.menu_close));

            if(buttons != null && buttons.size() > 0) {
                buttons.get(0).animate().alpha(1.0f);
                buttons_text.get(0).animate().alpha(1.0f);
                for (int i = 1; i < buttons.size(); i++) {
                    buttons.get(i).animate().translationYBy(-(activity.getResources().getDimension(R.dimen.menu_bar_tools_btn_height) * i)).alpha(1.0f);
                    buttons_text.get(i).animate().translationYBy(-(activity.getResources().getDimension(R.dimen.menu_bar_tools_btn_height) * i)).alpha(1.0f);
                }
            }

            menuVisible = true;
        }
        else {
            m_btnMenuOpn.setImageDrawable(activity.getDrawable(R.drawable.menu_open));

            closeTab();

            if(buttons != null && buttons.size() > 0) {
                buttons.get(0).animate().alpha(0);
                buttons_text.get(0).animate().alpha(0);
                for (int i = buttons.size() - 1; i > 0; i--) {
                    buttons.get(i).animate().translationYBy(activity.getResources().getDimension(R.dimen.menu_bar_tools_btn_height) * i).alpha(0);
                    buttons_text.get(i).animate().translationYBy(activity.getResources().getDimension(R.dimen.menu_bar_tools_btn_height) * i).alpha(0);
                }
            }

            menuVisible = false;
        }

    }


    public void closeTab(){
        final View parentTab = ((View)m_menuTabView.getParent());
        parentTab.setAlpha(0.5f);
        parentTab.animate().alpha(0).setDuration(50);

        for(ImageButton button : buttons){
            button.setVisibility(View.VISIBLE);
            button.setAlpha(1.0f);
        }

        for(TextView textView : buttons_text){
            textView.setVisibility(View.VISIBLE);
            textView.setAlpha(1.0f);
        }
    }

    public void openTab(TabType tabType, Activity activity, DrawingFragment.OnDrawingInteractionListener drawListener, MenuManagerListener menuListener){

        View parentTab = ((View)m_menuTabView.getParent());
        parentTab.animate().alpha(1.0f);

        for(ImageButton button : buttons){
            button.animate().alpha(0);
        }
        for(TextView textView : buttons_text){
            textView.animate().alpha(0);
        }

        switch (tabType){
            case STYLE_TAB:
                initBrushTab(activity, drawListener, menuListener);
                break;
            default:
                initParamsTab(activity, menuListener);
                break;
        }

    }


    public boolean isMenuVisible(){
        return menuVisible;
    }


    private void initBrushTab(Activity activity,
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


        Resources res = activity.getResources();
        SeekBar seekBar = view.findViewById(R.id.skbar_brush_size);
        seekBar.setOnSeekBarChangeListener(
                new SeekBarListener.BrushSeekBar(
                        res.getInteger(R.integer.default_brush_size),
                        res.getInteger(R.integer.min_brush_size),
                        res.getInteger(R.integer.seekbar_brush_size_jumps),
                        (TextView)view.findViewById(R.id.txt_brush_size),
                        menuListener.getPaint(),
                        drawListener));
        seekBar.incrementProgressBy(R.integer.seekbar_brush_size_jumps);
        seekBar.setMax(res.getInteger(R.integer.max_brush_size) - res.getInteger(R.integer.min_brush_size));
        seekBar.setProgress(res.getInteger(R.integer.default_brush_size) - res.getInteger(R.integer.min_brush_size) );


    }


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
    }

    public interface MenuManagerListener{
        StrokePaint getPaint();
        void setNewSpeed(int speed);
    }
}


