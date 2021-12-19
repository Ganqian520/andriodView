package com.gq.video.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.alibaba.fastjson.JSONArray
import com.gq.music.api.Request
import com.gq.music.databinding.FragmentVideosBinding
import com.gq.music.util.SPutil
import com.gq.video.GD
import com.gq.video.adapter.AdapterVideosItem
import com.gq.video.event.EventTimeChange
import com.gq.video.room.dataBase.DataBase
import com.gq.video.room.entities.Video
import com.gq.video.util.UtilTime
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.IOException


class FragmentVideos(val tag_:String) : Fragment() {
  
  lateinit var binding: FragmentVideosBinding
  var list_video:ArrayList<Video> = ArrayList()
  
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    binding = FragmentVideosBinding.inflate(layoutInflater)
    binding.rcvVideos.adapter = AdapterVideosItem(list_video)
    binding.fresh.setOnRefreshListener { getVideosLocal() }
    return binding.root
  }
  
  override fun onResume() {
    super.onResume()
    System.out.println("resume,${list_video.size}");
    EventBus.getDefault().register(this)
    if(list_video.size==0){
      if(GD.list_video.size==0){
        getVideosLocal()
      }else{
        getVideoShow()
      }
    }
  }
  
  @Subscribe
  fun onTimeChange(e: EventTimeChange){
    getVideoByTime(e)
  }
  
  fun getVideoByTime(e:EventTimeChange){
    list_video.clear()
    if (tag_=="全部"){
      if(e.month==0&&e.year!=0){
        GD.list_video.forEach {
          if (e.year==UtilTime.getDate(it.createTime)["year"]){
            list_video.add(it)
          }
        }
      }else if (e.month!=0){
        GD.list_video.forEach {
          if(e.year==UtilTime.getDate(it.createTime)["year"] && e.month==UtilTime.getDate(it.createTime)["month"]){
            list_video.add(it)
          }
        }
      }else{
        GD.list_video.forEach {
          list_video.add(it)
        }
      }
    }else if(tag_=="待定"){
      if(e.month==0&&e.year!=0){
        GD.list_video.forEach {
          if (tag_==""&&e.year==UtilTime.getDate(it.createTime)["year"]){
            list_video.add(it)
          }
        }
      }else if (e.month!=0){
        GD.list_video.forEach {
          if(tag_==""&&e.year==UtilTime.getDate(it.createTime)["year"] && e.month==UtilTime.getDate(it.createTime)["month"]){
            list_video.add(it)
          }
        }
      }else{
        GD.list_video.forEach {
          if (tag_==""){
            list_video.add(it)
          }
        }
      }
    }else{
      if(e.month==0&&e.year!=0){
        GD.list_video.forEach {
          if (tag_==it.tag&&e.year==UtilTime.getDate(it.createTime)["year"]){
            list_video.add(it)
          }
        }
      }else if (e.month!=0){
        GD.list_video.forEach {
          if(tag_==it.tag&&e.year==UtilTime.getDate(it.createTime)["year"] && e.month==UtilTime.getDate(it.createTime)["month"]){
            list_video.add(it)
          }
        }
      }else{
        GD.list_video.forEach {
          if (tag_==it.tag){
            list_video.add(it)
          }
        }
      }
    }
    activity?.runOnUiThread{binding.rcvVideos.adapter?.notifyDataSetChanged()}
  }
  
  fun getVideoCount(){
    val map = hashMapOf(
      "uid" to "gq",
      "action" to "getVideoCount"
    )
    val cb = object :Callback {
      override fun onFailure(call: Call, e: IOException) {
      
      }
      override fun onResponse(call: Call, response: Response) {
//        sys
      }
  
    }
  }
  
  private fun getVideosLocal():Boolean {
    val dao = DataBase.instance.getDaoVideo()
    GD.list_video = dao.selectVideoAll() as ArrayList<Video>
    getVideoShow()
    Toast.makeText(context,"${GD.list_video.size}",Toast.LENGTH_LONG).show()
    binding.fresh.isRefreshing = false
    return if (GD.list_video.size==0) false else true
  }
  
  private fun getVideos() {
    var map = hashMapOf<String,String>(
      "action" to "getVideo",
      "uid" to "gq"
    )
    var cb = object : Callback {
      override fun onFailure(call: Call, e: IOException) {
        System.out.println("请求视频失败：$e");
      }
      override fun onResponse(call: Call, response: Response) {
//        GD.list_video.clear()
//        var str = response.body?.string()
//        var arr = JSONObject.parseObject(str).getJSONArray("data")
//        for(i in 0..arr.size-1){
//          var dataVideo = DataVideo()
//          var item = arr.getJSONObject(i)
//          dataVideo.describe = item.getString("describe")
//          dataVideo.tag = item.getString("tag")
//          dataVideo.duration = item.getJSONObject("res").getIntValue("videoDuration")/1000
//          dataVideo.url = item.getJSONObject("res").getString("videoUrl")
//          dataVideo.img = item.getJSONObject("res").getString("coverUrl")
//          dataVideo.id = item.getJSONObject("res").getString("id")
//          dataVideo.desc = item.getJSONObject("res").getString("desc")
//          dataVideo.createTime = item.getJSONObject("res").getLong("createTime")
//          GD.list_video.add(dataVideo)
//        }
        var str_ = JSONArray.toJSONString(GD.list_video)
        getVideoShow()
        SPutil(context).putString("douVideos",str_)
        activity?.runOnUiThread {
          binding.rcvVideos.adapter?.notifyDataSetChanged()
          binding.fresh.isRefreshing = false
        }
      }
    }
    Request.myPost(map,cb)
  }
  
  fun getVideoShow(){
    list_video.clear()
    if (tag_=="全部"){
      GD.list_video.forEach {
        list_video.add(it)
      }
    }else if (tag_=="待定"){
      GD.list_video.forEach {
        if(it.tag==""){
          list_video.add(it)
        }
      }
    }else {
      GD.list_video.forEach {
        if(tag_==it.tag){
          list_video.add(it)
        }
      }
    }
    binding.rcvVideos.adapter?.notifyDataSetChanged()
  }
  
  override fun onPause() {
    super.onPause()
    System.out.println("pause");
    EventBus.getDefault().unregister(this)
  }
}