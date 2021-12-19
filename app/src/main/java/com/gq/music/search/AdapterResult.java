package com.gq.music.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gq.music.R;
import com.gq.music.databinding.AdapterSearchResultBinding;
import com.gq.music.songList.DataSong;

import java.util.ArrayList;

public class AdapterResult extends RecyclerView.Adapter<AdapterResult.Holder<AdapterSearchResultBinding>> {
  private Context context;
  private int flag;//0网易 1抖音
  private ArrayList<DataSong> list;
  private FragmentManager fragmentManager;

  public AdapterResult(Context context,ArrayList<DataSong> list,int flag,FragmentManager fragmentManager){
    this.flag = flag;
    this.context = context;
    this.list = list;
    this.fragmentManager = fragmentManager;
  }

  @NonNull
  @Override
  public Holder<AdapterSearchResultBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new Holder<AdapterSearchResultBinding>(LayoutInflater.from(context).inflate(R.layout.adapter_search_result,parent,false));
  }

  @Override
  public void onBindViewHolder(@NonNull Holder<AdapterSearchResultBinding> holder, int position) {
    holder.binding.setSong(list.get(position));
    holder.binding.setHandler(new Handler(fragmentManager));
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  class Holder<AdapterSearchResultBinding> extends RecyclerView.ViewHolder {
    public com.gq.music.databinding.AdapterSearchResultBinding binding;
    public Holder(@NonNull View itemView) {
      super(itemView);
      binding = DataBindingUtil.bind(itemView);
    }
  }
}
