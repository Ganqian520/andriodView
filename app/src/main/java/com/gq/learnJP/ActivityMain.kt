package com.gq.learnJP

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.gq.learnJP.room.DataBase
import com.gq.learnJP.myPublic.Static
import com.gq.learnJP.room.entity.MyCollection
import com.gq.learnJP.room.entity.Word
import com.gq.music.databinding.ActivityMain2Binding
import com.gq.music.util.Util
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class ActivityMain : AppCompatActivity() {
  
  lateinit var binding:ActivityMain2Binding
  lateinit var context: Context
  val dao = DataBase.instance.getDao()
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMain2Binding.inflate(layoutInflater)
    setContentView(binding.root)
    Util.tranStatusBar(this)
    context = this
    initBinding()
    getData()
  }
  fun initBinding(){
    binding.freshWord.setOnClickListener { getNetWords() }
    binding.freshSentence.setOnClickListener { getNetSentence() }
  }
  fun getData(){
    var res = dao.selectCollectionNames()
    val list = res as ArrayList<String>
    binding.rv1.adapter = AdapterCollection(list,this,object :AdapterCollection.Itf{
      override fun click(position: Int) {
        val intent = Intent(context,ActivitySentenceNames::class.java)
        intent.putExtra("collection",list[position])
        context.startActivity(intent)
      }
    })
  }
  fun getNetSentence(){
    binding.freshSentence.visibility = View.GONE
    binding.pbSentence.visibility = View.VISIBLE
    dao.clearMyCollection()
    val map = hashMapOf<String,String>(
      "action" to "getAllCollection"
    )
    val cb = object : Callback {
      override fun onFailure(call: Call, e: IOException) {
        System.out.println(e);
      }
      override fun onResponse(call: Call, response: Response) {
        val str = response.body?.string()
        val resobj:JSONObject = JSON.parseObject(str)
        val arr:JSONArray = resobj.getJSONArray("data")
        val myCollections = ArrayList<MyCollection>()
        for(i in 0..arr.size-1){
          val obj = arr[i] as JSONObject
          val myCollection = MyCollection(0,obj.getString("collection"),obj.getString("sentences"))
          myCollections.add(myCollection)
        }
        dao.insertMyCollection(myCollections)
        runOnUiThread {
          Toast.makeText(context,"更新成功，句集共${myCollections.size}条",Toast.LENGTH_LONG).show()
          binding.freshSentence.visibility = View.VISIBLE
          binding.pbSentence.visibility = View.GONE
          getData()
        }
      }
    }
    Static.post(map,cb)
  }
  fun getNetWords(){
    binding.freshWord.visibility = View.GONE
    binding.pbWord.visibility = View.VISIBLE
    dao.clearWords()
    val map = hashMapOf<String,String>(
      "action" to "getAllWords"
    )
    val cb = object : Callback {
      override fun onFailure(call: Call, e: IOException) {
        System.out.println(e);
      }
      override fun onResponse(call: Call, response: Response) {
        val str = response.body?.string()
        val resobj:JSONObject = JSON.parseObject(str)
        val arr:JSONArray = resobj.getJSONArray("data")
        val words = ArrayList<Word>()
        for(i in 0..arr.size-1){
          val obj = arr[i] as JSONObject
          val word:Word = Word(0,obj.getString("jp"),obj.getString("cn"),obj.getString("jm"))
          words.add(word)
        }
        dao.insertWords(words)
        runOnUiThread {
          Toast.makeText(context,"更新成功，单词共${words.size}条",Toast.LENGTH_LONG).show()
          getData()
          binding.freshWord.visibility = View.VISIBLE
          binding.pbWord.visibility = View.GONE
        }
      }
    }
    Static.post(map,cb)
  }
}