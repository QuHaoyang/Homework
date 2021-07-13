package com.example.hw2.recycler;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hw2.R;

public class Viewholder extends RecyclerView.ViewHolder{
    private TextView index;
    private TextView title;
    private TextView hot;
    private View contentView;

    public Viewholder(View v){
        super(v);
        contentView = v;
        index = v.findViewById(R.id.index);
        title = v.findViewById(R.id.title);
        hot = v.findViewById(R.id.hot);
    }

    public void onBind(int pos,TestData data){
        index.setText(new StringBuilder().append(pos).append(". ").toString());
        title.setText(data.title);
        hot.setText(data.hot);
        if(pos < 3){
            index.setTextColor(Color.parseColor("#FFD700"));
        }
        else{
            index.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        if (listener != null) {
            contentView.setOnClickListener(listener);
        }
    }

    public void setOnLongClickListener(View.OnLongClickListener listener) {
        if (listener != null) {
            contentView.setOnLongClickListener(listener);
        }
    }
}