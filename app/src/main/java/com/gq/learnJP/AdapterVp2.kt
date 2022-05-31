package com.gq.learnJP

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gq.learnJP.room.DataBase
import com.gq.learnJP.room.entity.Word
import com.gq.learnJP.type.Sentence
import com.gq.music.databinding.JplearnAdapterDetailBinding

class AdapterVp2(val context: Context,var list:ArrayList<Sentence>) : RecyclerView.Adapter<AdapterVp2.Holder>() {
  
  class Holder(val binding:JplearnAdapterDetailBinding) : RecyclerView.ViewHolder(binding.root)
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    return Holder(JplearnAdapterDetailBinding.inflate(LayoutInflater.from(context),parent,false))
  }
  
  override fun getItemCount(): Int {
    return list.size
  }
  
  override fun onBindViewHolder(holder: Holder, position: Int) {
    holder.binding.jp.text = list[position].jp
    holder.binding.cn.text = list[position].cn
    holder.binding.source.text = list[position].source
    val list_str = list[position].jp.split("　")
    val list = DataBase.instance.getDao().selectWords(list_str)
    val list_order = ArrayList<Word>()
    for(i in 0..list_str.size-1){
      for(j in 0..list.size-1){
        if(list_str[i]==list[j].jp){
          list_order.add(list[j])
        }
      }
    }
    holder.binding.rcv.adapter = AdapterWord(list_order)
  }
  
}