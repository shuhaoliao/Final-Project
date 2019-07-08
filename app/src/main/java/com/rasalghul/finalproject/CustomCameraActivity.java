package com.rasalghul.finalproject;

import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.rasalghul.finalproject.utils.Utils.MEDIA_TYPE_VIDEO;
import static com.rasalghul.finalproject.utils.Utils.getOutputMediaFile;

public class CustomCameraActivity extends AppCompatActivity implements ScaleGestureDetector.OnScaleGestureListener{

    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private TextView record;
    private TextView back;
    private File mRecord;
    private SurfaceHolder surfaceHolder;
    private ScaleGestureDetector gd;

    private int CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;

    private boolean isRecording = false;

    private int rotationDegree = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_custom_camera);

        releaseCameraAndPreview();
        mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        rotationDegree = getCameraDisplayOrientation(Camera.CameraInfo.CAMERA_FACING_BACK);
        mCamera.setDisplayOrientation(rotationDegree);

        gd = new ScaleGestureDetector(this,this);

        record = findViewById(R.id.btn_record);
        back = findViewById(R.id.btn_facing);
        mSurfaceView = findViewById(R.id.img);
        surfaceHolder = mSurfaceView.getHolder();
        //todo 给SurfaceHolder添加Callback
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    mCamera.setPreviewDisplay(surfaceHolder);
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        });

        findViewById(R.id.btn_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 录制，第一次点击是start，第二次点击是stop
                if (isRecording) {
                    //todo 停止录制
                    CustomCameraActivity.this.releaseMediaRecorder();
                    isRecording = false;
                    record.setText("RECORD");
                    CustomCameraActivity.this.startPreview(surfaceHolder);
                    Intent intent = new Intent(CustomCameraActivity.this, RecordVideoActivity.class);
                    startActivity(intent);
                } else {
                    //todo 录制
                    if (CustomCameraActivity.this.prepareVideoRecorder()) mMediaRecorder.start();
                    isRecording = true;
                    record.setText("STOP");
                }
            }
        });

        findViewById(R.id.btn_facing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 切换前后摄像头
                if (CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK)
                    CustomCameraActivity.this.changeCam(Camera.CameraInfo.CAMERA_FACING_FRONT, "BACK");
                else if (CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_FRONT)
                    CustomCameraActivity.this.changeCam(Camera.CameraInfo.CAMERA_FACING_BACK, "FACING");
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gd.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        int maxZ = mCamera.getParameters().getMaxZoom();
        Camera.Parameters p = mCamera.getParameters();
        int nZoom = mCamera.getParameters().getZoom();
        if(scaleGestureDetector.getScaleFactor()>1) {
            int pZoom = nZoom + 1 > maxZ ? maxZ : nZoom + 1;
            p.setZoom(pZoom);
            mCamera.setParameters(p);
        }else if(scaleGestureDetector.getScaleFactor()<1){
            int pZoom = nZoom - 1 < 0 ? 0 : nZoom - 1;
            p.setZoom(pZoom);
            mCamera.setParameters(p);
        }
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
    }

    private void changeCam(int CAMERA_TYPE_TO, String facingText){
        mCamera.stopPreview();
        mCamera.release();
        mCamera=null;

        this.CAMERA_TYPE = CAMERA_TYPE_TO;
        mCamera = getCamera(CAMERA_TYPE_TO);
        rotationDegree = getCameraDisplayOrientation(CAMERA_TYPE_TO);
        mCamera.setDisplayOrientation(rotationDegree);
        back.setText(facingText);

        try {
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    public Camera getCamera(int position) {
        CAMERA_TYPE = position;
        if (mCamera != null) {
            releaseCameraAndPreview();
        }
        Camera cam = Camera.open(position);

        //todo 摄像头添加属性，例是否自动对焦，设置旋转方向等

        return cam;
    }


    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;

    private int getCameraDisplayOrientation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            result = (DEGREE_360 - result) % DEGREE_360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }


    private void releaseCameraAndPreview() {
        //todo 释放camera资源
        int camCount = Camera.getNumberOfCameras();
        for(int i=0;i<camCount;i++) Camera.open(i).release();
    }

    Camera.Size size;

    private void startPreview(SurfaceHolder holder) {
        //todo 开始预览


    }


    private MediaRecorder mMediaRecorder;

    private boolean prepareVideoRecorder() {
        //todo 准备MediaRecorder
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mRecord = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        mMediaRecorder.setOutputFile(mRecord.toString());
        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        mMediaRecorder.setOrientationHint(rotationDegree);
        try {
            mMediaRecorder.prepare();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void releaseMediaRecorder() {
        //todo 释放MediaRecorder
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mCamera.lock();
    }
}
