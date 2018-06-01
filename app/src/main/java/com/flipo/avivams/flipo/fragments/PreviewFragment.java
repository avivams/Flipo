package com.flipo.avivams.flipo.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.flipo.avivams.flipo.R;
import com.flipo.avivams.flipo.utilities.Animation;
import com.flipo.avivams.flipo.utilities.MyPoint;
import com.flipo.avivams.flipo.utilities.MyView;
import com.flipo.avivams.flipo.utilities.Shape;
import com.flipo.avivams.flipo.utilities.Stroke;
import com.wacom.ink.boundary.Boundary;
import com.wacom.ink.boundary.BoundaryBuilder;
import java.nio.FloatBuffer;
import java.util.LinkedList;

public class PreviewFragment extends Fragment {

    private LinkedList<Shape> m_ShapesList;//holds all static shapes
    private LinkedList<Animation> m_AnimationsInfo;//hold all the animation
    private LinkedList<ObjectAnimator> m_Animations;
    private LinkedList<Path> m_Pathes;
    private LinkedList<MyView> m_Views;//hold all view - animated and static
    private AnimatorSet m_AnimationsSet;//the animator
    private BoundaryBuilder m_Builder;
    private LinkedList<FloatBuffer> m_PathsBuffers;


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

        for(View view : m_Views){
            ((ConstraintLayout) myView.findViewById(R.id.layoutPrieview)).addView(view);
        }

        return myView;
    }

    public void SetReady(Activity i_MainActivity){
        Display display = i_MainActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        m_Builder = new BoundaryBuilder();
        CreatePaths();
        CreateViews(i_MainActivity, size);
        CreateAnimations();
    }

    public void CreatePaths(){
        m_Pathes = new LinkedList<>();

        for(Animation animation : m_AnimationsInfo){
            for(Stroke stroke : animation.GetAnimationPath().GetPath()) {
                FloatBuffer floatBuffer = stroke.getPoints();
                floatBuffer.flip();
                Path path = new Path();
                path.moveTo(floatBuffer.get(), floatBuffer.get());
                while (floatBuffer.remaining() > 0) {
                    path.lineTo(floatBuffer.get(), floatBuffer.get());
                }
                //path.close();
                m_Pathes.add(path);
            }
        }
    }

    public void CreateViews(Activity i_MainActivity, Point i_WindowSize){
        m_Views = new LinkedList<>();

        for(Animation animation : m_AnimationsInfo){
            MyView view = getViewToAnimate(animation.GetAnimationObject().getShape(), i_MainActivity, i_WindowSize);
            m_Views.add(view);
        }

        for(Shape shape : m_ShapesList) {
            MyView view = getViewToAnimate(shape.getShape(), i_MainActivity, i_WindowSize);
            m_Views.add(view);
        }

        for (Animation animation : m_AnimationsInfo){
            MyView view = getViewToAnimate(animation.GetAnimationPath().GetPath(), i_MainActivity, i_WindowSize);
            m_Views.add(view);
        }
    }

    public void Start(){
        m_AnimationsSet = new AnimatorSet();

        if(m_Animations.size() == 1){
            m_AnimationsSet.play(m_Animations.get(0));
        }
        else {
            for(int i = 0; i < m_Animations.size() - 1; i++){
                m_AnimationsSet.play(m_Animations.get(i)).before(m_Animations.get(i + 1));
            }
        }

        m_AnimationsSet.start();
    }


    public void CreateAnimations(){
        m_Animations = new LinkedList<>();

        for(int i = 0; i < m_Pathes.size(); i++){
            float x = -(m_Views.get(i).getTopLeft().getX());
            float y = -(m_Views.get(i).getTopLeft().getY());
            //m_Pathes.get(i).offset((-m_Views.get(i).getTopLeft().getX()), -(m_Views.get(i).getTopLeft().getY()));
           m_Pathes.get(i).offset(-(m_Views.get(i).getBottomRight().getX() - (m_Views.get(i).getMyWidth())),
                    -(m_Views.get(i).getBottomRight().getY() - (m_Views.get(i).getMyHeight())));
            ObjectAnimator animator = ObjectAnimator.ofFloat(m_Views.get(i), View.X, View.Y, m_Pathes.get(i));
            animator.setRepeatCount(0);
            animator.setDuration(4000);
            //animator.start();
            m_Animations.add(animator);
        }
    }

    @Deprecated
    private Path getPathFromStroke(LinkedList<Stroke> i_AnimationPath){
        Path path;

        for(Stroke stroke : i_AnimationPath){
            m_Builder.addPath(stroke.getPoints(), stroke.getSize(), stroke.getStride(), stroke.getWidth());
        }

        Boundary boundary = m_Builder.getBoundary();
        path = boundary.createPath();
        path.close();

        return path;
    }

    private MyView getViewToAnimate(LinkedList<Stroke> i_Strokes, Activity i_Context, Point i_WIndowSize){
        MyView view = new MyView(i_Context);
        float xPos = i_WIndowSize.x;
        float yPos = i_WIndowSize.y;
        float maxX = 0;
        float maxY = 0;

        for(Stroke stroke : i_Strokes){
            RectF rect = stroke.getBounds();
            m_Builder.addPath(stroke.getPoints(), stroke.getSize(), stroke.getStride(), stroke.getWidth());

            xPos = min(rect.left, xPos);
            yPos = min(rect.top, yPos);
            maxX = max(rect.right, maxX);
            maxY = max(rect.bottom, maxY);
        }

        view.setWidth((int) (maxX - xPos));
        view.setHeight((int) (maxY - yPos));
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                view.getMyWidth(),
                view.getMyHeight());

        //layoutParams.leftMargin= (int) xPos;
        //layoutParams.topMargin = (int) yPos;

        view.setLayoutParams(layoutParams);

        //view.setPivotX((int) (maxX - xPos)/2);
        //view.setPivotY((int) (maxY - yPos)/2);
        //view.setX((int) (maxX - xPos)/2);
        //view.setY((int) (maxY - yPos)/2);
        view.setX(xPos);
        view.setY(yPos);
        view.setBackground(i_Context.getResources().getDrawable(R.drawable.border));
        Boundary boundary = m_Builder.getBoundary();
        Path path = boundary.createPath();

        path.offset(-xPos,-yPos);
        view.setTopLeft(new MyPoint(xPos, yPos));
        view.setBottomRight(new MyPoint(maxX, maxY));
        view.setObject(path);

        return view;
    }

    private float min(float i_First, float i_Second){
        return i_First < i_Second ? i_First : i_Second;
    }

    private float max(float i_First, float i_Second){
        return i_First > i_Second ? i_First : i_Second;
    }
}
