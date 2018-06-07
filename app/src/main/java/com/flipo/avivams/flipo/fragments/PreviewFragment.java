package com.flipo.avivams.flipo.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Paint;
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
import android.widget.Button;

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
    private Boundary m_Boundary;
    private Button m_PlayStopBtn, m_BackBtn;


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
        //set buttons listeners
        setButtons(myView);
        //add all views to fragment
        for(View view : m_Views){
            ((ConstraintLayout) myView.findViewById(R.id.layoutPrieview)).addView(view);
        }

        return myView;
    }

    public void SetReady(Activity i_MainActivity){
        //get window dimensions
        Display display = i_MainActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        m_Builder = new BoundaryBuilder();
        //create all animations
        createAnimations(i_MainActivity, size);
        //CreatePaths();
        //CreateViews(i_MainActivity, size);
        //CreateAnimations();
    }

    private void setButtons(final View i_View){
        m_PlayStopBtn = i_View.findViewById(R.id.buttonPlayStop);
        m_BackBtn = i_View.findViewById(R.id.buttonBack);

        m_PlayStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playStopBtn_OnClick(v);
            }
        });

        m_BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backBtn_OnClick();
            }
        });

        m_AnimationsSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                m_PlayStopBtn.setText("Play");
            }
        });
    }

    private void playStopBtn_OnClick(View i_View){
        if(m_PlayStopBtn.getText().equals("Stop")){
            m_PlayStopBtn.setText("Play");
            m_AnimationsSet.end();
        }
        else {
            m_PlayStopBtn.setText("Stop");
            m_AnimationsSet.start();
        }
    }

    private void backBtn_OnClick(){
        Fragment f = DrawingFragment.newInstance(null);
        m_BackBtn.setVisibility(View.INVISIBLE);
        m_PlayStopBtn.setVisibility(View.INVISIBLE);
        getFragmentManager().beginTransaction().add(R.id.fragment_container, f).commit();
    }

    private void createAnimations(Activity i_MainActivity, Point i_WindowSize){
        //holds all shapes - animated and statics
        m_Views = new LinkedList<>();
        //holds all paths
        m_Pathes = new LinkedList<>();
        //holds all animators
        m_Animations = new LinkedList<>();

        for(Animation animation : m_AnimationsInfo){
            //create view from object
            MyView view = getViewToAnimate(animation.GetAnimationObject().getShape(), i_MainActivity, i_WindowSize);
            m_Views.add(view);
            //create path from stroke
            Path path = createPath(animation.GetAnimationPath().GetPath().get(0),
                    (view.getTopLeft().getX()),
                    (view.getTopLeft().getY()));
            m_Pathes.add(path);
            //create animator according to path and view
            ObjectAnimator animator = createAnimation(view, path);
            m_Animations.add(animator);
        }
        //add all static shapes
        for(Shape shape : m_ShapesList) {
            MyView view = getViewToAnimate(shape.getShape(), i_MainActivity, i_WindowSize);
            m_Views.add(view);
        }

        /*for (Animation animation : m_AnimationsInfo){
            MyView view = getViewToAnimate(animation.GetAnimationPath().GetPath(), i_MainActivity, i_WindowSize);
            m_Views.add(view);
        }*/
    }

    public void Start(){
        //define animator set - order
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

    private MyView getViewToAnimate(LinkedList<Stroke> i_Strokes, Activity i_Context, Point i_WIndowSize){
        MyView view = new MyView(i_Context);
        float xPos = i_WIndowSize.x;
        float yPos = i_WIndowSize.y;
        float maxX = 0;
        float maxY = 0;

        for(Stroke stroke : i_Strokes){
            RectF rect = stroke.getBounds();
            //convert stroke to android path
            m_Builder.addPath(stroke.getPoints(), stroke.getSize(), stroke.getStride(), stroke.getWidth());
            //find topLeft and bottomRight points of view
            xPos = min(rect.left, xPos);
            yPos = min(rect.top, yPos);
            maxX = max(rect.right, maxX);
            maxY = max(rect.bottom, maxY);
        }
        //set view width and height - the default is the all screen
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

        //view.setX(xPos);
        //view.setY(yPos);
        //view.setBackground(i_Context.getResources().getDrawable(R.drawable.border));
        m_Boundary = m_Builder.getBoundary();
        Path shape = m_Boundary.createPath();
        //set shape offset - default is (0,0)
        shape.offset(-xPos,-yPos);
        view.setTopLeft(new MyPoint(xPos, yPos));
        view.setBottomRight(new MyPoint(maxX, maxY));
        view.setObject(shape);

        return view;
    }

    private Path createPath(Stroke i_PathStroke, float i_X, float i_Y){
        FloatBuffer floatBuffer = i_PathStroke.getPoints();
        floatBuffer.flip();
        Path path = new Path();
        //set path to start from the middle of the shape
        path.moveTo(i_X, i_Y);
        while (floatBuffer.remaining() > 0) {
            path.lineTo(floatBuffer.get(), floatBuffer.get());
        }
        
        return path;
    }

    private ObjectAnimator createAnimation(View i_Shape, Path i_Path){
        ObjectAnimator animator = ObjectAnimator.ofFloat(i_Shape, View.X, View.Y, i_Path);
        animator.setRepeatCount(0);
        animator.setDuration(4000);

        return animator;
    }

    private float min(float i_First, float i_Second){
        return i_First < i_Second ? i_First : i_Second;
    }

    private float max(float i_First, float i_Second){
        return i_First > i_Second ? i_First : i_Second;
    }
    @Deprecated
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
    @Deprecated
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
    @Deprecated
    public void CreateAnimations(){
        m_Animations = new LinkedList<>();

        for(int i = 0; i < m_Pathes.size(); i++){
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
}
