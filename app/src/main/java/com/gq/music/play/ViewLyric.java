package com.gq.music.play;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.gq.music.event.EventPlayProgress;
import com.gq.music.event.EventSongChange;
import com.gq.music.util.Data;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ViewLyric extends View {

  float w=0,h=0;  //画布宽高
  float itemH; //每项歌词高度
  final int n = 5;  //歌词项数
  float firstLyricY; //第一句歌词位置，上为负
  float previousY; //上次触摸y
  Paint paintNormal;
  private ArrayList<DataLyric> list = new ArrayList<DataLyric>(); //歌词数组
  boolean isUserDrag = false; //用户是否拖拽中
  int progressIndex = 0; //焦点序号
  int userIndex = 0; //用户滑动时的焦点序号
  Timer timer = new Timer(); //手指离开3秒内不动
  TimerTask task;
  float previousTime; //判断点击事件

  public ViewLyric(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    EventBus.getDefault().register(this);
  }

  @Subscribe
  public void onProgress(EventPlayProgress event){
    if(!isUserDrag){
      int newIndex = progressIndex;
      for(int i=0;i<list.size();i++){
        if(event.currentSecond == list.get(i).time_second){
          newIndex = i;
          break;
        }
      }
      if (newIndex != progressIndex){
        myScrollTo(newIndex);
        progressIndex = newIndex;
      }
    }
  }

  @Subscribe
  public void onSongChange(EventSongChange e){
    progressIndex = 0;
    firstLyricY = h/2;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if(w==0){
      init();
    }
    for(int i=0;i<list.size();i++){
      drawTextCenter(w/2,firstLyricY+i*itemH,list.get(i).content,canvas,paintNormal);
    }
    if(isUserDrag==true){
      Paint paintLine = new Paint();
      paintLine.setStrokeWidth(2);
      paintLine.setColor(0x33ffffff);
      canvas.drawLine(100,h/2,w-100,h/2,paintLine);

      Paint paintIcon = new Paint();
      paintIcon.setColor(0xb3ffffff);
      paintIcon.setTextSize(30);
      drawTextCenter(w-50,h/2,list.get(userIndex).time_str,canvas,paintIcon);

      Path path = new Path();
      path.moveTo(50,h/2-20);
      path.lineTo(50,h/2+20);
      path.lineTo(50+40,h/2);
      path.close();
      canvas.drawPath(path,paintIcon);
    }
  }
  void init(){
    w = getWidth();
    h = getHeight();
    itemH = h/n;
    firstLyricY = h/2;
    LinearGradient gradient = new LinearGradient(0, 0, 0, h,
      new int[]{Color.TRANSPARENT, Color.WHITE, Color.TRANSPARENT},
      new float[]{0f,0.5f,1f},
      Shader.TileMode.CLAMP);
    paintNormal = new Paint();
    paintNormal.setAntiAlias(true);
    paintNormal.setColor(0xffffffff);
    paintNormal.setShader(gradient);
    paintNormal.setTextSize(40);
  }
  void myScrollTo(int index){
    ValueAnimator animator = ValueAnimator.ofFloat(firstLyricY,h/2-index*itemH);
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        firstLyricY = (float) animation.getAnimatedValue();
        invalidate();
      }
    });
    animator.setDuration(300);
    post(new Runnable() { //动画只能跑在主线程
      @Override
      public void run() {
        animator.start();
      }
    });
  }
  void setList(ArrayList<DataLyric> list){
    this.list = list;
    progressIndex = 0;
    userIndex = 0;
    firstLyricY = h/2;
    invalidate();
  }

  //根据中心点绘制文字
  void drawTextCenter ( float centerX, float centerY, String text,Canvas canvas, Paint paint) {
    float textWidth = paint.measureText(text);
    Paint.FontMetrics fontMetrics = paint.getFontMetrics();
    float baselineY = centerY + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
    canvas.drawText(text, centerX - textWidth / 2, baselineY, paint);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if(list.size()<5){
      return true;
    }
    switch (event.getAction()){
      case MotionEvent.ACTION_DOWN:
        if(task!=null){
          task.cancel();
        }
        getParent().requestDisallowInterceptTouchEvent(true); //禁止父控件滑动
        isUserDrag = true;
        previousY = event.getY();
        break;
      case MotionEvent.ACTION_MOVE:
        float currentY = event.getY();
        firstLyricY += currentY-previousY;
        if(firstLyricY>h/2){
          firstLyricY = h/2;
        }
        if(firstLyricY<h/2-itemH*(list.size()-1)){
          firstLyricY = h/2-itemH*(list.size()-1);
        }
        previousY = currentY;
        float offsetY = h/2 - firstLyricY;
        userIndex = offsetY%itemH>0.5 ? (int) (offsetY/itemH)+1 : (int) (offsetY/itemH);
        break;
      case MotionEvent.ACTION_UP:
        if (event.getX()>40&&event.getX()<100&&event.getY()>h/2-30&&event.getY()<h/2+30){
          Data.control.seekTo(list.get(userIndex).time_second*1000);
          progressIndex = userIndex;
        }
        myScrollTo(userIndex);
        task = new TimerTask() {
          @Override
          public void run() {
            isUserDrag = false;
            myScrollTo(progressIndex);
          }
        };
        timer.schedule(task,2000);
        break;
    }
    invalidate();
    return true;
  }
}
