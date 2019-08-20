package com.lucky.intentplugin.sytemactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.lucky.intentplugin.Constants;
import com.lucky.intentplugin.utils.LogUtil;
import com.lucky.intentplugin.utils.PermissionUtil;

import java.io.File;

/**
 * 作者：jacky on 2019/8/17 11:28
 * 邮箱：jackyli706@gmail.com
 * <p>
 * 使用默认手机照相机拍照
 * 当不指定uri时，拍照返回的intent不为null
 * 如果指定uri，即保存路径，返回的intent为null
 * <p>
 * 但是在某些手机上依然拿到的intent为null
 * 使用自定义路径来解决这个问题
 */

/**
 * 使用默认手机照相机拍照
 * 当不指定uri时，拍照返回的intent不为null
 * 如果指定uri，即保存路径，返回的intent为null
 * <p>
 * 但是在某些手机上依然拿到的intent为null
 * 使用自定义路径来解决这个问题
 */

/**
 *   针对7.0以及之后的Uri处理
 *   1、在xml文件夹下创建file_paths.xml文件
 *   2、在配置文件配置创建provider
 *   <provider
 *             android:name="androidx.core.content.FileProvider"                 //FileProvider所在的类
 *             android:authorities="com.example.androidplugin.fileprovider"      //自定义的名字FileProvider第二个参数
 *             android:exported="false"
 *             android:grantUriPermissions="true">
 *             <meta-data
 *                 android:name="android.support.FILE_PROVIDER_PATHS"
 *                 android:resource="@xml/file_paths" />
 *         </provider>
 *   3、
 *
 */
public class IntentCamera {
    private static final IntentCamera ourInstance = new IntentCamera();

    private Uri uri;
    private File mPhotoFile;

    public static IntentCamera getInstance() {
        return ourInstance;
    }

    private IntentCamera() {
    }

    public void startCamera(Context context, File path) {
        if (PermissionUtil.CAMERA(context)) {
            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intentCamera.resolveActivity(context.getPackageManager()) != null) {
                if (path != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //第二个参数为 包名.fileprovider
                        uri = FileProvider.getUriForFile(context, "com.example.androidplugin.fileprovider", path);
                        intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);   //获取申请的临时权限
                    } else {
                        uri = Uri.fromFile(path);
                    }
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);      //输出的图片位置
                    intentCamera.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());   //输出的图片格式
                }
                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intentCamera, Constants.INTENT_CAMERA);
                }
            } else {
                LogUtil.w(this, "系统相机不存在");
            }
        } else {
            LogUtil.w(this, "CAMERA权限不存在");
        }
    }


    public File getFilePath(Context context) {
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && PermissionUtil.WRITE_EXTERNAL_STORAGE(context)) {

                String mPhotoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "pic.jpg";
                mPhotoFile = new File(mPhotoPath);
                if (!mPhotoFile.exists()) {
                    mPhotoFile.createNewFile();
                }
                return mPhotoFile;
            } else {
                String path = context.getFilesDir() + File.separator + "images" + File.separator;
                File mPath = new File(path);
                if (!mPath.exists()) {
                    mPath.mkdir();
                }
                mPhotoFile = new File(path, "pic.jpg");
                if (!mPhotoFile.exists()) {
                    mPhotoFile.createNewFile();
                }
                return mPhotoFile;
            }
        } catch (Exception e) {
            LogUtil.e(this, e.getMessage());
        }
        return null;
    }
}
