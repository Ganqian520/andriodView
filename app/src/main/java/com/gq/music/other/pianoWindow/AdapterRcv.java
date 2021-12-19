package com.gq.music.other.pianoWindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gq.music.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Map;

public class AdapterRcv extends RecyclerView.Adapter<AdapterRcv.Holder> {

  private Context context;
  private ArrayList<Map> list;

  public AdapterRcv(Context context, ArrayList<Map> list){
    this.context = context;
    this.list = list;
  }

  @NonNull
  @Override
  public AdapterRcv.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new Holder(LayoutInflater.from(context).inflate(R.layout.other_piano_rcv_holder,parent,false));
  }

  @Override
  public void onBindViewHolder(@NonNull AdapterRcv.Holder holder, int position) {
    holder.id_name.setText((String) list.get(position).get("name"));
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Map res = GD.mySQLite.getPiano((String) list.get(position).get("time"),0);  //拿数据库数据，给canvas用
        String str = (String) res.get("song");
        JSONArray arr = JSONArray.parseArray(str);
        GD.song.clear();
        for(int i=0;i<arr.size();i++){
          DataMusic dataMusic = JSONObject.toJavaObject(arr.getJSONObject(i),DataMusic.class);
          GD.song.add(dataMusic);
        }
        EventBus.getDefault().post(list.get(position));
        System.out.println(list.get(0).get("name"));
        System.out.println(list.get(1).get("name"));
      }
    });
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  class Holder extends RecyclerView.ViewHolder {
    private TextView id_name;
    public Holder(@NonNull View itemView) {
      super(itemView);
      this.id_name = itemView.findViewById(R.id.id_name);
    }
  }
}
