package com.gq.video.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gq.music.databinding.VideoAdapterTimeBinding
import com.gq.video.event.EventTimeChange
import org.greenrobot.eventbus.EventBus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.time.days

class AdapterTime() : RecyclerView.Adapter<AdapterTime.Holder>(){
  
  lateinit var list:IntArray
  var index = -1
  
  init {
    try {
      val endYear = Integer.parseInt(LocalDate.now().toString().substring(0,4))
      val startYear = 2018
      val length = (endYear-startYear+1)*13
      list = IntArray(length)
      for (i in 1..length){
        var remainder = i%13
        var quotient:Int = i/13
        when (remainder) {
          1 -> {
            list[i-1] = endYear-quotient
          }
          0 -> {
            list[i-1] = 1
          }
          else -> {
            list[i-1] = 13-(remainder-1)
          }
        }
      }
    }catch (e:Exception){System.out.println("捕获异常：$e")}
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    return Holder(VideoAdapterTimeBinding.inflate(LayoutInflater.from(parent.context),parent,false))
  }
  
  override fun getItemCount(): Int = list.size
  
  override fun onBindViewHolder(holder: Holder, position: Int) {
    holder.binding.tvNum.text = list[position].toString()
    if (index==position){
      holder.binding.vAlive.visibility = View.VISIBLE
    }else{
      holder.binding.vAlive.visibility = View.INVISIBLE
    }
    holder.itemView.setOnClickListener {
      var year = 0
      var month = 0
      var remainder = (position+1)%13
      if (remainder==1){
        year = list[position]
        month = 0
      }else{
        var quotient = (position+1)/13
        if(remainder!=0){
          year = list[position-(remainder-1)]
        }else{
          year = list[position-12]
        }
        month = list[position]
      }
      EventBus.getDefault().post(EventTimeChange(year,month))
      index = position
      notifyDataSetChanged()
    }
  }
  
  inner class Holder(binding_:VideoAdapterTimeBinding) :RecyclerView.ViewHolder(binding_.root) {
    var binding = binding_
  }
}