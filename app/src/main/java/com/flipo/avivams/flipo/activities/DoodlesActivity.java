package com.flipo.avivams.flipo.activities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.flipo.avivams.flipo.R;
import com.flipo.avivams.flipo.dialogs.ChooseDialog;
import com.flipo.avivams.flipo.fragments.DrawingFragment;
import com.flipo.avivams.flipo.utilities.Animation;
import com.flipo.avivams.flipo.utilities.AnimationPath;
import com.flipo.avivams.flipo.utilities.Shape;
import com.flipo.avivams.flipo.utilities.Stroke;
import com.wacom.ink.manipulation.Intersector;
import com.wacom.ink.rasterization.BlendMode;
import com.wacom.ink.rasterization.InkCanvas;
import com.wacom.ink.rasterization.Layer;
import com.wacom.ink.rasterization.SolidColorBrush;
import com.wacom.ink.rasterization.StrokePaint;
import com.wacom.ink.rasterization.StrokeRenderer;
import com.wacom.ink.rendering.EGLRenderingContext;

import java.util.LinkedList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DoodlesActivity extends AppCompatActivity implements DrawingFragment.OnDrawingInteractionListener {

    private SurfaceView m_SurfaceView;
    private StrokeRenderer m_StrokeRenderer;
    private int m_CanvasColor;
    private InkCanvas m_Canvas;
    private Layer m_ViewLayer, m_StrokesLayer, m_CurrentFrameLayer;
    private SolidColorBrush m_SolidBrush;
    private StrokePaint m_Paint;
    private Intersector<Stroke> intersector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //transparent navigation bar background
        Window w = getWindow(); // in Activity's onCreate() for instance

        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        ///////
        setContentView(R.layout.activity_doodles);
        m_CanvasColor = getResources().getColor(R.color.canvasBackground);

        m_SolidBrush = new SolidColorBrush();
        createStrokePaint();

        initSurfaceView();

        Fragment f = DrawingFragment.newInstance(m_Paint);
        getFragmentManager().beginTransaction().add(R.id.fragment_container, f).commit();
    }

    /**
     * specifies how to draw each stroke
     */
    private void createStrokePaint() {
        m_Paint = new StrokePaint();
        m_Paint.setStrokeBrush(m_SolidBrush);
        m_Paint.setColor(Color.BLUE);// Particle brush.
        //m_Paint.setWidth(Float.NaN);//draw it with width
        m_Paint.setWidth(50.0f);
    }

    private void initSurfaceView() {
        m_SurfaceView = findViewById(R.id.surfaceView);
        m_SurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (m_Canvas != null && !m_Canvas.isDisposed()) {
                    releaseResources();
                }

                m_Canvas = InkCanvas.create(holder, new EGLRenderingContext.EGLConfiguration());

                m_ViewLayer = m_Canvas.createViewLayer(width, height);//everything in this layer is drawn to screen (it is the target layer)
                m_StrokesLayer = m_Canvas.createLayer(width, height);//this layer draws all strokes
                m_CurrentFrameLayer = m_Canvas.createLayer(width, height);//this layer contains all the drawings on screen.
                //to add more drawings, we add them to this layer so on renderView() will update it.

                m_Canvas.clearLayer(m_CurrentFrameLayer, m_CanvasColor);
                if(m_SolidBrush == null)
                    m_SolidBrush = new SolidColorBrush();
                if(m_Paint == null)
                    createStrokePaint();

       //         m_Smoothener = new MultiChannelSmoothener(m_PathStride);
        //        m_Smoothener.enableChannel(2);

                m_StrokeRenderer = new StrokeRenderer(m_Canvas, m_Paint, width, height);

                intersector = new Intersector<Stroke>();

                /*USE A THREAD HERE
                Log.d("loading", "loaded");
                loadStrokes(Uri.fromFile(getFileStreamPath(getString(R.string.FILE_BIN_SAVE_NAME) + ".bin")));
                */
              //  drawStrokes(strokesList, true);
                renderView();
            }


            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder)
            {
                releaseResources();
            }

        });
    }

    @Override
    public void renderView() {
        m_Canvas.setTarget(m_ViewLayer);
        m_Canvas.drawLayer(m_CurrentFrameLayer, BlendMode.BLENDMODE_OVERWRITE);
        // m_Canvas.clearColor(getResources().getColor(R.color.canvasBackground));
        m_Canvas.invalidate();
    }

    private void releaseResources(){
        m_StrokeRenderer.dispose();
        m_Canvas.dispose();
    }

    //draw the strokes from the list from 'surfaceChanged'
    @Override
    public synchronized void drawShapes(LinkedList<Shape> shapesList, LinkedList<Animation> anims) {
        m_Canvas.setTarget(m_StrokesLayer);
        m_Canvas.clearColor(m_CanvasColor);
        int oldPaintColor = m_Paint.getColor();
/*
        if(animation != null && !animation.isCancelled() && !(animation.getStatus() == AsyncTask.Status.FINISHED)){
            if(!selectedsList.isEmpty())
                strokesList = selectedsList;
        }
*/

        for (Shape shape: shapesList){
            for(Stroke stroke : shape.getShape()) {
                m_Paint.setColor(stroke.GetColor());

                m_StrokeRenderer.setStrokePaint(m_Paint);
                m_StrokeRenderer.drawPoints(stroke.getPoints(), 0, stroke.getSize(), stroke.getStride(),
                        stroke.getStartValue(), stroke.getEndValue(), true);
                m_StrokeRenderer.blendStroke(m_StrokesLayer, BlendMode.BLENDMODE_NORMAL);
            }
        }

        for (Animation anim: anims){
            for(Stroke stroke : anim.GetAnimationObject().getShape()) {
                m_Paint.setColor(stroke.GetColor());

                m_StrokeRenderer.setStrokePaint(m_Paint);
                m_StrokeRenderer.drawPoints(stroke.getPoints(), 0, stroke.getSize(), stroke.getStride(),
                        stroke.getStartValue(), stroke.getEndValue(), true);
                m_StrokeRenderer.blendStroke(m_StrokesLayer, BlendMode.BLENDMODE_NORMAL);
            }

            for(Stroke path : anim.GetAnimationPath().GetPath()) {
                m_Paint.setColor(path.GetColor());

                m_StrokeRenderer.setStrokePaint(m_Paint);
                m_StrokeRenderer.drawPoints(path.getPoints(), 0, path.getSize(), path.getStride(),
                        path.getStartValue(), path.getEndValue(), true);
                m_StrokeRenderer.blendStroke(m_StrokesLayer, BlendMode.BLENDMODE_NORMAL);
            }
        }

        m_Canvas.setTarget(m_CurrentFrameLayer);
        m_Canvas.clearColor(m_CanvasColor);
        m_Canvas.drawLayer(m_StrokesLayer, BlendMode.BLENDMODE_NORMAL);
        m_Paint.setColor(oldPaintColor);
    }


    public void exitOnClick(View view){
        finish();
    }

    @Override
    public InkCanvas getCanvas() {
        return m_Canvas;
    }

    @Override
    public Layer getCurrentView() {
        return m_CurrentFrameLayer;
    }

    @Override
    public Layer getStrokesLayer() {
        return m_StrokesLayer;
    }

    @Override
    public StrokeRenderer getRenderer() {
        return m_StrokeRenderer;
    }

    @Override
    public void setNewPaint(StrokePaint newPaint){
        m_Paint = newPaint;
    }

    @Override
    public Intersector<Stroke> getIntersector() {
        return intersector;
    }
}

