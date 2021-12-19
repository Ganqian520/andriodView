package com.gq.video.util

import android.app.Activity

class UtilScreen(activity: Activity) {
  var h:Int = 0;
  var w:Int = 0;
  var sbH:Int = 0;
  
  init {
    val resources = activity.resources
    sbH = resources.getDimensionPixelSize(
      resources.getIdentifier("status_bar_height", "dimen", "android"))
    val displayMetrics = resources.displayMetrics
    h = displayMetrics.heightPixels
    w = displayMetrics.widthPixels
  }
}