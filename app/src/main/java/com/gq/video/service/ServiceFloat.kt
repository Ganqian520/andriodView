package com.gq.video.service

import android.app.Activity
import android.app.Service
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.View.generateViewId
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.gq.music.api.Request
import com.gq.music.databinding.VideoFloatAddBinding
import com.gq.music.util.SPutil
import com.gq.video.GD
import com.gq.video.room.dataBase.DataBase
import com.gq.video.room.entities.Video
import com.gq.video.util.UtilScreen
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


class ServiceFloat: Service() {
  
  private lateinit var windowManager: WindowManager
  private val lpSquare: WindowManager.LayoutParams = WindowManager.LayoutParams()
  var lpBink = WindowManager.LayoutParams()
  private lateinit var clipboardManager: ClipboardManager
  private lateinit var clipChangedListener: ClipboardManager.OnPrimaryClipChangedListener
  private var describe:String = ""
  private var tag:String = ""
  private var share = ""
  private lateinit var binding: VideoFloatAddBinding
  lateinit var tv:TextView
  var list_tag:ArrayList<String> = ArrayList<String>()
  
  override fun onCreate() {
    super.onCreate()
    System.out.println("服务 create");
    initLayout()
    initHandle()
    initClipboard()
//    windowManager.addView(binding.root,lpSquare)
    windowManager.addView(tv,lpBink)
  }
  
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    var flags: Int
    flags = START_STICKY
    return super.onStartCommand(intent, flags, startId)
  }
  
  private fun initClipboard(){
    clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    clipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
      if (clipboardManager.hasPrimaryClip() && clipboardManager.primaryClip?.itemCount!! > 0) {
        System.out.println("监听剪切板");
        share = clipboardManager.primaryClip!!.getItemAt(0).text as String
        if (binding.rcv.adapter==null) initRcv()
        windowManager.addView(binding.root,lpSquare)
      }
    }
    clipboardManager.addPrimaryClipChangedListener(clipChangedListener)
  }
  
  private fun initLayout(){
    windowManager = application.getSystemService(WINDOW_SERVICE) as WindowManager
    binding = VideoFloatAddBinding.inflate(LayoutInflater.from(baseContext))
    lpSquare.format = PixelFormat.TRANSPARENT
    lpSquare.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    lpSquare.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
    lpSquare.width = 900
    lpSquare.height = 1000

    lpBink = WindowManager.LayoutParams()
    lpBink.format = PixelFormat.TRANSPARENT
    lpBink.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    lpBink.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
    lpBink.width = 1
    lpBink.height = 1
    lpBink.gravity = Gravity.TOP
    tv = TextView(baseContext)
  }
  
  private fun initRcv(){
    getTags()
    binding.rcv.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
      private var indexLive = -1
      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var tv_tag = TextView(baseContext)
        tv_tag.height = 140
        tv_tag.setTextColor((0xffffffff).toInt())
        tv_tag.gravity = Gravity.CENTER
        var lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER_VERTICAL
        tv_tag.layoutParams = lp
        tv_tag.id = generateViewId()
        return object :RecyclerView.ViewHolder(tv_tag){}
      }
      override fun getItemCount(): Int = list_tag.size
      override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var tv = holder.itemView as TextView
        tv.text = list_tag[position]
        if(position==indexLive) tv.textSize = 20f else tv.textSize = 15f
        tv.setOnClickListener{
          indexLive = position
          tag = list_tag[position]
          if(position==list_tag.size-1) tag = ""
          notifyDataSetChanged()
        }
      }
    }
  }
  
  fun getTags(){
    val str = SPutil(baseContext).getString("videoTags")
    list_tag = JSON.parseObject(str,object: TypeReference<ArrayList<String>>(){})
    list_tag.add("待定")
  }
  
  private fun initHandle(){
    binding.tvCancel.setOnClickListener{
      System.out.println("cancle");
      windowManager.removeView(binding.root)
    }
    binding.tvOk.setOnClickListener{
      add()
    }
    binding.etDescribe.addTextChangedListener(object :TextWatcher {
      override fun afterTextChanged(s: Editable?) {}
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        describe = s.toString()
      }
    })
  }
  
  private fun add(){
    binding.tvOk.visibility = View.GONE
    binding.pb.visibility = View.VISIBLE
    var map = hashMapOf<String,String>(
      "action" to "addVideo",
      "uid" to "gq",
      "share" to share,
      "describe" to  describe,
      "tag" to tag
    )
    val cb = object :Callback {
      override fun onFailure(call: Call, e: IOException) {
        System.out.println("请求添加异常：$e");
        binding.tvOk.post { binding.tvOk.visibility = View.VISIBLE }
        binding.pb.post { binding.pb.visibility = View.GONE }
      }
      override fun onResponse(call: Call, response: Response) {
        val str = response.body?.string()
        System.out.println(str);
        val res = JSONObject.parseObject(str)
        if (res?.get("message")=="添加成功") {
          saveLocal(res.getJSONObject("api"))
          binding.tvOk.post { binding.tvOk.visibility = View.VISIBLE }
          binding.pb.post { binding.pb.visibility = View.GONE }
          windowManager.removeView(binding.root)
        }else{
          System.out.println("云函数执行失败:$res");
          binding.tvOk.post { binding.tvOk.visibility = View.VISIBLE }
          binding.pb.post { binding.pb.visibility = View.GONE }
        }
      }
    }
    Request.myPost(map,cb)
  }
  
  fun saveLocal(res:JSONObject){
    val dao = DataBase.instance.getDaoVideo()
    val video:Video = Video(
      0,
      res.getLong("createTime"),
      res.getString("id"),
      res.getString("videoUrl"),
      res.getString("coverUrl"),
      res.getString("desc"),
      res.getIntValue("videoDuration")/1000,
      describe,
      tag
    )
    dao.insertVideo(video)
    System.out.println("添加数据库成功");
  }
  
  override fun onDestroy() {
    System.out.println("服务关闭");
    clipboardManager.removePrimaryClipChangedListener(clipChangedListener)
    super.onDestroy()
  }
  
  override fun onBind(intent: Intent?): IBinder? {
    return Control()
  }
  
  inner class Control: Binder() {
    fun openBink(){
      windowManager.addView(tv,lpBink)
    }
    fun closeBink(){
      windowManager.removeView(tv)
    }
  }
  
}