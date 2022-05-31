package com.gq.music.songList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gq.music.R;
import com.gq.music.event.EventAddCustom;
import com.gq.music.event.EventIsPlay;
import com.gq.music.util.Data;
import com.gq.music.util.Icon;
import com.gq.music.util.Util;

import org.greenrobot.eventbus.EventBus;


public class AdapterListSong extends RecyclerView.Adapter<AdapterListSong.MyViewHolder>{
  private final Context myContext;
  private RelativeLayout rcv_out;
  public AdapterListSong(Context context,RelativeLayout rcv_out) {
    this.myContext = context;
    this.rcv_out = rcv_out;
  }
  @NonNull
  @Override
  public AdapterListSong.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new MyViewHolder(LayoutInflater.from(myContext).inflate(R.layout.adapter_rv_items, parent, false));
  }
  @Override
  public void onBindViewHolder(@NonNull AdapterListSong.MyViewHolder holder, int position) {
    holder.text_index.setText(String.valueOf(Data.list.size()-position));
    holder.text_name.setText(Data.list.get(position).name);
    holder.text_author.setText(Data.list.get(position).author);
    int duration = Data.originNode==0 ? Data.list.get(position).duration/1000 : Data.list.get(position).duration;
    holder.tv_duration.setText(Util.transTime(duration));
    holder.ic_add.setOnClickListener(v -> {
      Data.list_custom.add(Data.list.get(position));
      int[] loaction = new int[2];
      v.getLocationOnScreen(loaction);
      EventBus.getDefault().post(new EventAddCustom(loaction,position));
    });
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(Data.index != position){
          Data.index = position;
          Data.isPlay = true;
          EventBus.getDefault().post(new EventIsPlay(true));
          Data.control.play();
        }else{
          if (Data.isPlay) {
            EventBus.getDefault().post(new EventIsPlay(false));
            Data.control.pause();
          }else{
            EventBus.getDefault().post(new EventIsPlay(true));
            Data.control.resume();
          }
          Data.isPlay = !Data.isPlay;
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return Data.list.size();
  }


  class MyViewHolder extends RecyclerView.ViewHolder {
    private TextView text_index;
    private TextView text_name;
    private TextView text_author;
    private Icon ic_add;
    private TextView tv_duration;
    public MyViewHolder(@NonNull View itemView) {
      super(itemView);
      text_index = itemView.findViewById(R.id.text_index);
      text_name = itemView.findViewById(R.id.text_name);
      ic_add = itemView.findViewById(R.id.ic_add);
      text_author = itemView.findViewById(R.id.text_author);
      tv_duration = itemView.findViewById(R.id.tv_duration);
    }
  }

}
