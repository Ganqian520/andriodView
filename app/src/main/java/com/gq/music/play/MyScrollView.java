package com.gq.music.play;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;
import android.widget.Scroller;

public class MyScrollView extends ScrollView {

  private Scroller scroller;

  //在xml里面用的构造函数
  public MyScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
    scroller = new Scroller(getContext());
  }

  public void mScrollBy(int dx,int dy){
    scroller.startScroll(scroller.getFinalX(),scroller.getFinalY(),dx,dy,500);
    invalidate();
  }

  public void mScrollTo(int fx ,int fy){
    int dx = fx - scroller.getFinalX();
    int dy = fy - scroller.getFinalY();
    mScrollBy(dx, dy);
  }

  @Override
  public void computeScroll() {
    super.computeScroll();
    if (scroller.computeScrollOffset()) {
      scrollTo(scroller.getCurrX(), scroller.getCurrY());
      postInvalidate();
    }
  }
//
//  @Override
//  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//    super.onScrollChanged(l, t, oldl, oldt);
//    System.out.println("onscrollchanged");
//  }
}
