package com.flipo.avivams.flipo.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.flipo.avivams.flipo.R;
import com.flipo.avivams.flipo.dialogs.DialogMatcher;
import com.flipo.avivams.flipo.ui.PaletteAdapter;
import com.flipo.avivams.flipo.utilities.Animation;
import com.flipo.avivams.flipo.utilities.AnimationPath;
import com.flipo.avivams.flipo.utilities.MyView;
import com.flipo.avivams.flipo.utilities.Shape;
import com.flipo.avivams.flipo.utilities.Stroke;
import com.wacom.ink.boundary.Boundary;
import com.wacom.ink.boundary.BoundaryBuilder;
import com.wacom.ink.manipulation.Intersector;
import com.wacom.ink.path.PathUtils;
import com.wacom.ink.path.SpeedPathBuilder;
import com.wacom.ink.rasterization.BlendMode;
import com.wacom.ink.rasterization.InkCanvas;
import com.wacom.ink.rasterization.Layer;
import com.wacom.ink.rasterization.StrokePaint;
import com.wacom.ink.rasterization.StrokeRenderer;
import com.wacom.ink.smooth.MultiChannelSmoothener;

import java.nio.FloatBuffer;
import java.util.LinkedList;


public class DrawingFragment extends Fragment implements DialogMatcher.ResultYesNoListener{
    private enum detectMarker{SHAPES_ONLY, PATHS_ONLY, ANY};

    private Button m_btnDraw, m_btnPath, m_btnParams, m_btnStyle, m_btnToolsCls, m_btnPreview;
    private View m_topLeftBarView;
    private ImageButton m_btnCompletedDraw, m_btnToolsOpn;

    private SpeedPathBuilder m_PathBuilder;
    private SurfaceView m_SurfaceView;
    private StrokePaint m_Paint;
    private MultiChannelSmoothener m_Smoothener;
    private GestureDetector gestureDetector;

    private OnDrawingInteractionListener mListener;

    private LinkedList<Stroke> m_builtStrokes;
    private LinkedList<Shape> m_shapes;
    private LinkedList<Animation> m_animations;

    private Shape m_selectedShape;
    private Animation m_selectedAnimShape, m_selectedAnimPath; // to distinguish what exactly the user clicked on as part of an animation
    private int m_ColorCanvas;
    private boolean isDrawingNow;

// TODO 1 IMPORTANT : when user wants to delete - if a user chose an shape then delete the whole animation with the path. if the user chose a path, then delete the animation and send the shape to the shape list


    public DrawingFragment() {
        // Required empty public constructor
    }


    public static DrawingFragment newInstance(StrokePaint paint) {
        DrawingFragment fragment = new DrawingFragment();
        fragment.m_Paint = paint;
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_builtStrokes = new LinkedList<>();
        m_shapes = new LinkedList<>();
        m_animations = new LinkedList<>();
        gestureDetector = new GestureDetector(getActivity(), new SingleTapConfirm());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_drawing, container, false);
        initButtonsListeners(v);

        m_PathBuilder = new SpeedPathBuilder();
        m_Smoothener = new MultiChannelSmoothener(m_PathBuilder.getStride());
        m_ColorCanvas = getResources().getColor(R.color.canvasBackground);

