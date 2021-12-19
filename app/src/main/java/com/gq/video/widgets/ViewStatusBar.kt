package com.gq.video.widgets

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.gq.video.util.UtilScreen

class ViewStatusBar(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
  
  @SuppressLint("DrawAllocation")
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val utilScreen = UtilScreen(context as Activity)
    setMeasuredDimension(utilScreen.w,utilScreen.sbH)
  }

}