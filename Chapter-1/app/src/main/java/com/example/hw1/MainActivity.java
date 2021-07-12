package com.example.hw1;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.nfc.Tag;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.util.Log;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sub = findViewById(R.id.submit);
        final TextView name = findViewById(R.id.name);
        final RadioButton male = findViewById(R.id.male);
        final RadioButton female = findViewById(R.id.female);
        final CheckBox friday = findViewById((R.id.Friday));
        final CheckBox saturday = findViewById((R.id.Saturday));
        final CheckBox sunday = findViewById((R.id.Sunday));
        final Switch picture_visible = findViewById(R.id.visible);
        final ImageView picture = findViewById(R.id.picture);

        picture_visible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picture_visible.isChecked()){
                    picture.setVisibility(View.VISIBLE);
                }
                else{
                    picture.setVisibility(View.INVISIBLE);
                }
            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("name",name.getText().toString());
                if(male.isChecked()){
                    Log.d("sex  ","男性");
                }
                if(female.isChecked()){
                    Log.d("sex","女性");
                }
                String time = "";
                if(friday.isChecked()){
                    time = time + "周五";
                }
                if(saturday.isChecked()){
                    time = time + "周六";
                }
                if(sunday.isChecked()){
                    time = time + "周日";
                }
                if(time == ""){
                    time = "无";
                }
                Log.d("time",time);
            }
        });
    }
}
