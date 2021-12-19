package com.gq.video

import com.gq.video.room.entities.Video
import com.gq.video.service.ServiceFloat

class GD {
  companion object {
    var list_video:ArrayList<Video> = ArrayList<Video>()
    var list_video_show:ArrayList<Video> = ArrayList<Video>()
//    var list_tag:ArrayList<String> = ArrayList<String>()
    lateinit var control: ServiceFloat.Control  //浮窗开关,只有关掉交点浮窗时才能打开键盘
  }
}