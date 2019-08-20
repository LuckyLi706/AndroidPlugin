package com.lucky.cameraplugin.camera1;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.lucky.cameraplugin.utils.LogUtil;

import java.io.IOException;
import java.util.List;

/**
 * 作者：jacky on 2019/8/20 09:58
 * 邮箱：jackyli706@gmail.com
 */
public class CameraHelper implements Camera.PreviewCallback {

    private SurfaceView mSurfaceView;   //用于显示预览的显示
    private Activity activity;      //与Activity的某些声明周期进行绑定

    private Camera mCamera;
    private SurfaceHolder surfaceHolder;    //对SurfaceView各时期做处理
    private Camera.Parameters mParameters;   //Camera对象的参数
    public int mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;  //摄像头方向
    private int mDisplayOrientation = 0;    //预览旋转的角度

    private int picWidth = 2160;       //保存图片的宽
    private int picHeight = 3840;     //保存图片的高

    public CameraHelper(SurfaceView surfaceView, Activity activity) {
        this.mSurfaceView = surfaceView;
        this.activity = activity;
        surfaceHolder = mSurfaceView.getHolder();
        init();
    }

    private void init() {
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            //surface第一次创建时回调
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (mCamera == null) {
                    openCamera();
                }
                startPreview();
            }

            //surface变化的时候回调(格式/大小)
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {

            }

            //surface销毁的时候回调
            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                releaseCamera();
            }
        });
    }

    //打开相机
    private boolean openCamera() {
        boolean supportCameraFacing = supportCameraFacing(mCameraFacing);  //判断手机是否支持前置/后置摄像头
        if (supportCameraFacing) {
            try {
                mCamera = Camera.open(mCameraFacing);
                initParameters(mCamera);          //初始化相机配置信息
                mCamera.setPreviewCallback(this);
            } catch (Exception e) {
                e.printStackTrace();
                //toast("打开相机失败!")
                return false;
            }
        }
        return supportCameraFacing;
    }

    //判断是否支持某个相机
    private boolean supportCameraFacing(int cameraFacing) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == cameraFacing) return true;
        }
        return false;
    }


    //配置相机参数
    private void initParameters(Camera camera) {
        try {
            mParameters = camera.getParameters();
            mParameters.setPreviewFormat(ImageFormat.NV21);  //设置预览图片的格式

            //获取与指定宽高相等或最接近的尺寸
            //设置预览尺寸
            Camera.Size bestPreviewSize = getBestSize(mSurfaceView.getWidth(), mSurfaceView.getWidth(), mParameters.getSupportedPreviewSizes());
            mParameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
            //}
            //设置保存图片尺寸
            Camera.Size bestPicSize = getBestSize(picWidth, picHeight, mParameters.getSupportedPreviewSizes());
            mParameters.setPictureSize(bestPicSize.width, bestPicSize.height);
            //对焦模式
            if (isSupportFocus(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            camera.setParameters(mParameters);
        } catch (Exception e) {
            LogUtil.e(this, e.getMessage());
            //toast("相机初始化失败!")
        }
    }

    //获取与指定宽高相等或最接近的尺寸
    private Camera.Size getBestSize(int targetWidth, int targetHeight, List<Camera.Size> sizeList) {
        Camera.Size bestSize = null;
        double targetRatio = (Integer.valueOf(targetHeight).doubleValue() / targetWidth);  //目标大小的宽高比
        double minDiff = targetRatio;


        for (Camera.Size size : sizeList) {
            double supportedRatio = (Integer.valueOf(size.width).doubleValue() / size.height);
            LogUtil.d("系统支持的尺寸 : ${size.width} * ${size.height} ,    比例$supportedRatio");
        }

        for (Camera.Size size : sizeList) {
            if (size.width == targetHeight && size.height == targetWidth) {
                bestSize = size;
                break;
            }
            double supportedRatio = (Integer.valueOf(size.width).doubleValue() / size.height);
            if (Math.abs(supportedRatio - targetRatio) < minDiff) {
                minDiff = Math.abs(supportedRatio - targetRatio);
                bestSize = size;
            }
        }
        LogUtil.d("目标尺寸 ：$targetWidth * $targetHeight ，   比例  $targetRatio");
        LogUtil.d("最优尺寸 ：${bestSize?.height} * ${bestSize?.width}");
        return bestSize;
    }

    //开始预览
    void startPreview() {
        // mCamera ?.let {
        try {
            mCamera.setPreviewDisplay(surfaceHolder);         //设置相机预览对象
        } catch (IOException e) {
            e.printStackTrace();
        }
        setCameraDisplayOrientation();    //设置预览时相机旋转的角度
        mCamera.startPreview();
        // }
    }

    //判断是否支持某一对焦模式
    private boolean isSupportFocus(String focusMode) {
        boolean autoFocus = false;
        List<String> listFocusMode = mParameters.getSupportedFocusModes();
        for (String mode : listFocusMode) {
            if (mode == focusMode)
                autoFocus = true;
            LogUtil.d("相机支持的对焦模式:" + mode);
        }
        return autoFocus;
    }

    //切换摄像头
    void exchangeCamera() {
        releaseCamera();
        if (mCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK)
            mCameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
        else
            mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
        openCamera();
        startPreview();
    }

    //释放相机
    void releaseCamera() {
        if (mCamera != null) {
            // mCamera?.stopFaceDetection()
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    //设置预览旋转的角度
    private void setCameraDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraFacing, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        int screenDegree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                screenDegree = 0;
                break;
            case Surface.ROTATION_90:
                screenDegree = 90;
                break;
            case Surface.ROTATION_180:
                screenDegree = 180;
                break;
            case Surface.ROTATION_270:
                screenDegree = 270;
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mDisplayOrientation = (info.orientation + screenDegree) % 360;
            mDisplayOrientation = (360 - mDisplayOrientation) % 360;          // compensate the mirror
        } else {
            mDisplayOrientation = (info.orientation - screenDegree + 360) % 360;
        }
        mCamera.setDisplayOrientation(mDisplayOrientation);

        LogUtil.d("屏幕的旋转角度 : $rotation");
        LogUtil.d("setDisplayOrientation(result) : $mDisplayOrientation");
    }

    /**
     * 第一种方式使用该方法
     * 这些预览到的原始数据是非常有用的，比如我们可以保存下来当做一张照片，
     * 还有很多第三方的人脸检测及静默活体检测的sdk，
     * 都需要我们把相机预览的数据实时地传递过去。
     * 注意：实际上这个回调方法会一直一直的调用，
     * 如果要保存一张照片的话应该加个字段进行控制，此处只是做演示
     *
     * @param bytes  相机预览的数据
     * @param camera
     */
    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {

    }

    /**
     * 第一个是点击拍照时的回调。
     * 如果传null，则没有任何效果
     * 如果写一个空实现，则在点击拍照时会有"咔擦"声
     * <p>
     * 第二个和第三个参数类型一样，PictureCallback 有一个抽象方法
     * void onPictureTaken(byte[] data, Camera camera)
     * data就是点击拍照后相机返回的照片的byte数组，用该数组创建一个bitmap保存下来，就得到了拍摄的照片
     */
    public void takePicture() {
        mCamera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {

            }
        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {

            }
        });
    }
}
