package com.gq.video.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gq.music.R

class LinearLayoutFresh(context: Context, attrs: AttributeSet) :LinearLayout(context,attrs) {
  
  
  var header: View //头部
  lateinit var rcv: RecyclerView  //中间Recyclerview
  var maxTop = 600 //最大下拉距离
  var h_header = 300  //下拉超过header高度就响应刷新
  var offsetY = 0f //整体偏移量
  var oldTouchY = 0f
  var startTouchY = 0f
  var isCanFresh = true
  
  init {
    var tv = TextView(context)
    tv.height = h_header
    tv.width = width
    tv.gravity = Gravity.CENTER
    tv.setBackgroundColor(0x3300ffff.toInt())
    header = tv
    addView(tv)
  }
  
  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    header.layout(0,-h_header,width,0)
    rcv = findViewById(R.id.rcv_videos)

    rcv.layout(0,0,width,height)
    rcv.addOnScrollListener(object :RecyclerView.OnScrollListener(){
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState==1&&recyclerView.canScrollVertically(-1)){ //如果用户手指在滑但已经到顶部了
          isCanFresh = true
        }
        System.out.println("scrollstateChanged$isCanFresh");
      }
    })
    rcv.addOnItemTouchListener(object :RecyclerView.OnItemTouchListener{
      override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
      override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//        if(isCanFresh){
          when(e.action){
            0 -> {
              oldTouchY = e.y;
              startTouchY = e.y
            }
            2 -> {
//              scrollBy(0, -(e.y-oldTouchY).toInt())
              header.layout(0, (-h_header+e.y-startTouchY).toInt(),width, (-h_header+e.y-startTouchY).toInt())
              rcv.layout(0, (0+e.y-startTouchY).toInt(),width,(0+e.y-startTouchY).toInt())
              requestDisallowInterceptTouchEvent(true)
            }
            1 -> {
              header.layout(0,-h_header,0,0)
              rcv.layout(0,0,width,height)
              isCanFresh = false
            }
          }
//        }
        return false
      }
      override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        System.out.println(disallowIntercept);
      }
    })
//    rcv.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//      System.out.println(scrollY);  //一直为0
//    }
  }
}