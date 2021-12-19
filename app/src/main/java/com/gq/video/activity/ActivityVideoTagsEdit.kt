package com.gq.video.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.gq.music.databinding.ActivityVideoTagsEditBinding
import com.gq.music.util.SPutil
import com.gq.music.util.Util
import com.gq.video.adapter.AdapterVideoTags
import com.gq.video.event.EventTagChange
import com.gq.video.event.EventTagEdit
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.IOException

class ActivityVideoTagsEdit : AppCompatActivity() {
  
  lateinit var binding:ActivityVideoTagsEditBinding
  var list_tag:ArrayList<String> = ArrayList<String>()
  lateinit var adapter:AdapterVideoTags
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityVideoTagsEditBinding.inflate(LayoutInflater.from(this))
    setContentView(binding.root)
    Util.tranStatusBar(this)
    EventBus.getDefault().register(this)
    getTags()
    bindHandle()
    initItemHelper()
  }
  
  @Subscribe
  fun onTagEdit(e: EventTagEdit){
    runOnUiThread {
      list_tag[e.position] = e.name
      adapter.notifyItemChanged(e.position)
    }
  }
  
  fun initItemHelper(){
    val cb = object :ItemTouchHelper.Callback(){
      override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val moveFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlag = ItemTouchHelper.LEFT
        return makeMovementFlags(moveFlag,swipeFlag)
      }
      override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        adapter.onItemMove(viewHolder.bindingAdapterPosition,target.bindingAdapterPosition)
        return true
      }
      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
      
      }
    }
    ItemTouchHelper(cb).attachToRecyclerView(binding.listTag)
  }
  
  fun bindHandle(){
    adapter = AdapterVideoTags(this,list_tag)
    binding.listTag.adapter = adapter
    binding.tvBack.setOnClickListener { finish() }
    binding.tvAdd.setOnClickListener {
      list_tag.add(0,"未命名")
      adapter.notifyItemInserted(0)
    }
    binding.tvOk.setOnClickListener {
      val str = JSON.toJSONString(list_tag)
      updateTag(str)
    }
  }
  
  fun updateTag(tags:String){
    val map = hashMapOf<String,String>(
      "uid" to "gq",
      "action" to "editVideoTag",
      "douVideoTags" to tags
    )
    val cb = object :Callback{
      override fun onFailure(call: Call, e: IOException) {
        System.out.println("更新标签请求失败：$e");
      }
      override fun onResponse(call: Call, response: Response) {
        val res_str = response.body!!.string()
        val res = JSONObject.parseObject(res_str)
        if(res.getString("message")=="成功"){
          SPutil(baseContext).putString("videoTags",tags)
          EventBus.getDefault().post(EventTagChange())
          runOnUiThread{
            Toast.makeText(baseContext,"更新成功",Toast.LENGTH_LONG).show()
          }
        }
      }
    }
    com.gq.music.api.Request.myPost(map,cb)
  }
  
  fun getTags(){
    val str = SPutil(baseContext).getString("videoTags")
    list_tag = JSON.parseObject(str,object: TypeReference<ArrayList<String>>(){})
  }
  
  override fun onDestroy() {
    EventBus.getDefault().unregister(this)
    super.onDestroy()
  }
}