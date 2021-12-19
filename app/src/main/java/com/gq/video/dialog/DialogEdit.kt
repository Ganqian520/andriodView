package com.gq.video.dialog

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.gq.music.R
import com.gq.music.databinding.VideoDialogEditBinding
import com.gq.music.util.Data
import com.gq.video.util.UtilScreen

class DialogEdit : DialogFragment() {
  
  lateinit var binding: VideoDialogEditBinding
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_TITLE, R.style.DialogCollect)
  }
  
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = VideoDialogEditBinding.inflate(inflater)
    initHandle()
    return binding.root
  }
  
  fun initHandle(){

  }
  
  override fun onStart() {
    super.onStart()
    val window = dialog!!.window
    val lp = window!!.attributes
    lp.width = UtilScreen(context as Activity).w
    lp.height = UtilScreen(context as Activity).h/2
    lp.gravity = Gravity.BOTTOM or Gravity.CENTER
    window.attributes = lp
  }
}