package com.gq.music.play;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gq.music.R;
import com.gq.music.util.Data;

import java.util.ArrayList;

public class AdapterRecLyric extends RecyclerView.Adapter<AdapterRecLyric.MyViewHolder>{

  private Context context;
  private ArrayList<DataLyric> list;

  public AdapterRecLyric(Context context,ArrayList<DataLyric> list){
    this.context = context;
    this.list = list; //这里传的是地址不是值
  }
  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new AdapterRecLyric.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_lyric, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    holder.lyric.setText(list.get(position).content);
    if(position== Data.currentLyric){
      holder.lyric.setTextColor(Color.WHITE);
    }
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  class MyViewHolder extends RecyclerView.ViewHolder {
    private TextView lyric;
    public MyViewHolder(@NonNull View itemView) {
      super(itemView);
      lyric = itemView.findViewById(R.id.tv_lyric);
    }
  }
}
