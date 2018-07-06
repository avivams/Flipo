package com.flipo.avivams.flipo.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flipo.avivams.flipo.R;
import com.flipo.avivams.flipo.dialogs.DialogMatcher;
import com.flipo.avivams.flipo.ui.MenuManager;
import com.flipo.avivams.flipo.utilities.Animation;
import com.flipo.avivams.flipo.utilities.AnimationPath;
import com.flipo.avivams.flipo.utilities.Shape;
import com.flipo.avivams.flipo.utilities.Stroke;
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


public class DrawingFragment extends Fragment implements DialogMatcher.ResultYesNoListener, MenuManager.MenuManagerListener{
    private enum detectMarker{SHAPES_ONLY, PATHS_ONLY, ANY};

    /*private Button m_btnDraw, m_btnPath, m_btnParams, m_btnStyle, m_btnToolsCls, m_btnPreview,
            m_btnErase;*/
    private LinearLayout m_btnDraw;
    private ImageButton m_btnOpnDraw, m_btnPath, m_btnPreview,
            m_btnErase, m_btnCompletedDraw, m_btnSelect;
    private Button m_btnTask;

    private MenuManager menuManager;

    private SpeedPathBuilder m_PathBuilder;
    private SurfaceView m_SurfaceView;
    private StrokePaint m_Paint;
    private MultiChannelSmoothener m_Smoothener;
    private GestureDetector gestureDetector;

    private OnDrawingInteractionListener mListener;

    private LinkedList<Stroke> m_builtStrokes;
    private LinkedList<Shape> m_shapes;
    private LinkedList<Animation> m_animations;

    private Stroke m_selectedStroke; //stroke which in drawing mode
    private Shape m_selectedShape;
    private Animation m_selectedAnimShape, m_selectedAnimPath; // to distinguish what exactly the user clicked on as part of an animation
    private int m_ColorCanvas;
    private int m_pathSpeed;
    private boolean isDrawingNow;


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


        menuManager = new MenuManager(getActivity());
        initButtonsListeners(v);

        menuManager.registerButtons(mListener, this,
                (LinearLayout)v.findViewById(R.id.draw_pallete_view),
                m_btnOpnDraw,
                getResources().getIntArray(R.array.brush_sizes).length, getResources().getIntArray(R.array.palette_colors).length,
                (ImageView)v.findViewById(R.id.brush_size_tiny),  (ImageView)v.findViewById(R.id.brush_size_small), (ImageView)v.findViewById(R.id.brush_size_normal),
                (ImageView)v.findViewById(R.id.brush_size_big), (ImageView)v.findViewById(R.id.brush_size_giant),
                (ImageView)v.findViewById(R.id.brush_color_y), (ImageView)v.findViewById(R.id.brush_color_r), (ImageView)v.findViewById(R.id.brush_color_b), (ImageView)v.findViewById(R.id.brush_color_g),
                (ImageView)v.findViewById(R.id.brush_color_blk), (ImageView)v.findViewById(R.id.brush_color_w));
        /*
        menuManager.registerButtons(m_btnMenuOpn, m_btnStyle, m_btnParams, m_btnPath, m_btnDraw);
        menuManager.registerButtonsText((TextView)v.findViewById(R.id.menu_btn_style_txt), (TextView)v.findViewById(R.id.menu_btn_params_txt),
                (TextView)v.findViewById(R.id.menu_btn_path_txt), (TextView)v.findViewById(R.id.menu_btn_shape_txt));
        menuManager.registerTab(m_menuTabView);
*/
        m_PathBuilder = new SpeedPathBuilder();
        m_Smoothener = new MultiChannelSmoothener(m_PathBuilder.getStride());
        m_ColorCanvas = getResources().getColor(R.color.canvasBackground);

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

        m_pathSpeed = context.getResources().getInteger(R.integer.min_params_speed);

