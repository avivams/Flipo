package com.flipo.avivams.flipo.animation;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.ToggleButton;

import com.flipo.avivams.flipo.R;

import java.io.IOException;

/**
 * Created by aviva on 06/05/2018.
 */

public class AnimeRecorder {
    private static final SparseIntArray sf_ORIENTATIONS = new SparseIntArray();
    private static final String sf_TAG = "AnimeRecorder";
    private static final int sf_DISPLAY_WIDTH = 720;
    private static final int sf_DISPLAY_HEIGHT = 1280;
    public static final int sf_REQUEST_PERMISSIONS = 10;
    public static final int sf_REQUEST_CODE = 1000;
    private final Activity f_Activity;
    private int m_ScreenDensity;
    private MediaProjectionManager m_ProjectionManager;
    private MediaProjection m_MediaProjection;
    private VirtualDisplay m_VirtualDisplay;
    private MediaProjectionCallback m_MediaProjectionCallback;
    private ToggleButton m_ToggleButton;
    private MediaRecorder m_MediaRecorder;

    static {
        sf_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        sf_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        sf_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        sf_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public AnimeRecorder(Activity i_Activity) {
        f_Activity = i_Activity;
        startMembers();
        //m_ToggleButton = f_Activity.findViewById(R.id.record);
        m_ToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButton_OnClick(v);
            }
        });
    }

    private void startMembers(){
        DisplayMetrics metrics = new DisplayMetrics();
        f_Activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        m_ScreenDensity = metrics.densityDpi;

        m_MediaRecorder = new MediaRecorder();

        m_ProjectionManager = (MediaProjectionManager) f_Activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    private void toggleButton_OnClick(View i_View){
        if (ContextCompat.checkSelfPermission(f_Activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(f_Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ) {
                m_ToggleButton.setChecked(false);
                Snackbar.make(f_Activity.findViewById(android.R.id.content), "permit recording",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(f_Activity,
                                        new String[]{Manifest.permission
                                                .WRITE_EXTERNAL_STORAGE},
                                        sf_REQUEST_PERMISSIONS);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(f_Activity,
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE},
                        sf_REQUEST_PERMISSIONS);
            }
        } else {
            onToggleScreenShare(i_View);
        }
    }

    public void onToggleScreenShare(View view) {
        if (((ToggleButton) view).isChecked()) {
            initRecorder();
            shareScreen();
        } else {
            m_MediaRecorder.stop();
            m_MediaRecorder.reset();
            Log.v(sf_TAG, "Stopping Recording");
            stopScreenSharing();
        }
    }

    private void shareScreen() {
        if (m_MediaProjection == null) {
            f_Activity.startActivityForResult(m_ProjectionManager.createScreenCaptureIntent(), sf_REQUEST_CODE);
            return;
        }
        m_VirtualDisplay = createVirtualDisplay();
        m_MediaRecorder.start();
        Log.d(sf_TAG, "started recording");
    }

    private VirtualDisplay createVirtualDisplay() {
        return m_MediaProjection.createVirtualDisplay("MainActivity",
                sf_DISPLAY_WIDTH, sf_DISPLAY_HEIGHT, m_ScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                m_MediaRecorder.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }

    private void initRecorder() {
        try {
            m_MediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            m_MediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            m_MediaRecorder.setOutputFile(Environment
                    .getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS) + "/draw_animation.mp4");
            m_MediaRecorder.setVideoSize(sf_DISPLAY_WIDTH, sf_DISPLAY_HEIGHT);
            m_MediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            m_MediaRecorder.setVideoEncodingBitRate(512 * 1000);
            m_MediaRecorder.setVideoFrameRate(30);
            int rotation = f_Activity.getWindowManager().getDefaultDisplay().getRotation();
            int orientation = sf_ORIENTATIONS.get(rotation);// + 90);
            m_MediaRecorder.setOrientationHint(orientation);
            m_MediaRecorder.prepare();
            Log.d(sf_TAG, Environment
                    .getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS) + "/draw_animation.mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            if (m_ToggleButton.isChecked()) {
                m_ToggleButton.setChecked(false);
                m_MediaRecorder.stop();
                m_MediaRecorder.reset();
                Log.v(sf_TAG, "Recording Stopped");
            }
            m_MediaProjection = null;
            stopScreenSharing();
        }
    }

    private void stopScreenSharing() {
        if (m_VirtualDisplay == null) {
            return;
        }
        m_VirtualDisplay.release();
        //m_MediaRecorder.release(); //If used: m_MediaRecorder object cannot
        // be reused again
        destroyMediaProjection();
    }

    public void destroyMediaProjection() {
        if (m_MediaProjection != null) {
            m_MediaProjection.unregisterCallback(m_MediaProjectionCallback);
            m_MediaProjection.stop();
            m_MediaProjection = null;
        }
        Log.i(sf_TAG, "MediaProjection Stopped");
    }

    public void startAfterResult(int resultCode, Intent data){

        m_MediaProjectionCallback = new AnimeRecorder.MediaProjectionCallback();
        m_MediaProjection = m_ProjectionManager.getMediaProjection(resultCode, data);
        m_MediaProjection.registerCallback(m_MediaProjectionCallback, null);
        m_VirtualDisplay = createVirtualDisplay();
        m_MediaRecorder.start();
    }
}

