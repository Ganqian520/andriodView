package com.gq.video.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import com.gq.music.R
import com.gq.music.databinding.ActivityEditBinding
import com.gq.video.GD
import com.gq.video.event.EventTagEdit
import org.greenrobot.eventbus.EventBus

class ActivityEdit : AppCompatActivity() {
  lateinit var name:String
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_edit)
    var binding = ActivityEditBinding.inflate(LayoutInflater.from(this))
    setContentView(binding.root)
    GD.control.closeBink()
    var key = intent.extras?.get("key")?.toString()
    binding.tvTitle.text = key
    if(key=="编辑标签名"){
      binding.tvOk.setOnClickListener {
        EventBus.getDefault().post(EventTagEdit(name,intent.extras!!.get("position") as Int))
        finish()
      }
    }
    binding.icBack.setOnClickListener { finish() }
    
//    binding.et.requestFocus()
//    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//    imm.showSoftInput(binding.et,0)
    binding.et.addTextChangedListener(object :TextWatcher{
      override fun afterTextChanged(s: Editable?) {
      
      }
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      
      }
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        name = s.toString()
      }
    })
  }
  
  override fun onDestroy() {
    super.onDestroy()
    GD.control.openBink()
    System.out.println("ac destroy");
  }
}