package com.gq.video.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

class UtilTime {
  
  companion object{
    @SuppressLint("SimpleDateFormat")
    fun getDate(timeStamp:Long):Map<String,Int>{
      val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      val str = format.format(timeStamp*1000)
      val map = hashMapOf<String,Int>(
        "year" to str.substring(0,4).toInt(),
        "month" to str.substring(5,7).toInt()
      )
      return map
    }
  }

}