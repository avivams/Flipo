package com.flipo.avivams.flipo.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flipo.avivams.flipo.R;
import com.flipo.avivams.flipo.utilities.Animation;
import com.flipo.avivams.flipo.utilities.Shape;

import java.util.LinkedList;


public class PreviewFragment extends Fragment {
    //test master
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
        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

}
