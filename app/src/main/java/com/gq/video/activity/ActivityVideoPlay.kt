package com.gq.video.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.aitangba.swipeback.SwipeBackActivity
import com.gq.music.databinding.ActivityVideoPlayBinding
import com.gq.music.util.Util
import com.gq.video.adapter.AdapterVp2Play
import com.gq.video.event.EventChange
import com.gq.video.event.EventPlayClose
import org.greenrobot.eventbus.EventBus


class ActivityVideoPlay : SwipeBackActivity() {
  
  lateinit var binding:ActivityVideoPlayBinding
  lateinit var adapterVp2Play:AdapterVp2Play
  var currentIndex = 0
  
  @SuppressLint("ClickableViewAccessibility")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityVideoPlayBinding.inflate(layoutInflater)
    setContentView(binding.root)
    currentIndex = intent.extras?.get("index") as Int
    initByVp2()
    Util.tranStatusBar(this)
  }
  
  fun initByVp2(){
    adapterVp2Play = AdapterVp2Play(this,currentIndex)
    adapterVp2Play.mYegister()
  
    binding.vp2Play.offscreenPageLimit = 1  //滑动后维持只有3页存在
    binding.vp2Play.adapter = adapterVp2Play
    binding.vp2Play.setCurrentItem(currentIndex,false)
    binding.vp2Play.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
      @SuppressLint("CheckResult")
      override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        EventBus.getDefault().post(EventChange(position))
      }
    })
  }
  
  override fun onDestroy() {
    super.onDestroy()
    EventBus.getDefault().post(EventPlayClose())
  }
  
}