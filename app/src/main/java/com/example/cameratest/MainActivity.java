//https://www.youtube.com/watch?v=w8P0boxogp0

package com.example.cameratest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private CameraCaptureSession myCameraCaptureSession;
    private String myCameraId;
    private CameraManager myCameraManager;
    private CameraDevice myCameraDevice;
    private TextureView myTextureView;
    private CaptureRequest.Builder myCaptureRequestBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myTextureView = findViewById(R.id.textureView);
        myCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        openCamera();

    }

    private CameraDevice.StateCallback myStackCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            myCameraDevice = camera;
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            myCameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            myCameraDevice.close();
            myCameraDevice = null;
        }
    };

    private void openCamera() {
        try {
            myCameraId = myCameraManager.getCameraIdList()[0];

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            myCameraManager.openCamera(myCameraId, myStackCallBack, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cameraPreview(View view) {
        SurfaceTexture mySurfaceTexture = myTextureView.getSurfaceTexture();
        Surface mySurface = new Surface(mySurfaceTexture);

        try {
            myCaptureRequestBuilder = myCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            myCaptureRequestBuilder.addTarget(mySurface);

            myCameraDevice.createCaptureSession(Arrays.asList(mySurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    myCameraCaptureSession = session;
                    myCaptureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                    try {
                        myCameraCaptureSession.setRepeatingRequest(myCaptureRequestBuilder.build(), null, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

}
