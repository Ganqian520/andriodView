package com.gq.learnJP

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.fastjson.JSONObject
import com.gq.learnJP.myPublic.Static
import com.gq.learnJP.room.DataBase
import com.gq.learnJP.type.Sentence
import com.gq.music.R
import com.gq.music.databinding.ActivitySentenceNamesBinding
import com.gq.music.util.Util

class ActivitySentenceNames : AppCompatActivity() {
  
  lateinit var binding:ActivitySentenceNamesBinding
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivitySentenceNamesBinding.inflate(layoutInflater)
    setContentView(binding.root)
    Util.tranStatusBar(this)
    Static.list = getData()
    val list:ArrayList<String> = ArrayList()
    Static.list.forEach {
      list.add(it.cn)
    }
    val context = this
    binding.rcv.adapter = AdapterCollection(list,this,object :AdapterCollection.Itf{
      override fun click(position: Int) {
        val intent = Intent(context,ActivityDetail::class.java)
        intent.putExtra("collection",list[position])
        intent.putExtra("index",position)
        context.startActivity(intent)
      }
    })
  }
  fun getData():ArrayList<Sentence>{
    val dao = DataBase.instance.getDao()
    val collection: String = intent.getStringExtra("collection")!!
    val res = dao.selectCollection(collection)
    return JSONObject.parseArray(res.sentences, Sentence::class.java) as ArrayList<Sentence>
  }
}