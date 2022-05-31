package com.gq.learnJP

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gq.music.databinding.JplearnAdapterCollectionBinding

class AdapterCollection(var list:ArrayList<String>,var context: Context, var itf: Itf) : RecyclerView.Adapter<AdapterCollection.Holder>() {
  
  class Holder(var binding:JplearnAdapterCollectionBinding) : RecyclerView.ViewHolder(binding.root)
  
  interface Itf{
    fun click(position: Int)
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    return Holder(JplearnAdapterCollectionBinding.inflate(LayoutInflater.from(parent.context),parent,false))
  }
  
  override fun getItemCount(): Int {
    return list.size
  }
  
  override fun onBindViewHolder(holder: Holder, position: Int) {
    holder.binding.index.text = "${position+1}"
    holder.binding.name.text = list[position]
    holder.binding.root.setOnClickListener {
//      val intent = Intent(context,ActivityDetail::class.java)
//      intent.putExtra("collection",list[position])
//      context.startActivity(intent)
      itf.click(position)
    }
  }
}