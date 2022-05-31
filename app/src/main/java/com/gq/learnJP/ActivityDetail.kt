package com.gq.learnJP

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.fastjson.JSONObject
import com.gq.learnJP.myPublic.Static
import com.gq.learnJP.room.DataBase
import com.gq.learnJP.type.Sentence
import com.gq.music.databinding.ActivityDetail2Binding
import com.gq.music.util.Util

class ActivityDetail : AppCompatActivity() {
  lateinit var binding:ActivityDetail2Binding
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityDetail2Binding.inflate(layoutInflater)
    setContentView(binding.root)
    Util.tranStatusBar(this)
    binding.vp2.adapter = AdapterVp2(this,Static.list)
    binding.vp2.setCurrentItem(intent.getIntExtra("index",0),false)
  }
}