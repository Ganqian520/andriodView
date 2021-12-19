package com.gq.music.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gq.music.R;

import org.greenrobot.eventbus.EventBus;

class AdapterGuess extends RecyclerView.Adapter<AdapterGuess.Holder> {

  private VM vm;
  private Context context;

  public AdapterGuess(VM vm,Context context){
    this.vm = vm;
    this.context = context;
  }

  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new Holder(LayoutInflater.from(context).inflate(R.layout.adapter_search_guess,parent,false));
  }

  @Override
  public void onBindViewHolder(@NonNull Holder holder, int position) {
    holder.tv_name.setText(vm.list_keyword.get(position));
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        vm.setFlag_state(2);
        vm.keyword = vm.list_keyword.get(position);
        EventBus.getDefault().post(new EventSearchNet());
      }
    });
  }

  @Override
  public int getItemCount() {
    return vm.list_keyword.size();
  }

  class Holder extends RecyclerView.ViewHolder {
    private TextView tv_name;
    public Holder(@NonNull View itemView) {
      super(itemView);
      tv_name = itemView.findViewById(R.id.tv_name);
    }
  }

}
