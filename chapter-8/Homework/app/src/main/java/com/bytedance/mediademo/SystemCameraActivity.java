package com.bytedance.mediademo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemCameraActivity extends AppCompatActivity {
    private int REQUEST_CODE_TAKE_PHOTO = 1001;
    private int REQUEST_CODE_TAKE_PHOTO_PATH = 1002;
    private int PERMISSION_REQUEST_CAMERA_CODE = 1003;
    private int PERMISSION_REQUEST_CAMERA_PATH_CODE = 1004;
    public static final int PICTURE = 0;
    public static final int VIDEO = 1;

    private ImageView imageView;
    private String takeImagePath;

    private Uri localUri;
    private File localFile;

    public static void startUI(Context context) {
        Intent intent = new Intent(context, SystemCameraActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_camera);
        imageView = findViewById(R.id.iv_img);
    }

    public void takePhoto(View view) {
        requestCameraPermission();
    }

    public void takePhotoUsePath(View view) {
        requestCameraAndSDCardPermission();
    }

    private void requestCameraAndSDCardPermission() {
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        if (hasCameraPermission) {
            takePhotoUsePathHasPermission();
        } else {
            String[] permissions = new String[]{Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CAMERA_PATH_CODE);
        }
    }

    private void takePhotoUsePathHasPermission() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takeImagePath = getOutputMediaPath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, PathUtils.getUriForFile(this, takeImagePath));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO_PATH);
        }
        // todo 1.3 唤起拍照 intent 并设置图片文件地址

    }

    private String getOutputMediaPath() {
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".jpg");
        if (!mediaFile.exists()) {
            mediaFile.getParentFile().mkdirs();
        }
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(),
                    mediaFile.getAbsolutePath(), "IMG_" + timeStamp + ".jpg", null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        localFile = mediaFile;
        localUri = Uri.fromFile(mediaFile);
        return mediaFile.getAbsolutePath();
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[]{Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CAMERA_CODE);
        } else {
            takePhotoHasPermission();
        }
    }

    private void takePhotoHasPermission() {
        // todo 1.1 直接唤起拍照 intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotoHasPermission();
            } else {
                Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_REQUEST_CAMERA_PATH_CODE) {
            boolean hasPermission = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    hasPermission = false;
                    break;
                }
            }
            if (hasPermission) {
                takePhotoUsePathHasPermission();
            } else {
                Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(bitmap);
            //私有路径里的照片没有导入到相册当中
            // todo 1.2 在 data 中直接获取 bitmap
        } else if (requestCode == REQUEST_CODE_TAKE_PHOTO_PATH && resultCode == RESULT_OK) {

            saveImageToGallery(this, localFile, PICTURE);

            int targetWidth = imageView.getWidth();
            int targetHeight = imageView.getHeight();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(takeImagePath, options);
            int photoWidth = options.outWidth;
            int photoHeight = options.outHeight;

            // todo 1.4 通过图片地址构造 bitmap
            int scaleFactor = Math.min(photoWidth / targetWidth, photoHeight / targetHeight);
            options.inJustDecodeBounds = false;
            options.inSampleSize = scaleFactor;
            Bitmap bitmap = BitmapFactory.decodeFile(takeImagePath, options);
            imageView.setImageBitmap(bitmap);

            // todo 1.5 注意图片大小
        }
    }


    public void saveImageToGallery(Context context, File file, int type) {

        // 其次把文件插入到系统图库
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "IMG_" + timeStamp;
            switch (type) {
                case PICTURE:
                    fileName += ".jpg";
                    MediaStore.Images.Media.insertImage(context.getContentResolver(),
                            file.getAbsolutePath(), fileName, null);
                    break;
                case VIDEO:
                    fileName += ".mp4";
//                    downMp4(context,file);
                    break;
                default:
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 判断SDK版本是不是4.4或者高于4.4
            String[] paths = new String[]{file.getAbsolutePath()};
            MediaScannerConnection.scanFile(context, paths, null, null);
        } else {
            final Intent intent;
            if (file.isDirectory()) {
                intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerReceiver");
                intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
            } else {
                intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(file));
            }
            context.sendBroadcast(intent);
        }
    }

//    private void downMp4(Context context, File file) {
//        ContentResolver localContentResolver = getContentResolver();
//        ContentValues localContentValues = getVideoContentValues(context, file, System.currentTimeMillis());
//        Uri localUri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);
//    }
//
//    public static ContentValues getVideoContentValues(Context paramContext, File paramFile, long paramLong) {
//        ContentValues localContentValues = new ContentValues();
//        localContentValues.put("title", paramFile.getName());
//        localContentValues.put("_display_name", paramFile.getName());
//        localContentValues.put("mime_type", "video/mp4");
//        localContentValues.put("datetaken", Long.valueOf(paramLong));
//        localContentValues.put("date_modified", Long.valueOf(paramLong));
//        localContentValues.put("date_added", Long.valueOf(paramLong));
//        localContentValues.put("_data", paramFile.getAbsolutePath());
//        localContentValues.put("_size", Long.valueOf(paramFile.length()));
//        return localContentValues;
//    }
}