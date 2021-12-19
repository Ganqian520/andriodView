package com.gq.video.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gq.music.R
import com.gq.music.databinding.VideoAdapterVideoBinding
import com.gq.video.activity.ActivityVideoPlay
import com.gq.video.GD
import com.gq.video.room.entities.Video

class AdapterVideosItem(list:ArrayList<Video>) : RecyclerView.Adapter<AdapterVideosItem.Holder>() {
  
  var list:ArrayList<Video>
  lateinit var parent: ViewGroup
  
  init {
    this.list = list
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    this.parent = parent
    return Holder(VideoAdapterVideoBinding.inflate(LayoutInflater.from(parent.context),parent,false))
  }
  
  override fun getItemCount(): Int = list.size
  
  override fun onBindViewHolder(holder: Holder, position: Int) {
    holder.binding.tvDescribe.text = list[position].describe
    
    holder.binding.tvIndex.text = (list.size-position).toString()

    val option = RequestOptions()
      .error(R.mipmap.ic_launcher_round)
//      .override(500,500) //压缩图片，分辨率
//      .transform(RoundedCorners(50))//设置圆角半径
    Glide.with(parent.context.applicationContext)
      .applyDefaultRequestOptions(option)
      .load(list[position].img)
      .into(holder.binding.imgCover)
    holder.itemView.setOnClickListener {
      GD.list_video_show = list
      val intent = Intent(parent.context, ActivityVideoPlay::class.java)
      intent.putExtra("index",position)
      parent.context.startActivity(intent)
    }
  }
  
  inner class Holder(itemView: VideoAdapterVideoBinding) : RecyclerView.ViewHolder(itemView.root) {
    var binding:VideoAdapterVideoBinding = itemView
  }
  
}