package com.example.hw2;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onStart(){
        super.onStart();
        Log.d("tag","main_start");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("tag","main_resume");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("tag","main_pause");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("tag","main_stop");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("tag","main_destroy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("tag","main_create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button test = findViewById(R.id.test);
        Button baidu = findViewById(R.id.baidu);
        Button phone = findViewById(R.id.phone);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PracticeActivity.class);
                startActivity(intent);
            }
        });
        baidu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.baidu.com"));
                startActivity(intent);
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                startActivity(intent);
            }
        });
    }

}