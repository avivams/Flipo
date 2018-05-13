package com.flipo.avivams.flipo.activities;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.flipo.avivams.flipo.R;
import com.wacom.ink.manipulation.Intersector;
import com.wacom.ink.rasterization.BlendMode;
import com.wacom.ink.rasterization.InkCanvas;
import com.wacom.ink.rasterization.Layer;
import com.wacom.ink.rasterization.SolidColorBrush;
import com.wacom.ink.rasterization.StrokePaint;
import com.wacom.ink.rasterization.StrokeRenderer;
import com.wacom.ink.rendering.EGLRenderingContext;
import com.wacom.ink.smooth.MultiChannelSmoothener;

import java.util.LinkedList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DoodlesActivity extends AppCompatActivity {

    private SurfaceView m_SurfaceView;
    private InkCanvas m_Canvas;
    private Layer m_ViewLayer, m_StrokesLayer, m_CurrentFrameLayer;
    private SolidColorBrush m_SolidBrush;
    private StrokePaint m_Paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doodles);
        //transparent navigation bar background
        Window w = getWindow(); // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        ///////

        initSurfaceView();
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

                m_Canvas.clearLayer(m_CurrentFrameLayer, getResources().getColor(R.color.canvasBackground));

                m_SolidBrush = new SolidColorBrush();
                createStrokePaint();
/*
                m_Smoothener = new MultiChannelSmoothener(m_PathStride);
                m_Smoothener.enableChannel(2);

                m_StrokeRenderer = new StrokeRenderer(m_Canvas, m_Paint, m_PathStride, width, height);

                intersector = new Intersector<Stroke>();
*/
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
            public void surfaceDestroyed(SurfaceHolder holder) {
                releaseResources();
            }

        });
    }

    private void releaseResources(){
        m_Canvas.dispose();
    }

    private void renderView() {
        m_Canvas.setTarget(m_ViewLayer);
        m_Canvas.clearColor(getResources().getColor(R.color.canvasBackground));
        m_Canvas.invalidate();
    }

    /**
     * specifies how to draw each stroke
     */
    private void createStrokePaint()
    {
        m_Paint = new StrokePaint();
        m_Paint.setStrokeBrush(m_SolidBrush);
        m_Paint.setColor(Color.BLUE);// Particle brush.
        m_Paint.setWidth(Float.NaN);//draw it with width
    }

    /*
    //draw the strokes from the list from 'surfaceChanged'
    public synchronized void drawStrokes(LinkedList<Stroke> strokesList, boolean withPaths) {
        m_Canvas.setTarget(m_StrokesLayer);
        m_Canvas.clearColor(Color.WHITE);

        if(animation != null && !animation.isCancelled() && !(animation.getStatus() == AsyncTask.Status.FINISHED)){
            if(!selectedsList.isEmpty())
                strokesList = selectedsList;
        }


        for (Stroke stroke: strokesList){
            if(!withPaths && stroke.getType()== Stroke.StrokeType.PATH)
                continue;
            m_Paint.setColor(stroke.getColor());

            m_StrokeRenderer.setStrokePaint(m_Paint);
            m_StrokeRenderer.drawPoints(stroke.getPoints(), 0, stroke.getSize(),
                    stroke.getStartValue(), stroke.getEndValue(), true);
            m_StrokeRenderer.blendStroke(m_StrokesLayer, BlendMode.BLENDMODE_NORMAL);
        }

        m_Canvas.setTarget(m_CurrentFrameLayer);
        m_Canvas.clearColor(Color.WHITE);
        m_Canvas.drawLayer(m_StrokesLayer, BlendMode.BLENDMODE_NORMAL);
    }*/
}

