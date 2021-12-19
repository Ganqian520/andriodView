package com.gq.video

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.gq.music.api.Request
import com.gq.music.databinding.ActivityIndexVideoBinding
import com.gq.music.util.SPutil
import com.gq.music.util.Util
import com.gq.video.activity.ActivityVideoTagsEdit
import com.gq.video.adapter.AdapterTime
import com.gq.video.event.EventTagChange
import com.gq.video.event.EventTimeChange
import com.gq.video.fragment.FragmentVideos
import com.gq.video.service.ServiceFloat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.IOException

class ActivityIndexVideo : AppCompatActivity() {
  
//  var list_video:ArrayList<Video> = ArrayList<Video>()  //全部视频
  var list_tag:ArrayList<String> = ArrayList<String>()
  lateinit var binding:ActivityIndexVideoBinding
  private var isTime = false
  lateinit var conn:ServiceConnection
  var isOpen = true
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    System.out.println("activity create");
    binding = ActivityIndexVideoBinding.inflate(layoutInflater)
    setContentView(binding.root)
    EventBus.getDefault().register(this)
    
    binding.rcvTime.adapter = AdapterTime()
    
    if(!getTagsLocal()) getTags()
    initVp2()
    initHandle()
    Util.tranStatusBar(this);
    initService()
  }
  
  @Subscribe
  fun onTagChange(e:EventTagChange){
    getTagsLocal()
  }
  
  fun initService(){
    conn = object :ServiceConnection{
      override fun onServiceDisconnected(name: ComponentName?) {}
      override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        GD.control = service as ServiceFloat.Control
      }
    }
    val intent = Intent(this,ServiceFloat::class.java)
    bindService(intent,conn, BIND_AUTO_CREATE)
  }
  
  private fun getTagsLocal():Boolean {
    val str = SPutil(baseContext).getString("videoTags")
    if(str == ""){
      return false
    }else{
      list_tag = JSON.parseObject(str,object: TypeReference<ArrayList<String>>(){})
      list_tag.add("待定")
      list_tag.add(0,"全部")
      runOnUiThread { binding.vp2.adapter?.notifyDataSetChanged() }
      return true
    }
  }
  
  private fun getTags() {
    val map = hashMapOf<String,String>(
      "action" to "getVideoTags",
      "uid" to "gq"
    )
    val cb = object :Callback {
      override fun onFailure(call: Call, e: IOException) {
        System.out.println("请求视频标签：$e");
      }
      override fun onResponse(call: Call, response: Response) {
        list_tag.clear()
        var str_res = response.body?.string()
        var jsonObj = JSONObject.parseObject(str_res)
        var jsonArr = jsonObj.getJSONArray("data").getJSONObject(0).getJSONArray("douVideoTags")
        for(item in jsonArr){
          list_tag.add(item as String)
        }
        var str_tags = JSONArray.toJSONString(list_tag)
        SPutil(baseContext).putString("videoTags",str_tags)
        list_tag.add("待定")
        list_tag.add(0,"全部")
        runOnUiThread {
          binding.vp2.adapter?.notifyDataSetChanged()
        }
      }
  
    }
    Request.myPost(map,cb)
  }
  
  private fun initVp2(){
    binding.vp2.adapter = object : FragmentStateAdapter(this){
      override fun getItemCount():Int = list_tag.size
      override fun createFragment(position: Int):Fragment {
        return FragmentVideos(list_tag[position])
      }
    }
    binding.vp2.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
      override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
      }
    })
    binding.tab.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener {
      override fun onTabReselected(tab: TabLayout.Tab?) {}
      override fun onTabUnselected(tab: TabLayout.Tab?) {}
      override fun onTabSelected(tab: TabLayout.Tab?) {
      
      }
    })
    TabLayoutMediator(binding.tab, binding.vp2,
      TabConfigurationStrategy { tab: TabLayout.Tab, position: Int -> tab.text = list_tag[position] }
    ).attach()
  }
  
  private fun initHandle() {
    binding.tvTime.setOnClickListener{
      when (isTime) {
        true -> {
          EventBus.getDefault().post(EventTimeChange(0,0))  //关闭时全部显示
          binding.rcvTime.visibility = View.GONE
          isTime = false
        }
        false -> {
          binding.rcvTime.visibility=View.VISIBLE
          isTime = true
        }
      }
    }
    binding.tvLast.setOnClickListener{
      startActivity(Intent(this, ActivityVideoTagsEdit::class.java))
    }
    binding.tvSwitch.setOnClickListener {
      if (isOpen){
        isOpen = false
        GD.control.closeBink()
        binding.tvSwitch.text = "关"
      }else{
        isOpen = true
        GD.control.openBink()
        binding.tvSwitch.text = "开"
      }
    }
  }
  
  override fun onDestroy() {
    EventBus.getDefault().unregister(this)
    stopService(Intent(this,ServiceFloat::class.java))
    super.onDestroy()
  }
  
}