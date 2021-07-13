package com.example.hw2.recycler;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hw2.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Collections;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<TestData> mDataset = new ArrayList<>();
    private IOnItemClickListener mItemClickListener;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.onBind(position, mDataset.get(position));
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLick(position, mDataset.get(position));
                }
            }
        });
        holder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemLongCLick(position, mDataset.get(position));
                }
                return false;
            }

        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface IOnItemClickListener {

        void onItemCLick(int position, TestData data);

        void onItemLongCLick(int position, TestData data);
    }

    public MyAdapter(List<TestData> myDataset) {
        mDataset.addAll(myDataset);
    }

    public void setOnItemClickListener(IOnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void addData(int position, TestData data) {
        mDataset.add(position, data);
        notifyItemInserted(position);
        if (position != mDataset.size()) {
            //刷新改变位置item下方的所有Item的位置,避免索引错乱
            notifyItemRangeChanged(position, mDataset.size() - position);
        }
    }

    public void removeData(int position) {
        if (null != mDataset && mDataset.size() > position) {
            mDataset.remove(position);
            notifyItemRemoved(position);
            if (position != mDataset.size()) {
                //刷新改变位置item下方的所有Item的位置,避免索引错乱
                notifyItemRangeChanged(position, mDataset.size() - position);
            }
        }
    }

    public void addnum(int pos,int num){
        if(mDataset.size() > pos){
            TestData tmp = mDataset.get(pos);
            tmp.hot = (Integer.parseInt(tmp.hot)+num)+"";
            mDataset.set(pos,tmp);
            int i = pos;
            if(num > 0){
                while (i > 0){
                    if(Integer.parseInt(mDataset.get(i-1).hot) < Integer.parseInt(mDataset.get(i).hot)){
                        tmp = mDataset.get(i);
                        mDataset.set(i,mDataset.get(i-1));
                        mDataset.set(i-1,tmp);
                        i = i - 1;
                    }
                    else{
                        break;
                    }
                }
            }
            else if(num < 0){
                while (i < mDataset.size()-1){
                    if(Integer.parseInt(mDataset.get(i).hot) < Integer.parseInt(mDataset.get(i+1).hot)){
                        tmp = mDataset.get(i);
                        mDataset.set(i,mDataset.get(i+1));
                        mDataset.set(i+1,tmp);
                        i = i + 1;
                    }
                    else{
                        break;
                    }
                }
            }
            notifyItemRangeChanged(0, mDataset.size());
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView index;
        private TextView title;
        private TextView hot;
        private View contentView;


        public MyViewHolder(View v) {
            super(v);
            contentView = v;
            index = v.findViewById(R.id.index);
            title = v.findViewById(R.id.title);
            hot = v.findViewById(R.id.hot);
        }

        public void onBind(int position, TestData data) {
            index.setText(new StringBuilder().append(position).append(".  ").toString());
            title.setText(data.title);
            hot.setText(data.hot);
            if (position < 3) {
                index.setTextColor(Color.parseColor("#FFD700"));
            } else {
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
}
