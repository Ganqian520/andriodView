package com.gq.video.util

import android.os.Handler
import android.view.View

class MyClick(cb:MyClick.CB) : View.OnClickListener {
  
  var handler:Handler
  var count = 0
  var cb:MyClick.CB
  
  init {
    this.cb = cb
    handler = Handler()
  }
  
  interface CB {
    fun singleClick()
    fun doubleClick()
  }
  
  override fun onClick(v: View?) {
    count++
    handler.postDelayed(object :Runnable{
      override fun run() {
        if(count==1){
          cb.singleClick()
        }else{
          cb.doubleClick()
        }
        handler.removeCallbacksAndMessages(null)
        count = 0
      }
    },300)
  }
}