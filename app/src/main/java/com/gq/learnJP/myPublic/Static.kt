package com.gq.learnJP.myPublic

import com.alibaba.fastjson.JSON
import com.gq.learnJP.type.Sentence
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class Static {
  companion object{
    fun post(map:HashMap<String,String>,cb:Callback){
      val url = "https://ca448d14-fda5-4d8f-9279-3f4896d8f854.bspapp.com/learnJP"
      val mediaType: MediaType? = "application/json".toMediaTypeOrNull()
      val body = RequestBody.create(mediaType, JSON.toJSONString(map))
      val okHttpClient = OkHttpClient.Builder().build()
      val request = Request.Builder().post(body).url(url).addHeader("content-type", "application/json").build()
      val call = okHttpClient.newCall(request)
      call.enqueue(cb)
    }
    
    var list:ArrayList<Sentence> = ArrayList()
  }
}