package com.gq.music.other.lyric3d;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomViewLyric extends View {

  private final int n = 10;//最大显示歌词条数
  private final int r = 500;//虚拟风车的半径
  private final int w = 800,h = 1000; //canvas的宽高
  private int maxSize = 45;
  private int minSize = 30;
  private float oldY; //上次滑动坐标
  private Paint paint;
  private String[] origin = new String[20];
//  private int[] indexs = {0,1,2,3,4};
  private ArrayList<Float> angles = new ArrayList<Float>();

  public CustomViewLyric(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.translate(w/2,h/2);  //将绘图原点移到画布中心，方便各种居中
    for(int i=0;i<n;i++){
      if(angles.get(i)>=0 && angles.get(i)<=1){
        paint.setTextSize(minSize+(maxSize-minSize)*Util.calArea(angles.get(i))); //明显卡顿
        paint.setColor(Color.argb((int) (Util.calArea(angles.get(i))*255),255,255,255));
        int y = (int) (-Math.cos(angles.get(i)*Math.PI)*r);
        Util.drawTextCenter(0,y,origin[i],canvas,paint);
      }
    }
  }

  void init(){
    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setTextSize(40);
//    paint.setColor(Color.WHITE);
    for(int i=0;i<n;i++){
      angles.add((float) (1.0/(n-1)*i));
    }
    for(int i=0;i<origin.length;i++){
      origin[i] = "歌词歌词歌词歌词歌词"+ i;
    }
  }

  void handleMove(MotionEvent event){
    float offAngle = (event.getY()-oldY)/(h*1);
    for(int i=0;i<n;i++){
      angles.set(i,angles.get(i)+offAngle);
    }
    if(offAngle>0 && angles.get(n-1)>1){
      for(int i=0;i<n;i++){
        angles.set(i,((float)1) / (n-1) * (i-1) );
      }
    }
    if(offAngle<0 && angles.get(0)<0){
      for(int i=0;i<n;i++){
        angles.set(i,((float)1) / (n-1) * (i+1) );
      }
    }
    invalidate();
    oldY = event.getY();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()){
      case MotionEvent.ACTION_DOWN:
        oldY = event.getY();
        break;
      case MotionEvent.ACTION_MOVE:
        handleMove(event);
        break;
      case MotionEvent.ACTION_UP:

        break;
    }
    return true;
  }
}
