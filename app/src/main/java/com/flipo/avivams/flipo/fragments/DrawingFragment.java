package com.flipo.avivams.flipo.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.flipo.avivams.flipo.R;
import com.wacom.ink.path.PathUtils;
import com.wacom.ink.path.SpeedPathBuilder;
import com.wacom.ink.rasterization.BlendMode;
import com.wacom.ink.rasterization.InkCanvas;
import com.wacom.ink.rasterization.Layer;
import com.wacom.ink.rasterization.StrokeRenderer;

import java.nio.FloatBuffer;


public class DrawingFragment extends Fragment {

    private Button m_btnDraw, m_btnPath, m_btnParams, m_btnStyle;
    private SpeedPathBuilder m_PathBuilder;
    private ImageButton m_btnCompletedDraw;
    private SurfaceView m_SurfaceView;
    private OnDrawingInteractionListener mListener;

    public DrawingFragment() {
        // Required empty public constructor
    }


    public static DrawingFragment newInstance() {
        DrawingFragment fragment = new DrawingFragment();
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
        View v = inflater.inflate(R.layout.fragment_drawing, container, false);
        initButtonsListeners(v);

        m_PathBuilder = new SpeedPathBuilder();
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
     * this is for the onAttach methods only
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
            m_SurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    buildPath(event);
                    drawStroke(event);
                    mListener.renderView();
                    return true;
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


    private void initButtonsListeners(View fView){

        m_btnDraw = fView.findViewById(R.id.btn_draw);
        m_btnPath = fView.findViewById(R.id.btn_path);
        m_btnParams = fView.findViewById(R.id.btn_params);
        m_btnStyle = fView.findViewById(R.id.btn_style);
        m_btnCompletedDraw = fView.findViewById(R.id.btn_draw_complete);
        m_btnCompletedDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 1.1: before showing the toast, check if an object is drawn. (with the builder)
                //TODO 1.2: if an object was drawn then save it
                Toast.makeText(getActivity(), "Shape saved", Toast.LENGTH_LONG).show();
            }
        });
        m_btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtonsExcept((Button)v);
                m_btnCompletedDraw.setVisibility(View.VISIBLE);
            }
        });
        m_btnParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtonsExcept((Button)v);
            }
        });
        m_btnStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtonsExcept((Button)v);
            }
        });
        m_btnPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButtonsExcept((Button)v);
            }
        });
        m_btnDraw.setPressed(true);
        m_btnCompletedDraw.setVisibility(View.VISIBLE);
    }

    private void disableButtonsExcept(Button btn){
        int id = btn == null? View.NO_ID : btn.getId();

        m_btnStyle.setPressed(m_btnStyle.getId() == id);
        m_btnParams.setPressed(m_btnParams.getId() == id);
        m_btnPath.setPressed(m_btnPath.getId() == id); m_btnDraw.setPressed(m_btnDraw.getId() == id);
        m_btnCompletedDraw.setVisibility(View.GONE);
        // TODO 2: make function: stopBuildStroke(); // if a stroke is being build we need to finish it and dismiss
    }


    public void drawStroke(MotionEvent event){
        InkCanvas canvas = mListener.getCanvas();
        Layer currentFrameLayer = mListener.getCurrentView(), strokesLayer = mListener.getStrokesLayer();
        StrokeRenderer strokeRenderer = mListener.getRenderer();

        strokeRenderer.drawPoints(m_PathBuilder.getPathBuffer(), m_PathBuilder.getPathLastUpdatePosition(),
                m_PathBuilder.getAddedPointsSize(), m_PathBuilder.getStride(), event.getAction()==MotionEvent.ACTION_UP);

        if (event.getAction()!=MotionEvent.ACTION_UP){
            canvas.setTarget(currentFrameLayer, strokeRenderer.getStrokeUpdatedArea());
            canvas.clearColor(getResources().getColor(R.color.canvasBackground));
            canvas.drawLayer(strokesLayer, BlendMode.BLENDMODE_NORMAL);
            strokeRenderer.blendStrokeUpdatedArea(currentFrameLayer, BlendMode.BLENDMODE_NORMAL);
        } else {
            strokeRenderer.blendStroke(strokesLayer, BlendMode.BLENDMODE_NORMAL);
            canvas.setTarget(currentFrameLayer);
            canvas.clearColor(getResources().getColor(R.color.canvasBackground));
            canvas.drawLayer(strokesLayer, BlendMode.BLENDMODE_NORMAL);
        }
    }

    public void buildPath(MotionEvent event){
        if (event.getAction()!=MotionEvent.ACTION_DOWN
                && event.getAction()!=MotionEvent.ACTION_MOVE
                && event.getAction()!=MotionEvent.ACTION_UP){
            return;
        }

        PathUtils.Phase phase = PathUtils.getPhaseFromMotionEvent(event);
        // Add the current input point to the path builder
        FloatBuffer part = m_PathBuilder.addPoint(phase, event.getX(), event.getY(), event.getEventTime());
        int partSize = m_PathBuilder.getPathPartSize();

        if (partSize>0){
            // Add the returned control points (aka partial path) to the path builder.
            m_PathBuilder.addPathPart(part, partSize);
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
    }
}
