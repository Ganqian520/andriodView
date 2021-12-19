package com.gq.video.viewModel

import androidx.lifecycle.ViewModel
import com.gq.video.room.entities.Video

class ViewModelVideo : ViewModel() {
  var index:Int = 0 //当前序号
  lateinit var list_video:ArrayList<Video>  //全部视频
  var list_tag:ArrayList<String> = arrayListOf("1","2","3","4","5","6")
  
}