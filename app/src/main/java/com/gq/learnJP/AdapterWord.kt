package com.gq.learnJP

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gq.learnJP.room.entity.Word
import com.gq.music.databinding.JplearnAdapterWordBinding

class AdapterWord(val list:ArrayList<Word>) : RecyclerView.Adapter<AdapterWord.Holder>() {
  
  class Holder(val binding:JplearnAdapterWordBinding) : RecyclerView.ViewHolder(binding.root)
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    return Holder(JplearnAdapterWordBinding.inflate(LayoutInflater.from(parent.context),parent,false))
  }
  
  override fun getItemCount(): Int {
    return list.size
  }
  
  override fun onBindViewHolder(holder: Holder, position: Int) {
    holder.binding.index.text = "${position + 1}"
    holder.binding.jm.text = list[position].jm
    holder.binding.jp.text = list[position].jp
    holder.binding.cn.text = list[position].cn
  }
}