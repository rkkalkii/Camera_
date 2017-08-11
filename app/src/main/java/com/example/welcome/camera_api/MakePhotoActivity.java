package com.example.welcome.camera_api;

/**
 * Created by Neha on 30-04-2017.
 */

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MakePhotoActivity extends Activity {
   public final static String DEBUG_TAG = "MakePhotoActivity";
    public Camera camera;
    private int cameraId = 0;
    private CameraPreview cameraPreview;
    FrameLayout preview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // do we have a camera?
       start_camera();
    }

    public void onClick(View view) {
        camera.startPreview();
        camera.takePicture(null, null,
                new PhotoHandler(getApplicationContext()));
        restartCAM();
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(DEBUG_TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public void start_camera(){
        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        } else {
            cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId);
            }
        }
//        mCamera = getCameraInstance();
        cameraPreview = new CameraPreview(this, camera);
        cameraPreview.setCameraDisplayOrientation(this,cameraId,camera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);
    }
    private void restartCAM() {
        Thread restart_preview=new Thread(){public void run(){
            try {
                Thread.sleep(500);
                camera.startPreview();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }};
        restart_preview.start();}

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.release();
//            camera = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}