package com.flipo.avivams.flipo.fragments;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Path;
import android.os.Bundle;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flipo.avivams.flipo.R;
import com.flipo.avivams.flipo.utilities.Animation;
import com.flipo.avivams.flipo.utilities.MyView;
import com.flipo.avivams.flipo.utilities.Shape;
import com.flipo.avivams.flipo.utilities.Stroke;
import com.wacom.ink.boundary.Boundary;
import com.wacom.ink.boundary.BoundaryBuilder;

import java.nio.FloatBuffer;
import java.util.LinkedList;


public class PreviewFragment extends Fragment {

    private LinkedList<Shape> m_ShapesList;
    private LinkedList<Animation> m_AnimationsInfo;
    private LinkedList<ObjectAnimator> m_Animations;
    private LinkedList<Path> m_Pathes;
    private LinkedList<View> m_Views;
    private LinkedList<float[]> m_Points;

    public PreviewFragment() {
        // Required empty public constructor
    }


    public static PreviewFragment newInstance(LinkedList<Shape> shapesList, LinkedList<Animation> anims) {
        PreviewFragment fragment = new PreviewFragment();
        fragment.m_ShapesList = shapesList;
        fragment.m_AnimationsInfo = anims;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_preview, container, false);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);

        for(View view : m_Views){
            view.setLayoutParams(layoutParams);
            ((ConstraintLayout) myView.findViewById(R.id.layoutPrieview)).addView(view, layoutParams);
        }

        return myView;
    }

    public void SetReady(Activity i_MainActivity){
        CreatePathes();
        CreateViews(i_MainActivity);
    }

    public void CreatePathes(){
        m_Pathes = new LinkedList<>();

        for(Animation animation : m_AnimationsInfo){
            m_Pathes.add(getPathFromStroke(animation.GetAnimationPath().GetPath()));
        }

        m_Points = new LinkedList<>();

        for(Animation animation : m_AnimationsInfo){
            FloatBuffer floatBuffer = animation.GetAnimationPath().GetPath().get(0).getPoints();
            float[] floatList = new float[floatBuffer.capacity()];
            floatBuffer.flip();
            int i = 0;
            while(floatBuffer.remaining() > 0){
                floatList[i] = floatBuffer.get();
                i++;
            }

            m_Points.add(floatList);
        }
    }

    public void CreateViews(Activity i_MainActivity){
        m_Views = new LinkedList<>();

        for(Animation animation : m_AnimationsInfo){
            View view = getViewToAnimate(animation.GetAnimationObject().getShape(), i_MainActivity);
            view.setY(0);
            view.setY(0);
            m_Views.add(view);
        }

        for(Shape shape : m_ShapesList){
            View view = getViewToAnimate(shape.getShape(), i_MainActivity);
            m_Views.add(view);
        }
    }

    public void CreateAnimations(){
        m_Animations = new LinkedList<>();

        for(int i = 0; i < m_Pathes.size(); i++){

            ObjectAnimator animator = ObjectAnimator.ofFloat
                    (m_Views.get(i), View.X, View.Y, m_Pathes.get(i));
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setRepeatCount(0);
            animator.setDuration(4000);
            animator.start();
        }
    }

    private Path getPathFromStroke(LinkedList<Stroke> i_AnimationPath){
        Path path;

        BoundaryBuilder builder = new BoundaryBuilder();

        for(Stroke stroke : i_AnimationPath){
            builder.addPath(stroke.getPoints(), stroke.getSize(), stroke.getStride(), stroke.getWidth());
        }

        Boundary boundary = builder.getBoundary();
        path = boundary.createPath();

        return path;
    }

    private View getViewToAnimate(LinkedList<Stroke> i_Strokes, Activity i_Context){
        MyView view = new MyView(i_Context);

        BoundaryBuilder builder = new BoundaryBuilder();

        for(Stroke stroke : i_Strokes){
            builder.addPath(stroke.getPoints(), stroke.getSize(), stroke.getStride(), stroke.getWidth());
        }

        Boundary boundary = builder.getBoundary();
        view.setObject(boundary.createPath());
        //view.invalidate();
        //view.CreateCanvas();

        return view;
    }
}
