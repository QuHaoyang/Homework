package com.example.chapter_7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends Activity {
    String mockUrl = "https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4";
    private long lastClick;
    private long timeBound = 1000;
    private long currentTime;
    private VideoView videoView;
    private static int oldTime = 0;
    private static String oldUrl = "https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4";
//    private static String oldUrl = "content://media/external/video/media/62741";
    private String currentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration mConfiguration = this.getResources().getConfiguration();
        Context mContext = this;
        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        int ori = mConfiguration.orientation;
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//            //横
        }
        else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            setTheme(android.R.style.Theme_Black);
//            //竖
        }
        setContentView(R.layout.activity_video);

        videoView = (VideoView)findViewById(R.id.vv_detail);

        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            currentUrl = uri.toString();
        }
        else{
            currentUrl = oldUrl;
        }
        videoView.setVideoURI(Uri.parse(currentUrl));



//        videoView.setMediaController(new MediaController(this));
//        videoView.resume();
//        videoView.seekTo(oldTime);
//        videoView.start();



        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.setMediaController(new MediaController(VideoActivity.this));
                if(currentUrl == oldUrl){
                    videoView.seekTo(oldTime);
                }
                videoView.start();
            }
        });

        lastClick = System.currentTimeMillis();


        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTime = System.currentTimeMillis();
                oldTime = videoView.getCurrentPosition();
                Log.d("tag123123",currentTime+" , "+lastClick+" , "+oldTime);
                if(currentTime - lastClick < timeBound){
                    if(videoView.isPlaying()){
                        videoView.pause();
                    }
                    else{
                        videoView.start();
                    }
                }
                lastClick = currentTime;
            }
        });

    }

    @Override
    protected void onPause(){
        oldTime = videoView.getCurrentPosition();
        Log.d("tag123123"," , "+oldTime);
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        oldUrl = currentUrl;
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        Configuration mConfiguration = this.getResources().getConfiguration();
        int ori = mConfiguration.orientation;
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//            //横
        }
        else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            setTheme(android.R.style.Theme_Black);
//            //竖
        }
        super.onConfigurationChanged(newConfig);
    }


}