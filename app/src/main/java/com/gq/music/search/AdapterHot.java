package com.gq.music.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gq.music.R;

import java.util.ArrayList;

public class AdapterHot extends BaseAdapter {

  private ArrayList<String> list;

  private Context context;

  public AdapterHot(Context context,ArrayList<String> list){
    this.context = context;
    this.list = list;
  }

  @Override
  public int getCount() {
    return list.size();
  }

  @Override
  public Object getItem(int position) {
    return list.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @SuppressLint("ViewHolder")
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Holder holder = null;
    if(convertView==null){
      convertView = LayoutInflater.from(context).inflate(R.layout.adapter_search_hot,parent,false);
      holder = new Holder();
      holder.tv_index = convertView.findViewById(R.id.tv_index);
      holder.tv_name = convertView.findViewById(R.id.tv_name);
      convertView.setTag(holder);
    }else{
      holder = (Holder) convertView.getTag();
    }
    holder.tv_index.setText(""+(position+1));
    holder.tv_name.setText(list.get(position));
    return convertView;
  }

  class Holder {
    private TextView tv_index;
    private TextView tv_name;
  }
}
