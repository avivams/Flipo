package com.flipo.avivams.flipo.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.flipo.avivams.flipo.R;
import com.flipo.avivams.flipo.dialogs.DialogMatcher;
import com.flipo.avivams.flipo.dialogs.TabsDialog;
import com.flipo.avivams.flipo.utilities.Animation;
import com.flipo.avivams.flipo.utilities.Shape;

import java.util.LinkedList;


public class PreviewFragment extends Fragment implements DialogMatcher.RecordResultDialogListener{

    private LinkedList<Shape> shapesList;
    private LinkedList<Animation> anims;

    public PreviewFragment() {
        // Required empty public constructor
    }


    public static PreviewFragment newInstance(LinkedList<Shape> shapesList, LinkedList<Animation> anims) {
        PreviewFragment fragment = new PreviewFragment();
        fragment.shapesList = shapesList;
        fragment.anims = anims;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_preview, container, false);
        ((ImageButton)v.findViewById(R.id.preview_btn_play)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogMatcher.showDialog(getActivity(),
                        DialogMatcher.PreviewDialogType.RECORD_RESULT,
                        getFragmentManager().beginTransaction(),
                        PreviewFragment.this);
            }
        });
        return v;
    }

    @Override
    public void onConfirmButtonClicked(TabsDialog.TabType tabType, String userInput) {

    }
}
