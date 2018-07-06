package com.flipo.avivams.flipo.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flipo.avivams.flipo.R;

/**
 * Created by aviv_ams on 07/06/2018.
 */

public class CustomFragment extends Fragment {

    private String mText = "";
    public static CustomFragment createInstance(String txt)
    {
        CustomFragment fragment = new CustomFragment();
        fragment.mText = txt;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_record_result_tab,container,false);
       // ((TextView) v.findViewById(R.id.textView)).setText(mText);
        return v;
    }
}