        m_SurfaceView = getActivity().findViewById(R.id.surfaceView);
        if(m_SurfaceView != null) {
            //noinspection AndroidLintClickableViewAccessibility
            m_SurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(menuManager.isMenuVisible())
                        toggleDrawMenu();

                    if(m_btnOpnDraw.isSelected()) {
                        drawingMode(event);
                        return true;
                    }

                    if(m_btnSelect.isSelected()){
                        buttonSelectHandler(event);
                        return true;
                    }

                    if(m_btnPath.isSelected()){
                        if(m_builtStrokes.isEmpty() &&  gestureDetector.onTouchEvent(event)) {
                            handleSelectShape(event, detectMarker.PATHS_ONLY); //the user may have selected a path
                        }
                        else
                            drawingMode(event); // the user is drawing a path
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

        m_btnOpnDraw = fView.findViewById(R.id.btn_opnDraw);
        m_btnPath = fView.findViewById(R.id.btn_path);
        m_btnSelect = fView.findViewById(R.id.btn_select);
        m_btnCompletedDraw = fView.findViewById(R.id.btn_draw_complete);

        m_btnPreview = fView.findViewById(R.id.btn_preview);
        m_btnPreview.setEnabled(true);
        m_btnErase = fView.findViewById(R.id.btn_erase);
        m_btnErase.setEnabled(false);

        m_btnTask = fView.findViewById(R.id.btn_task);

        // set color for icons when api is less than 23
        Activity activity = getActivity();


        // CompleteDrawing button
        m_btnCompletedDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if an object is drawn.
                if(m_builtStrokes.isEmpty()){
                    if(m_btnOpnDraw.isSelected()) //if needs to draw a shape
                        DialogMatcher.showDialog(getActivity(), DialogMatcher.DoodlesDialogType.DRAW_SHAPE_FIRST, getFragmentManager().beginTransaction(), null);
                    else // needs to draw a path
                        DialogMatcher.showDialog(getActivity(), DialogMatcher.DoodlesDialogType.DRAW_PATH_FIRST, getFragmentManager().beginTransaction(), null);
                    return;
                }


                if(m_btnOpnDraw.isSelected()) {
                    completeDrawObject();
                    Toast.makeText(getActivity(), "Shape saved", Toast.LENGTH_LONG).show();
                }
                else {//the animation path is pressed and the user ended drawing a path
                    completeDrawPath(null);
                }

                disableButtonsExcept(m_btnCompletedDraw);
            }
        });

        m_btnOpnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                disableButtonsExcept(m_btnOpnDraw);
                menuManager.animateMenu(!menuManager.isMenuVisible());

                m_btnCompletedDraw.setVisibility(View.VISIBLE);
            }
        });

        //Select button
        m_btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableButtonsExcept(m_btnSelect);

                //if no shape was drawn, then show a dialog and turn 'draw button' on
                if(m_builtStrokes.isEmpty() && m_shapes.isEmpty() && m_animations.isEmpty()) {
                    DialogMatcher.showDialog(getActivity(), DialogMatcher.DoodlesDialogType.DRAW_SHAPE_FIRST, getFragmentManager().beginTransaction(), null);
                    m_btnOpnDraw.callOnClick();
                }
                else
                    DialogMatcher.showDialog(getActivity(), DialogMatcher.DoodlesDialogType.CHOOSE_SHAPE, getFragmentManager().beginTransaction(), null);
            }
        });



        //Animation path button
        m_btnPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if no shape was drawn, then show a dialog and turn 'draw button' on
                if(m_builtStrokes.isEmpty() && m_shapes.isEmpty() && m_animations.isEmpty()) {
                    DialogMatcher.showDialog(getActivity(), DialogMatcher.DoodlesDialogType.DRAW_SHAPE_FIRST, getFragmentManager().beginTransaction(), null);
                    m_btnOpnDraw.callOnClick();
                    return;
                }
                if(m_selectedAnimPath != null || m_selectedAnimShape != null) { // the user selected an animated path or shape
                    DialogMatcher.showDialog(getActivity(), DialogMatcher.DoodlesDialogType.CHOOSE_FREE_SHAPE, getFragmentManager().beginTransaction(), null);
                    disableButtonsExcept(m_btnSelect);
                    return;
                }
                if(m_selectedShape == null){ // nothing was selected
                    m_btnSelect.callOnClick();
                    return;
                }

                disableButtonsExcept(m_btnPath);
                DialogMatcher.showDialog(getActivity(), DialogMatcher.DoodlesDialogType.DRAW_PATH, getFragmentManager().beginTransaction(), null);
                m_btnCompletedDraw.setVisibility(View.VISIBLE);
            }
        });

        //TODO change the erase button to trash
        m_btnErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_btnErase.setSelected(true);

                toggleDrawMenu();
                DialogMatcher.showDialog(getActivity(), DialogMatcher.DoodlesDialogType.DELETE_CHOSED, getFragmentManager().beginTransaction(), DrawingFragment.this);
            }
        });



        m_btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtonsExcept(m_btnPreview);
                mListener.startPreviewFragment(m_shapes, m_animations);

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(getViewToAnimate(), View.X, View.Y, getPathFromStroke(m_animations.getLast().GetAnimationPath().GetPath()));
                    animator.setDuration(3000);
                    animator.start();
                }*/
            }
        });

        
        m_btnTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogMatcher.showDialog(getActivity(), DialogMatcher.DoodlesDialogType.ASSIGNMENT_OBJECTIVE, getFragmentManager().beginTransaction(), null);
               /* AssignmentDialog.makeInstance(getString(R.string.assignment_title), getString(R.string.assignment_description),
                        getString(R.string.btn_thanks_gotit), fView.findViewById(R.id.layout_container), getActivity());*/
            }
        });


        activity = null;
        //set the draw button as pressed by default
        m_btnOpnDraw.setSelected(true);
        m_btnCompletedDraw.setVisibility(View.VISIBLE);
    }


    /**
     * close the Draw's palette, if it is open.
     */
    private void toggleDrawMenu(){
        if(menuManager.isMenuVisible()) {
            menuManager.animateMenu(!menuManager.isMenuVisible());
        }
    }


    private void buttonSelectHandler(MotionEvent event){
        //if no shape was selected then we need to check a selection
        if(m_selectedShape == null && m_selectedAnimPath == null && m_selectedAnimShape == null){
            handleSelectShape(event, detectMarker.ANY);
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

        m_btnPath.setSelected(m_btnPath.getId() == id);
        m_btnOpnDraw.setSelected(m_btnOpnDraw.getId() == id);
        m_btnSelect.setSelected(m_btnSelect.getId() == id);
        m_btnCompletedDraw.setVisibility(View.INVISIBLE);
        m_btnErase.setEnabled(false);

        if(!m_btnOpnDraw.isSelected() || m_selectedShape != null)
            // if a stroke is being build we need to finish it and dismiss by rendering only the remaining
            // or if user pressed Draw after drawing a path
            stopBuildStroke();


        if(m_btnPath.isSelected())
            cancelSelected(m_selectedShape);
        else
            cancelSelected();

        if(menuManager.isMenuVisible() && !m_btnOpnDraw.isSelected())
            toggleDrawMenu();

    }


    /**
     * Call this to clean and rendering the view when user stopped drawing new strokes.
     */
    private void stopBuildStroke(){
        if(!(m_builtStrokes.isEmpty())) {
            m_builtStrokes.clear();
            mListener.drawShapes(m_shapes, m_animations, m_builtStrokes);
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

           if(m_btnSelect.isSelected()){
                if(m_selectedShape != null) {
                    paintThese(m_selectedShape, null, false); //highlight it
                    m_btnErase.setEnabled(true);
                }
                else if(m_selectedAnimShape != null) {
                    paintThese(m_selectedAnimShape.GetAnimationObject(), m_selectedAnimShape.GetAnimationPath(), false); //highlight it
                    m_btnErase.setEnabled(true);
                }
                else if(m_selectedAnimPath != null) {
                    paintThese(m_selectedAnimPath.GetAnimationObject(), m_selectedAnimPath.GetAnimationPath(), false); //highlight it
                    m_btnErase.setEnabled(true);
                }
            }

            //the user selected an animation path to merge
            else if (m_btnPath.isSelected() && m_selectedAnimPath != null) { //the user chose a path which already located in an Animation
                DialogMatcher.showDialog(getActivity(), DialogMatcher.DoodlesDialogType.CHOSE_EXIST_PATH, getFragmentManager().beginTransaction(), this);
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

            for(Stroke stroke : m_builtStrokes){
                if (intersector.isIntersectingTarget(stroke)) {
                    m_selectedStroke = stroke;
                    return;
                }
            }

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

            for(Shape shape : m_shapes) {
                for (Stroke stroke : shape.getShape()) {
                    if (intersector.isIntersectingTarget(stroke)) {
                        m_selectedShape = shape;
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
                .SetSpeed(m_pathSpeed);

        m_animations.add(anim);

        m_shapes.remove(m_selectedShape);
        m_selectedShape = null;
        m_selectedAnimPath = m_selectedAnimShape = null;

        m_btnCompletedDraw.setVisibility(View.INVISIBLE);

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

        mListener.drawShapes(m_shapes, m_animations, m_builtStrokes);
        mListener.renderView();
    }


    private void cancelSelected(){
        cancelSelected(null);
    }

    /**
     * used by the Path button.
     * @param except don't cancel this shape
     */
    private void cancelSelected(Shape except){

        if(m_selectedShape != null && m_selectedShape != except){
            paintThese(m_selectedShape, null, true);
            m_selectedShape = null;
        }
        if(m_selectedAnimShape != null) {
            paintThese(m_selectedAnimShape.GetAnimationObject(), m_selectedAnimShape.GetAnimationPath(), true);
            m_selectedAnimShape = null;
        }
        if(m_selectedAnimPath != null){
            paintThese(m_selectedAnimPath.GetAnimationObject(), m_selectedAnimPath.GetAnimationPath(), true);
            m_selectedAnimPath = null;
        }
    }

    /**
     * delete the selected stroke. depends on the selected type.
     * @param render should render the screen after removing the stroke?
     */
    private void deleteStroke(boolean render){

        //user erasing a stroke when drawing
        if(m_selectedStroke != null) {
            m_builtStrokes.remove(m_selectedStroke);
            m_selectedStroke = null;
        }
        // the user erasing a shape (only objects that are not current shape)
        else if(m_selectedShape != null && !(m_btnPath.isSelected())){
            m_shapes.remove(m_selectedShape);
            m_selectedShape = null;
        }

        //if the user chose to delete a path, then send any shape corresponds to this path, to the shapes list.
        else if( m_selectedAnimPath != null ){
            for(Animation anim : m_animations){
                //find if there are other shapes corresponds to this path
               if(anim != m_selectedAnimPath && anim.GetAnimationPath() == m_selectedAnimPath.GetAnimationPath()){
                   m_shapes.add(anim.GetAnimationObject());
                   m_animations.remove(anim);
               }
            }
            Shape shape = m_selectedAnimPath.GetAnimationObject();
            m_shapes.add(shape);
            m_animations.remove(m_selectedAnimPath);
            m_selectedShape = shape;
            cancelSelected();
            m_selectedAnimPath = null;
        }
        // if user chose to delete a shape then delete its whole animation
        else if( m_selectedAnimShape != null){
            m_animations.remove(m_selectedAnimShape);
            m_selectedAnimShape = null;
        }

        if(render) {
            mListener.drawShapes(m_shapes, m_animations, m_builtStrokes);
            mListener.renderView();
        }
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
        void drawShapes(LinkedList<Shape> shapesList, LinkedList<Animation> anims, LinkedList<Stroke> strokes);
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

        if(m_btnErase.isSelected()) {
            deleteStroke(true);
            m_btnErase.setSelected(false);
            disableButtonsExcept(null);
            return;
        }
        if(m_btnPath.isSelected() && m_selectedAnimPath != null) { //the user chose an existing path and decided to merge
            completeDrawPath(m_selectedAnimPath.GetAnimationPath()); // create animation with the selected path
        }

    }


    /**
     * this is a callback from dialog
     */
    @Override
    public void resultCancel() {

        if(m_btnErase.isSelected()){
            if(!(m_btnPath.isSelected())){ // if the path btn is not selected then we can null it
                m_selectedShape = null;
            }

            m_selectedStroke = null;
            m_selectedAnimPath = m_selectedAnimShape = null;
            m_btnErase.setSelected(false);
        }

        if(m_btnPath.isSelected() && m_selectedAnimPath != null) { //the user chose an existing path and decided to cancel
            m_selectedAnimPath = null;
        }
    }

    @Override
    public StrokePaint getPaint() {
        return m_Paint;
    }

    @Override
    public void setNewSpeed(int speed) {
        m_pathSpeed = speed;
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
