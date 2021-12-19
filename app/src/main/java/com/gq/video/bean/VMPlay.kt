package com.gq.video.bean

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

class VMPlay : BaseObservable() {
  @Bindable
  var a = false //是否显示进度条
   set(value){
     field = value
     notifyPropertyChanged(BR.a)
   }
}