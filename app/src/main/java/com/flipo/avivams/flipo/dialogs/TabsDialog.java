package com.flipo.avivams.flipo.dialogs;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.flipo.avivams.flipo.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by aviv_ams on 07/06/2018.
 */

public class TabsDialog extends DialogFragment {

    public enum TabType {SHARE_TAB, SAVE_TAB};

    private ArrayList<View> views;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private EditText m_emailAd, m_saveName;
    private Button m_btnSend, m_btnSave;

    private DialogMatcher.RecordResultDialogListener listener;


    public TabsDialog(){}

    @Override
    public void onResume() {
        super.onResume();
        Window window =  getDialog().getWindow();
        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });
        window.setLayout((int)getActivity().getResources().getDimension(R.dimen.preview_dialog_record_result_width),
                (int)getActivity().getResources().getDimension(R.dimen.preview_dialog_record_result_height));
        Toast.makeText(getActivity(), "Width = " +  window.getAttributes().width + " Height="+window.getAttributes().height, Toast.LENGTH_LONG).show();

    }

    public static TabsDialog TabsDialogInstance(@NonNull DialogMatcher.RecordResultDialogListener listener) throws RuntimeException{
        TabsDialog dialog = new TabsDialog();
        dialog.listener = listener;
        return dialog;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        views = new ArrayList<>();

        View rootview = getActivity().getLayoutInflater().inflate(R.layout.dialog_record_result, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        tabLayout = (TabLayout) rootview.findViewById(R.id.tabs_title);
        viewPager = (ViewPager) rootview.findViewById(R.id.pages_container);

        initViews();
        initTabs();
        setAdapter();

    //    tabLayout.setupWithViewPager(viewPager, false);


        rootview.setLayoutParams(new ViewGroup.LayoutParams((int)getActivity().getResources().getDimension(R.dimen.preview_dialog_record_result_width),
                (int)getActivity().getResources().getDimension(R.dimen.preview_dialog_record_result_height) ));


        builder.setView(rootview);
        Dialog dlg = builder.create();
        Window window =  dlg.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.getAttributes().windowAnimations = R.style.Dialog_PopUp;

        return dlg;
    }


    private void initViews(){
        Activity activity = getActivity();

        View shareTab =  activity.getLayoutInflater().inflate(R.layout.dialog_record_result_tabview, null);

        m_emailAd = shareTab.findViewById(R.id.preview_tab_txt_user_input);
        m_emailAd.setHint(R.string.dialog_record_share_input_hint);
        m_btnSend = shareTab.findViewById(R.id.preview_tab_btn_confirm);
        m_btnSend.setText(getString(R.string.dialog_record_share_btn_txt));
        m_btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onConfirmButtonClicked(TabType.SHARE_TAB, m_emailAd.getText().toString());
            }
        });
        ((TextView)shareTab.findViewById(R.id.preview_tab_txt_title)).setText(getString(R.string.dialog_record_share_title));



        View saveTab =  activity.getLayoutInflater().inflate(R.layout.dialog_record_result_tabview, null);

        m_saveName = saveTab.findViewById(R.id.preview_tab_txt_user_input);
        m_saveName.setHint(R.string.dialog_record_save_input_hint);
        m_btnSave = saveTab.findViewById(R.id.preview_tab_btn_confirm);
        m_btnSave.setText(getString(R.string.dialog_record_save_btn_txt));
        m_btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onConfirmButtonClicked(TabType.SAVE_TAB, m_saveName.getText().toString());
            }
        });
        ((TextView)saveTab.findViewById(R.id.preview_tab_txt_title)).setText(getString(R.string.dialog_record_save_title));

        viewPager.setCurrentItem(0);
        viewPager.addView(saveTab);
        views.add(0, saveTab);
        views.add(1, shareTab);
    }


    private void initTabs(){
        Activity activity = getActivity();
        LayoutInflater inflater = activity.getLayoutInflater();

        View tabSave = inflater.inflate(R.layout.dialog_record_result_tab, null);
        ((ImageView)tabSave.findViewById(R.id.icon)).setImageResource(R.drawable.ic_save_tab);
        ((TextView)tabSave.findViewById(R.id.textView)).setText(getString(R.string.dialog_record_tab_save_txt));


        View tabShare = inflater.inflate(R.layout.dialog_record_result_tab, null);
        ((ImageView)tabShare.findViewById(R.id.icon)).setImageResource(R.drawable.ic_share_tab);
        ((TextView)tabShare.findViewById(R.id.textView)).setText(getString(R.string.dialog_record_tab_share_txt));

        tabLayout.addTab(tabLayout.newTab().setCustomView(tabSave), 0);
        tabLayout.addTab(tabLayout.newTab().setCustomView(tabShare), 1);

    }


    private void setAdapter(){
        final PagerAdapter adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return tabLayout.getTabCount();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return true;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
//                super.instantiateItem(container, position);
                return getActivity().getLayoutInflater().inflate(R.layout.dialog_record_result_tab, null);
            }
        };

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) );
        tabLayout.addOnTabSelectedListener(tabListener);
        viewPager.setCurrentItem(0);

    }



    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
       // resizeDialog();
    }


    @Override
    public void dismiss() {
        listener = null;
        super.dismiss();
    }

    /**
     * To resize the size of this dialog
     */
    private void resizeDialog() {
        try {
            Window window = getDialog().getWindow();
            Activity activity = getActivity();

            if (activity == null || window == null) return;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            window.setLayout(50, 50);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.getAttributes().windowAnimations = R.style.Dialog_PopUp;

        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }



    private TabLayout.OnTabSelectedListener tabListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition(), true);
            viewPager.getAdapter().notifyDataSetChanged();
            viewPager.removeView(views.get(tab.getPosition()));
            viewPager.addView(views.get(tab.getPosition()));
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            viewPager.removeView(views.get(tab.getPosition()));
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };


}
