package com.bytedance.mediademo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SystemRecordActivity extends AppCompatActivity {
    private final static int PERMISSION_REQUEST_CODE = 1001;
    private final static int REQUEST_CODE_RECORD = 1002;

    private String mp4Path = "";
    private VideoView mVideoView;
    public static void startUI(Context context) {
        Intent intent = new Intent(context, SystemRecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_record);
        mVideoView = findViewById(R.id.videoview);
    }

    public void record(View view) {
        requestPermission();
    }

    private void requestPermission() {
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean hasAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        if (hasCameraPermission && hasAudioPermission) {
            recordVideo();
        } else {
            List<String> permission = new ArrayList<String>();
            if (!hasCameraPermission) {
                permission.add(Manifest.permission.CAMERA);
            }
            if (!hasAudioPermission) {
                permission.add(Manifest.permission.RECORD_AUDIO);
            }
            ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), PERMISSION_REQUEST_CODE);
        }

    }

    private void recordVideo() {
        // todo 2.1 唤起视频录制 intent 并设置视频地址
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mp4Path = getOutputMediaPath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT,PathUtils.getUriForFile(this,mp4Path));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent,REQUEST_CODE_RECORD);
        }
    }

    private String getOutputMediaPath() {
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".mp4");
        if (!mediaFile.exists()) {
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermission = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
                break;
            }
        }
        if (hasPermission) {
            recordVideo();
        } else {
            Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // todo 2.1 视频获取成功，播放视频
        if(requestCode == REQUEST_CODE_RECORD && resultCode == RESULT_OK){
            mVideoView.setVideoPath(mp4Path);
//            saveImageToGallery(SystemRecordActivity.this,new File(mp4Path),SystemCameraActivity.VIDEO);
            mVideoView.start();
        }
    }


    //尝试把文件载入到相册里面，照片能够正常载入了，但是视频还是不太行，就先不用了
    public void saveImageToGallery(Context context, File file, int type) {

        // 其次把文件插入到系统图库
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "IMG_" + timeStamp;
            switch (type) {
                case SystemCameraActivity.PICTURE:
                    fileName += ".jpg";
                    MediaStore.Images.Media.insertImage(context.getContentResolver(),
                            file.getAbsolutePath(), fileName, null);
                    break;
                case SystemCameraActivity.VIDEO:
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
//        localContentValues.put("mime_type", "video/3gp");
//        localContentValues.put("datetaken", Long.valueOf(paramLong));
//        localContentValues.put("date_modified", Long.valueOf(paramLong));
//        localContentValues.put("date_added", Long.valueOf(paramLong));
//        localContentValues.put("_data", paramFile.getAbsolutePath());
//        localContentValues.put("_size", Long.valueOf(paramFile.length()));
//        return localContentValues;
//    }
}