        GridView gridview = v.findViewById(R.id.menu_style_palette);
        gridview.setAdapter(new PaletteAdapter(getActivity()));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
               m_Paint.setColor(((ColorDrawable)v.getBackground()).getColor());
               mListener.getRenderer().setStrokePaint(m_Paint);
            }
        });

        //  bb = new BoundaryBuilder();
        return v;
    }


    @Override
    public void onAttach(Context context) throws RuntimeException{
        super.onAttach(context);
        forOnAttacheMethods(context);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        forOnAttacheMethods(activity);
    }

    /**
     * this is for the onAttach methods only, because of different api onAttach method.
     * @param context
     */
    private void forOnAttacheMethods(Context context){
        if (context instanceof OnDrawingInteractionListener) {
            mListener = (OnDrawingInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }


        m_SurfaceView = getActivity().findViewById(R.id.surfaceView);
        if(m_SurfaceView != null) {
            //noinspection AndroidLintClickableViewAccessibility
            m_SurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(m_topLeftBarView.getVisibility() == View.VISIBLE)
                        m_btnToolsCls.callOnClick();

                    if(m_btnDraw.isSelected()) {
                        drawingMode(event);
                        return true;
                    }

                    if(m_btnPath.isSelected()){

                        //if no shape was selected then we need to check a selection
                        if(m_selectedShape == null){
                            handleSelectShape(event, detectMarker.SHAPES_ONLY);
                        }
                        else { // the user selected a shape, and now user is selecting/drawing a path

                            if(m_builtStrokes.isEmpty() &&  gestureDetector.onTouchEvent(event)) {
                                handleSelectShape(event, detectMarker.PATHS_ONLY); //the user may selected a path
                            }
                            else
                                drawingMode(event); // the user is drawing
                        }
                        return true;
                    }

                    return false;
                }
            });

        }else throw new RuntimeException("cannot find SurfaceView from" +
                this.getClass().getName() + "onAttach method");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * initialize all buttons in fragment and their behavior
     * @param fView the fragment view
     */
    private void initButtonsListeners(final View fView){

        m_btnDraw = fView.findViewById(R.id.btn_draw);
        m_btnPath = fView.findViewById(R.id.btn_path);
        m_btnParams = fView.findViewById(R.id.btn_params);
        m_btnStyle = fView.findViewById(R.id.btn_style);
        m_btnCompletedDraw = fView.findViewById(R.id.btn_draw_complete);
        m_btnToolsCls = fView.findViewById(R.id.btn_cls_tools);
        m_btnToolsOpn = fView.findViewById(R.id.btn_opn_tools);
        m_btnPreview = fView.findViewById(R.id.btn_preview);
        m_topLeftBarView = fView.findViewById(R.id.menu_topleft_bar_view);

        final View styleView =  fView.findViewById(R.id.menu_topleft_bar_style_tab);

        // set color for icons when api is less than 23
        Activity activity = getActivity();
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            m_btnPath.getCompoundDrawablesRelative()[0].setTint(activity.getResources().getColor(R.color.menu_btn_draw_path));
            m_btnParams.getCompoundDrawablesRelative()[0].setTint(activity.getResources().getColor(R.color.menu_btn_params));
        }

        m_btnToolsOpn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_btnToolsCls.setText(getString(R.string.menu_tools_title));
                animMenuAction(false);
            }
        });
        m_btnToolsCls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animMenuAction(true);
                styleView.setVisibility(View.GONE);
            }
        });

        activity = null;

        // CompleteDrawing button
        m_btnCompletedDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if an object is drawn.
                if(m_builtStrokes.isEmpty()){
                    if(m_btnDraw.isSelected()) //if needs to draw a shape
                        DialogMatcher.showDialog(getActivity(), DialogMatcher.DialogType.DRAW_SHAPE_FIRST, getFragmentManager().beginTransaction(), null);
                    else // needs to draw a path
                        DialogMatcher.showDialog(getActivity(), DialogMatcher.DialogType.DRAW_PATH_FIRST, getFragmentManager().beginTransaction(), null);
                    return;
                }

                if(m_btnDraw.isSelected()) {
                    completeDrawObject();
                    Toast.makeText(getActivity(), "Shape saved", Toast.LENGTH_LONG).show();
                }
                else {//the animation path is pressed and the user ended drawing a path
                    completeDrawPath(null);
                }

                disableButtonsExcept(m_btnCompletedDraw);
            }
        });

        // Draw button
        m_btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                disableButtonsExcept(m_btnDraw);
                m_btnCompletedDraw.setVisibility(View.VISIBLE);
            }
        });
        //TODO 4: complete the Params button
        m_btnParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtonsExcept(m_btnParams);

            }
        });

        //TODO 5: complete the Style button
        m_btnStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //disableButtonsExcept(m_btnStyle);
               // disableButtonsExcept(null);
                m_btnToolsCls.setText(getString(R.string.menu_style_title));
                styleView.setVisibility(View.VISIBLE);

            }
        });

        //Animation path button
        m_btnPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtonsExcept(m_btnPath);

                //if no shape was drawn, then show a dialog and turn 'draw button' on
                if(m_builtStrokes.isEmpty() && m_shapes.isEmpty() && m_animations.isEmpty()) {
                    DialogMatcher.showDialog(getActivity(), DialogMatcher.DialogType.DRAW_SHAPE_FIRST, getFragmentManager().beginTransaction(), null);
                    m_btnDraw.callOnClick();
                }
                else
                    DialogMatcher.showDialog(getActivity(), DialogMatcher.DialogType.CHOOSE_SHAPE, getFragmentManager().beginTransaction(), null);
            }
        });

        m_btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.startPreviewFragment(m_shapes, m_animations);
            }
        });

        //set the draw button as pressed by default
        m_btnDraw.setSelected(true);
        m_btnCompletedDraw.setVisibility(View.VISIBLE);
    }


    /**
     * animation action for the top left menu bar
     * @param close is closing animation
     */
    private void animMenuAction(boolean close){
        if(close){
            m_topLeftBarView.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.top_left_menu_close));
            m_btnToolsOpn.setVisibility(View.VISIBLE);
            m_topLeftBarView.setVisibility(View.INVISIBLE);
        }
        else {
            m_btnToolsOpn.setVisibility(View.INVISIBLE);
            m_topLeftBarView.setVisibility(View.VISIBLE);
            m_topLeftBarView.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.top_left_menu_open));
        }
    }


    /**
     * This function is drawing the strokes on SurfaceView with touch events.
     * Call this function when the user is drawing.
     * @param event The MotionEvent instance from the onTouch.
     */
    private void drawingMode(MotionEvent event){
        boolean isFinished = buildPath(event);
        isDrawingNow = !isFinished;

        drawStroke(event);
        mListener.renderView();
        //saves the new add stroke to the list
        if (isFinished){
            saveCurrStroke();
        }
    }


    /**
     * Disables all buttons on the fragment except this one.
     * @param btn The button to skip disabling.
     */
    private void disableButtonsExcept(View btn){
        int id = (btn == null? View.NO_ID : btn.getId());

        m_btnStyle.setSelected(m_btnStyle.getId() == id);
        m_btnParams.setSelected(m_btnParams.getId() == id);
        m_btnPath.setSelected(m_btnPath.getId() == id);
        m_btnDraw.setSelected(m_btnDraw.getId() == id);
        m_btnCompletedDraw.setVisibility(View.GONE);

        // if a stroke is being build we need to finish it and dismiss by rendering only the remaining
        stopBuildStroke();
        m_selectedShape = null;

        if(m_topLeftBarView.getVisibility() == View.VISIBLE && !(m_btnParams.isSelected() || m_btnStyle.isSelected()))
            m_btnToolsCls.callOnClick();
    }


    /**
     * Call this to clean and rendering the view when user stopped drawing new strokes.
     */
    private void stopBuildStroke(){
        if(!(m_builtStrokes.isEmpty())) {
            m_builtStrokes.clear();
            mListener.drawShapes(m_shapes, m_animations);
            mListener.renderView();
        }
    }


    public void drawStroke(MotionEvent event){
        InkCanvas canvas = mListener.getCanvas();
        Layer currentFrameLayer = mListener.getCurrentView(), strokesLayer = mListener.getStrokesLayer();
        StrokeRenderer strokeRenderer = mListener.getRenderer();
        int stride = m_PathBuilder.getStride();

        strokeRenderer.drawPoints(m_PathBuilder.getPathBuffer(), m_PathBuilder.getPathLastUpdatePosition(),
                m_PathBuilder.getAddedPointsSize(), stride, event.getAction()==MotionEvent.ACTION_UP);
        strokeRenderer.drawPrelimPoints(m_PathBuilder.getPreliminaryPathBuffer(), 0,
                m_PathBuilder.getFinishedPreliminaryPathSize(), stride);

        if (event.getAction()!=MotionEvent.ACTION_UP){
            canvas.setTarget(currentFrameLayer, strokeRenderer.getStrokeUpdatedArea());
            canvas.clearColor(m_ColorCanvas);
            canvas.drawLayer(strokesLayer, BlendMode.BLENDMODE_NORMAL);
            strokeRenderer.blendStrokeUpdatedArea(currentFrameLayer, BlendMode.BLENDMODE_NORMAL);
        } else {
            strokeRenderer.blendStroke(strokesLayer, BlendMode.BLENDMODE_NORMAL);
            canvas.setTarget(currentFrameLayer);
            canvas.clearColor(m_ColorCanvas);
            canvas.drawLayer(strokesLayer, BlendMode.BLENDMODE_NORMAL);
        }
    }


    public boolean buildPath(MotionEvent event){
        if (event.getAction()!=MotionEvent.ACTION_DOWN
                && event.getAction()!=MotionEvent.ACTION_MOVE
                && event.getAction()!=MotionEvent.ACTION_UP){
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Reset the smoothener instance when starting to generate a new path.
            m_Smoothener.reset();
        }

        PathUtils.Phase phase = PathUtils.getPhaseFromMotionEvent(event);
        // Add the current input point to the path builder
        FloatBuffer part = m_PathBuilder.addPoint(phase, event.getX(), event.getY(), event.getEventTime());
        MultiChannelSmoothener.SmoothingResult smoothingResult;

        int partSize = m_PathBuilder.getPathPartSize();

        if (partSize>0){
            // Smooth the returned control points (aka partial path).
            smoothingResult = m_Smoothener.smooth(part, partSize, (phase == PathUtils.Phase.END));
            // Add the smoothed control points to the path builder.
            m_PathBuilder.addPathPart(smoothingResult.getSmoothedPoints(), smoothingResult.getSize());
        }

        // Create a preliminary path.
        FloatBuffer preliminaryPath = m_PathBuilder.createPreliminaryPath();
        // Smoothen the preliminary path's control points (return inform of a partial path).
        smoothingResult = m_Smoothener.smooth(preliminaryPath, m_PathBuilder.getPreliminaryPathSize(), true);
        // Add the smoothed preliminary path to the path builder.
        m_PathBuilder.finishPreliminaryPath(smoothingResult.getSmoothedPoints(), smoothingResult.getSize());

        return (event.getAction()==MotionEvent.ACTION_UP && m_PathBuilder.hasFinished());
    }

    /**
     * this function handles the selection of a shape.
     * @param event the current event from onTouch.
     * @param type the type of object we are detecting.
     */
    private void handleSelectShape(MotionEvent event, detectMarker type){

        boolean bFinished = buildPath(event); // only if buildPath is finished

        if(bFinished) {

            float w = m_Paint.getWidth();
            m_Paint.setWidth(2.0f); //this is used by the intersector
            checkSelection(type);
            m_Paint.setWidth(w);

            //the user selected a shape or an animation path
            if (m_btnPath.isSelected()) {
                if (m_selectedShape != null && type == detectMarker.SHAPES_ONLY) {

                    paintThese(m_selectedShape, null, false); //highlight it
                    DialogMatcher.showDialog(getActivity(), DialogMatcher.DialogType.DRAW_PATH, getFragmentManager().beginTransaction(), null);
                    m_btnCompletedDraw.setVisibility(View.VISIBLE);

                }
                else if (m_selectedShape == null && type == detectMarker.SHAPES_ONLY){
                    DialogMatcher.showDialog(getActivity(), DialogMatcher.DialogType.CHOOSE_FREE_SHAPE,
                            getFragmentManager().beginTransaction(), null);
                }
                else if (m_selectedAnimPath != null) { //the user chose a path which already located in an Animation
                    DialogMatcher.showDialog(getActivity(), DialogMatcher.DialogType.CHOSE_EXIST_PATH, getFragmentManager().beginTransaction(), this);
                }
            }
        }

    }


    /**
     * call this when the user finished drawing his object to save it into the list
     */
    private void completeDrawObject(){
        m_shapes.add(new Shape((LinkedList<Stroke>)m_builtStrokes.clone()));
        m_builtStrokes.clear();
    }

    /**
     * saves the newly added stroke/object to the list
     */
    private void saveCurrStroke() {
        Stroke stroke = new Stroke();
        stroke.CopyPoints(m_PathBuilder.getPathBuffer(), 0, m_PathBuilder.getPathSize());
        stroke.setStride(m_PathBuilder.getStride());
        stroke.SetWidth(m_Paint.getWidth());
        stroke.SetColor(m_Paint.getColor());
        stroke.SetInterval(0.0f, 1.0f);
        stroke.SetBlendMode(BlendMode.BLENDMODE_NORMAL);
        stroke.calculateBounds();
        m_builtStrokes.add(stroke);
    }

    /**
     * this function is for detecting a touch event on a shape or path.
     * @param restriction if you want to restrict detection to a type of stroke
     */
    private void checkSelection(detectMarker restriction){

        Intersector<Stroke> intersector = mListener.getIntersector();


        //I CHANGED THE INTERSECTOR CODE HERE, see the tutorial for selecting the whole stroke
        intersector.setTargetAsStroke(m_PathBuilder.getPathBuffer(), m_PathBuilder.getPathLastUpdatePosition(),
                m_PathBuilder.getAddedPointsSize(), m_PathBuilder.getStride(), m_Paint.getWidth());

        // TODO 2: should we add a UI THREAD for search ?
        if(restriction == detectMarker.SHAPES_ONLY) {
            for (Shape shape : m_shapes) {
                for (Stroke stroke : shape.getShape()) {
                    if (intersector.isIntersectingTarget(stroke)) {
                        m_selectedShape = shape;
                        return;
                    }
                }
            }
        }
        else if(restriction == detectMarker.PATHS_ONLY){
            for (Animation anim : m_animations) {
                for (Stroke stroke : anim.GetAnimationPath().GetPath()) {
                    if (intersector.isIntersectingTarget(stroke)) {
                        m_selectedAnimPath = anim;
                        return;
                    }
                }
            }
        }
        else if(restriction == detectMarker.ANY){

            for (Animation anim : m_animations) {
                for (Stroke stroke : anim.GetAnimationObject().getShape()) {
                    if (intersector.isIntersectingTarget(stroke)) {
                        m_selectedAnimShape = anim;
                        return;
                    }
                }
                for (Stroke stroke : anim.GetAnimationPath().GetPath()) {
                    if (intersector.isIntersectingTarget(stroke)) {
                        m_selectedAnimPath = anim;
                        return;
                    }
                }
            }
        }
    }

    /**
     * call this when the user finished drawing the animation path
     * @param path if the user chose an existing path - pass it here.
     */
    private void completeDrawPath(AnimationPath path){

        if (path == null){
            path = new AnimationPath((LinkedList<Stroke>)m_builtStrokes.clone());
            m_builtStrokes.clear();
        }


        Animation anim = new Animation();
        anim.SetAnimationPath(path)
                .SetAnimationObject(m_selectedShape)
                .SetSpeed(5);
        //TODO 3: add the correct speed here when adding the speed parameter in UI
        m_animations.add(anim);

        m_shapes.remove(m_selectedShape);
        m_selectedShape = null;
        m_selectedAnimPath = m_selectedAnimShape = null;

        m_btnCompletedDraw.setVisibility(View.GONE);

        paintThese(anim.GetAnimationObject(), anim.GetAnimationPath(), true);

        //paintThese(anim.GetAnimationObject(), anim.GetAnimationPath(), true);

        m_btnPath.setSelected(false);
        Toast.makeText(getActivity(), "Animation saved", Toast.LENGTH_LONG).show();
    }

    /**
     * this function highlights the parameters on canvas.
     * it is also calls to renderView().
     * @param shape the shape to highlight
     * @param path the path to highlight
     * @param oldColor if using the old color
     */
    private void paintThese(Shape shape, AnimationPath path, boolean oldColor){
        int color;

        if(shape != null) {
            for (Stroke stroke : shape.getShape()){
                color = oldColor ? stroke.getFormerColor() : stroke.GetColor() >> 1;
                if(!oldColor)
                    stroke.setFormerColor(stroke.GetColor());

                stroke.SetColor(color);
            }
        }
        if(path != null && !(m_btnPath.isSelected())) { // no need to highlight paths when selecting/drawing paths
            for (Stroke stroke : path.GetPath()){
                color = oldColor ? stroke.getFormerColor() : stroke.GetColor() >> 1;
                if(!oldColor)
                    stroke.setFormerColor(stroke.GetColor());

                stroke.SetColor(color);
            }
        }

        mListener.drawShapes(m_shapes, m_animations);
        mListener.renderView();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity.
     */
    public interface OnDrawingInteractionListener {

        void renderView();
        InkCanvas getCanvas();
        Layer getCurrentView();
        Layer getStrokesLayer();
        StrokeRenderer getRenderer();
        Intersector<Stroke> getIntersector();
        void setNewPaint(StrokePaint newPaint);
        void drawShapes(LinkedList<Shape> shapesList, LinkedList<Animation> anims);
        void startPreviewFragment(LinkedList<Shape> shapesList, LinkedList<Animation> anims);
    }

    /**
     * this class is for detecting 1 tap on the SurfaceView
     */
    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }


    /**
     * this is a callback from dialog
     */
    @Override
    public void resultOk() {
        if(m_btnPath.isSelected() && m_selectedAnimPath != null) { //the user chose an existing path and decided to merge
            completeDrawPath(m_selectedAnimPath.GetAnimationPath()); // create animation with the selected path
        }
    }
    /**
     * this is a callback from dialog
     */
    @Override
    public void resultCancel() {
        if(m_btnPath.isSelected() && m_selectedAnimPath != null) { //the user chose an existing path and decided to cancel
            m_selectedAnimPath = null;
        }
    }

    /*create a bitmap from a path and a view
    public static Bitmap loadBitmapFromView(View v) {

            bb.addPath(m_PathBuilder.getPathBuffer(), m_PathBuilder.getPathSize(), m_PathBuilder.getStride(),
                    m_Paint.getWidth());
            Boundary boundary = bb.getBoundary();
            path = boundary.createPath();
            strokeView s = getView().findViewById(R.id.stroke_show);
            s.setPath(path);
            s.invalidate();/*


        Bitmap b = Bitmap.createBitmap( v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }
    private Path path;
     */


}
