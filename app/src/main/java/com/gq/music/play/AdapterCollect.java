package com.gq.music.play;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gq.music.R;
import com.gq.music.api.Request;
import com.gq.music.songsMenu.DataSongs;
import com.gq.music.util.Data;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AdapterCollect extends RecyclerView.Adapter<AdapterCollect.VH> {

  private Context context;
  private Activity activity;
  private ArrayList<DataSongs> list;

  public AdapterCollect(Context context,ArrayList<DataSongs> list,Activity activity){
    this.context = context;
    this.list = list;
    this.activity = activity;
  }

  @NonNull
  @Override
  public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.adapter_collect,parent,false);
    return new VH(view);
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  @Override
  public void onBindViewHolder(@NonNull VH holder, int position) {
    holder.tv_name.setText(list.get(position).name);
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        collectSong(Data.dataSong.id,list.get(position).id);
      }
    });
  }

  void collectSong(String songId,String songsId){
//    /playlist/tracks?op=add&pid=24381616&tracks=347231
    String url = Data.baseUrl+"/playlist/tracks?op=add&pid="+songsId+"&tracks="+songId;
    Callback cb = new Callback() {
      @Override
      public void onFailure(@NotNull Call call, @NotNull IOException e) {
        System.out.println("收藏请求："+e);
      }
      @Override
      public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        try{
          String str_res = response.body().string();
          System.out.println(""+str_res);
          JSONObject jobj = JSON.parseObject(str_res);
          int code = jobj.getJSONObject("body").getInteger("code");
          activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
              if(code==200){
                Toast.makeText(context,"添加歌单成功",Toast.LENGTH_LONG).show();
              }else if(code==502){
                Toast.makeText(context,"添加重复",Toast.LENGTH_LONG).show();
              }
            }
          });
        }catch(Exception e){System.out.println("异常："+e);}
      }
    };
    Request.get(url,cb);
  }

  public void onItemMove(int fromPosition, int toPosition){
    Collections.swap(list,fromPosition,toPosition);
    notifyItemMoved(fromPosition,toPosition);
  }

  class VH extends RecyclerView.ViewHolder {
    private TextView tv_name;
    public VH(@NonNull View itemView) {
      super(itemView);
      tv_name = itemView.findViewById(R.id.tv_name);
    }
  }
}
