package com.gq.video.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gq.music.databinding.AdapterVideoTagBinding
import com.gq.video.activity.ActivityEdit
import java.util.*
import kotlin.collections.ArrayList

class AdapterVideoTags(val context: Context,val list_tag:ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return Holder(AdapterVideoTagBinding.inflate(LayoutInflater.from(parent.context),parent,false))
  }
  
  override fun getItemCount(): Int = list_tag.size
  
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    var binding = (holder as AdapterVideoTags.Holder).binding
    binding.tvName.text = list_tag[position]
    binding.icDelete.setOnClickListener {
      list_tag.removeAt(position)
//      notifyItemRemoved(position) //接下来的删除会错乱？
      notifyDataSetChanged()
    }
    binding.icEdit.setOnClickListener {
      val intent = Intent(context,ActivityEdit::class.java)
      intent.putExtra("key","编辑标签名")
      intent.putExtra("position",position)
      context.startActivity(intent)
    }
  }
  
  fun onItemMove(fromPosition: Int, toPosition: Int) {
    Collections.swap(list_tag, fromPosition, toPosition)
    notifyItemMoved(fromPosition, toPosition)
  }
  
  inner class Holder(binding:AdapterVideoTagBinding):RecyclerView.ViewHolder(binding.root) {
    var binding:AdapterVideoTagBinding
    init {
      this.binding = binding
    }
  }
}